import hashlib
import os
import sqlite3
from typing import Optional, Dict, Any

DB_FILE = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "database.sqlite")

def get_db_connection() -> sqlite3.Connection:
    conn = sqlite3.connect(DB_FILE)
    conn.row_factory = sqlite3.Row
    return conn

def hash_password(password: str, salt: Optional[str] = None) -> str:
    """Hash password sử dụng PBKDF2 HMAC SHA-256 từ standard library."""
    if not salt:
        salt = os.urandom(16).hex()
    pwd_hash = hashlib.pbkdf2_hmac(
        "sha256",
        password.encode("utf-8"),
        salt.encode("utf-8"),
        iterations=100_000
    ).hex()
    return f"{salt}${pwd_hash}"

def verify_password(plain_password: str, hashed_password: str) -> bool:
    """Kiểm tra mật khẩu khớp với hash đã lưu."""
    try:
        salt, expected_hash = hashed_password.split("$", 1)
        test_hash = hashlib.pbkdf2_hmac(
            "sha256",
            plain_password.encode("utf-8"),
            salt.encode("utf-8"),
            iterations=100_000
        ).hex()
        return test_hash == expected_hash
    except Exception:
        return False

def init_db():
    """Khởi tạo cấu trúc bảng SQLite và seed dữ liệu mẫu ban đầu."""
    with get_db_connection() as conn:
        cursor = conn.cursor()
        cursor.execute("""
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                email TEXT UNIQUE NOT NULL,
                password_hash TEXT NOT NULL,
                full_name TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """)
        
        # Kiểm tra xem đã có dữ liệu mẫu chưa
        cursor.execute("SELECT COUNT(*) as count FROM users")
        if cursor.fetchone()["count"] == 0:
            sample_users = [
                ("admin", "admin@example.com", hash_password("admin123"), "Quản trị viên"),
                ("user", "user@example.com", hash_password("user123"), "Người dùng thử nghiệm")
            ]
            cursor.executemany("""
                INSERT INTO users (username, email, password_hash, full_name)
                VALUES (?, ?, ?, ?)
            """, sample_users)
            conn.commit()

def get_user_by_login(identifier: str) -> Optional[Dict[str, Any]]:
    """Tìm user bằng username HOẶC email (case-insensitive)."""
    with get_db_connection() as conn:
        cursor = conn.cursor()
        cursor.execute("""
            SELECT * FROM users 
            WHERE LOWER(username) = LOWER(?) OR LOWER(email) = LOWER(?)
        """, (identifier.strip(), identifier.strip()))
        row = cursor.fetchone()
        return dict(row) if row else None

def get_user_by_id(user_id: int) -> Optional[Dict[str, Any]]:
    """Lấy thông tin user bằng ID."""
    with get_db_connection() as conn:
        cursor = conn.cursor()
        cursor.execute("SELECT * FROM users WHERE id = ?", (user_id,))
        row = cursor.fetchone()
        return dict(row) if row else None
