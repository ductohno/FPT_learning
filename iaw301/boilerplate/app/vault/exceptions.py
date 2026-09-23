class VaultError(Exception):
    """Lớp ngoại lệ cơ sở cho các lỗi liên quan đến Vault."""
    pass


class VaultConnectionError(VaultError):
    """Ném ra khi không thể kết nối tới Vault hoặc Vault trả về lỗi HTTP 5xx/429."""
    pass


class CircuitBreakerOpenError(VaultError):
    """Ném ra khi Circuit Breaker đang ở trạng thái OPEN (ngắt mạch, fail-fast)."""
    pass
