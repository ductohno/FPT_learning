import os
from typing import Optional
from fastapi import FastAPI, Request, Form, Response, status
from fastapi.responses import HTMLResponse, RedirectResponse, PlainTextResponse, JSONResponse
from fastapi.staticfiles import StaticFiles
from fastapi.templating import Jinja2Templates

from app.database import (
    init_db,
    get_user_by_login,
    get_user_by_id,
    verify_password
)

BASE_DIR = os.path.dirname(os.path.abspath(__file__))

app = FastAPI(
    title="FastAPI + SQLite Boilerplate",
    description="Boilerplate với FastAPI, Uvicorn, SQLite theo sơ đồ bảng trắng.",
    version="1.0.0"
)

# Khởi tạo SQLite database khi khởi động
init_db()

# Mount static files & Jinja2 templates
app.mount("/static", StaticFiles(directory=os.path.join(BASE_DIR, "static")), name="static")
templates = Jinja2Templates(directory=os.path.join(BASE_DIR, "templates"))

SESSION_COOKIE_NAME = "session_user_id"


@app.get("/")
def index():
    """Chuyển hướng trang chủ về form đăng nhập."""
    return RedirectResponse(url="/login-form", status_code=status.HTTP_302_FOUND)


@app.get("/ping", response_class=PlainTextResponse)
def ping():
    """
    Endpoint /ping -> 'pong' theo sơ đồ bảng trắng.
    """
    return "pong"


@app.get("/login-form", response_class=HTMLResponse)
def login_form(
    request: Request,
    error: Optional[str] = None,
    message: Optional[str] = None
):
    """
    Hiển thị giao diện form đăng nhập (username/email và password).
    Nếu đã đăng nhập, chuyển tiếp tới dashboard.
    """
    session_user_id = request.cookies.get(SESSION_COOKIE_NAME)
    if session_user_id:
        try:
            user = get_user_by_id(int(session_user_id))
            if user:
                return templates.TemplateResponse(
                    request=request,
                    name="dashboard.html",
                    context={"user": user}
                )
        except (ValueError, TypeError):
            pass

    return templates.TemplateResponse(
        request=request,
        name="login.html",
        context={
            "error": error,
            "message": message,
            "identifier": ""
        }
    )


@app.post("/login")
async def login(
    request: Request,
    response: Response,
    identifier: Optional[str] = Form(None),
    password: Optional[str] = Form(None)
):
    """
    Xử lý xác thực đăng nhập:
    - Hỗ trợ cả Web Form (POST Form Data) lẫn JSON API request.
    - Đăng nhập bằng Username hoặc Email.
    - Kết quả: 'login thành công' hoặc 'login thất bại'.
    """
    # Hỗ trợ lấy dữ liệu nếu client gửi dạng application/json
    is_json = request.headers.get("content-type", "").startswith("application/json")
    if is_json:
        try:
            json_body = await request.json()
            identifier = json_body.get("identifier") or json_body.get("username") or json_body.get("email")
            password = json_body.get("password")
        except Exception:
            return JSONResponse(
                status_code=status.HTTP_400_BAD_REQUEST,
                content={"status": "error", "message": "JSON body không hợp lệ"}
            )

    identifier = (identifier or "").strip()
    password = password or ""

    if not identifier or not password:
        err_msg = "Vui lòng nhập đầy đủ tài khoản (username/email) và mật khẩu!"
        if is_json:
            return JSONResponse(
                status_code=status.HTTP_400_BAD_REQUEST,
                content={"status": "error", "message": err_msg}
            )
        return templates.TemplateResponse(
            request=request,
            name="login.html",
            context={"error": err_msg, "identifier": identifier},
            status_code=status.HTTP_400_BAD_REQUEST
        )

    # Kiểm tra xác thực qua SQLite
    user = get_user_by_login(identifier)
    if not user or not verify_password(password, user["password_hash"]):
        # Login thất bại
        err_msg = "Đăng nhập thất bại: Tên người dùng/email hoặc mật khẩu không chính xác!"
        if is_json:
            return JSONResponse(
                status_code=status.HTTP_401_UNAUTHORIZED,
                content={"status": "error", "message": err_msg}
            )
        return templates.TemplateResponse(
            request=request,
            name="login.html",
            context={"error": err_msg, "identifier": identifier},
            status_code=status.HTTP_401_UNAUTHORIZED
        )

    # Login thành công
    if is_json:
        resp = JSONResponse(
            status_code=status.HTTP_200_OK,
            content={
                "status": "success",
                "message": "Đăng nhập thành công!",
                "user": {
                    "id": user["id"],
                    "username": user["username"],
                    "email": user["email"],
                    "full_name": user["full_name"]
                }
            }
        )
        resp.set_cookie(key=SESSION_COOKIE_NAME, value=str(user["id"]), httponly=True)
        return resp

    # Trả về trang dashboard khi đăng nhập thành công qua form
    html_resp = templates.TemplateResponse(
        request=request,
        name="dashboard.html",
        context={"user": user}
    )
    html_resp.set_cookie(key=SESSION_COOKIE_NAME, value=str(user["id"]), httponly=True)
    return html_resp


@app.get("/logout")
@app.post("/logout")
def logout(request: Request):
    """
    Đăng xuất: xóa cookie phiên làm việc và chuyển về /login-form.
    """
    is_json = request.headers.get("content-type", "").startswith("application/json")
    if is_json:
        resp = JSONResponse(content={"status": "success", "message": "Đã đăng xuất thành công"})
        resp.delete_cookie(key=SESSION_COOKIE_NAME)
        return resp

    resp = RedirectResponse(url="/login-form?message=Đã+đăng+xuất+thành+công", status_code=status.HTTP_302_FOUND)
    resp.delete_cookie(key=SESSION_COOKIE_NAME)
    return resp
