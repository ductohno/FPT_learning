import sqlite3
from fastapi import FastAPI, Form, Response
from fastapi.responses import FileResponse
from fastapi.responses import HTMLResponse
import uvicorn

def init_db():
    db_name = "iaw301"

    connection = sqlite3.Connection(database=db_name)

    cursor = connection.cursor()

    cursor.execute("""
        CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            username TEXT UNIQUE NOT NULL,
            password TEXT NOT NULL
        )
    """)

    cursor.executemany("""
        INSERT OR IGNORE INTO users (username, password) VALUES (?, ?)
    """, [("admin", "123"), ("user1", "456")]
    )

    connection.commit()
    cursor.close()
    connection.close()

def is_user_true(username, password):
    db_name = "iaw301"

    connection = sqlite3.Connection(database=db_name)

    cursor = connection.cursor()

    cursor.execute(f"SELECT * FROM users WHERE username = ? and password = ?", (username, password))

    result = cursor.fetchone()
    cursor.close()
    connection.close()

    return result is not None
   
app = FastAPI(title="test")

@app.get("/ping")
def index():
    return "pong"

@app.get("/login-form")
def loginForm():
    return FileResponse("login.html")

@app.post("/login")
def login(response: Response, username: str = Form(...),  password: str = Form(...)):
    if is_user_true(username, password):
        response.set_cookie(key="username", value=username)
        return "Login thành công"
    return "Login thất bại"

@app.get("/logout")
def logout(response: Response):
    response.delete_cookie(key="username")
    return "You are logout"

@app.get("/all", response_class=HTMLResponse)
def all_users():
    connection = sqlite3.connect("iaw301")
    cursor = connection.cursor()

    cursor.execute("SELECT * FROM users")
    users = cursor.fetchall()

    connection.close()

    rows = ""

    for user in users:
        rows += f"""
        <tr>
            <td>{user[0]}</td>
            <td>{user[1]}</td>
            <td>{user[2]}</td>
        </tr>
        """

    sample = """
    <html>
    <body>
        <table border="1">
            <tr>
                <th>ID</th>
                <th>Username</th>
                <th>Password</th>
            </tr>

            {{USERS}}
        </table>
    </body>
    </html>
    """

    html = sample.replace("{{USERS}}", rows)

    return HTMLResponse(content=html)

if __name__ == "__main__":
    uvicorn.run(
        app=app,
        host="127.0.0.1",
        port=8888
    )