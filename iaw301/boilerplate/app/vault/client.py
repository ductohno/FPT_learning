from typing import Optional, Dict, Any
from app.vault.mock_vault import MockVaultServer
from app.vault.resilience import ExponentialBackoff, CircuitBreaker
from app.vault.exceptions import VaultError, CircuitBreakerOpenError


class ResilientVaultClient:
    """
    Client tương tác với Vault được tích hợp sẵn 2 cơ chế phòng vệ:
    1. Circuit Breaker: Ngắt mạch bảo vệ hệ thống khi Vault sập.
    2. Exponential Backoff: Tự động retry khi Vault chập chờn.
    """

    def __init__(
        self,
        vault_server: Optional[MockVaultServer] = None,
        backoff: Optional[ExponentialBackoff] = None,
        circuit_breaker: Optional[CircuitBreaker] = None
    ):
        self.server = vault_server or MockVaultServer()
        self.backoff = backoff or ExponentialBackoff(base_delay=0.1, max_delay=1.0, max_retries=3)
        self.circuit_breaker = circuit_breaker or CircuitBreaker(failure_threshold=3, recovery_timeout=3.0)

    def get_secret(self, path: str) -> Dict[str, Any]:
        """
        Lấy secret từ Vault qua bộ bọc Circuit Breaker + Exponential Backoff.
        """
        def _fetch():
            return self.backoff.execute_with_retry(self.server.read_secret, path)

        return self.circuit_breaker.call(_fetch)

    def get_auth_pepper(self, fallback: str = "default-local-fallback-pepper") -> str:
        """
        Lấy pepper key phục vụ hash mật khẩu trong hệ thống auth.
        Nếu Circuit Breaker bị OPEN hoặc Vault lỗi hoàn toàn, có thể dùng fallback để hệ thống không chết.
        """
        try:
            data = self.get_secret("secret/auth")
            return data.get("pepper", fallback)
        except (VaultError, NotImplementedError):
            return fallback


# Khởi tạo một singleton client dùng chung cho ứng dụng
vault_client = ResilientVaultClient()
