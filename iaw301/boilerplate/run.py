import uvicorn

if __name__ == "__main__":
    print("=" * 60)
    print("Khởi động FastAPI + SQLite Boilerplate Server...")
    print("Truy cập trang đăng nhập tại: http://127.0.0.1:8000/login-form")
    print("Kiểm tra ping tại:          http://127.0.0.1:8000/ping")
    print("Tài khoản mặc định:         admin / admin123 (hoặc admin@example.com)")
    print("=" * 60)
    uvicorn.run("app.main:app", host="127.0.0.1", port=8000, reload=True)
