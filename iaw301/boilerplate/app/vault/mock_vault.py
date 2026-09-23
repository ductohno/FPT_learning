from typing import Dict, Any, Optional
from app.vault.exceptions import VaultConnectionError


class MockVaultServer:
    """
    Mock Service giả lập một máy chủ HashiCorp Vault (Key-Value Store).
    
    Cung cấp các chế độ vận hành để sinh viên kiểm thử resilience patterns:
    - HEALTHY: Hoạt động bình thường, trả về secret ngay lập tức.
    - FLAKY: Chập chờn, sẽ bị lỗi N lần đầu (để test Exponential Backoff retry).
    - DOWN: Sập hoàn toàn (để test Circuit Breaker ngắt mạch).
    """

    def __init__(self):
        # Kho lưu trữ secret giả lập
        self._secrets: Dict[str, Dict[str, Any]] = {
            "secret/auth": {
                "pepper": "hashicorp-vault-pepper-key-2026",
                "jwt_secret": "super-secure-jwt-signing-secret"
            }
        }
        self.mode: str = "HEALTHY"  # HEALTHY | FLAKY | DOWN
        self.flaky_failures_remaining: int = 0
        self.total_requests: int = 0
        self.failed_requests: int = 0

    def set_healthy(self):
        """Chuyển Vault về trạng thái hoạt động bình thường."""
        self.mode = "HEALTHY"
        self.flaky_failures_remaining = 0

    def set_flaky(self, fail_times: int = 2):
        """Cấu hình Vault sẽ bị lỗi fail_times lần, sau đó tự hồi phục."""
        self.mode = "FLAKY"
        self.flaky_failures_remaining = fail_times

    def set_down(self):
        """Cấu hình Vault sập hoàn toàn (luôn ném lỗi)."""
        self.mode = "DOWN"

    def read_secret(self, path: str) -> Dict[str, Any]:
        """
        Đọc secret tại đường dẫn path.
        Ném ra VaultConnectionError nếu Vault đang DOWN hoặc đang trong lượt lỗi của FLAKY.
        """
        self.total_requests += 1

        if self.mode == "DOWN":
            self.failed_requests += 1
            raise VaultConnectionError(f"Vault Connection Refused (503 Service Unavailable): Path '{path}'")

        if self.mode == "FLAKY" and self.flaky_failures_remaining > 0:
            self.flaky_failures_remaining -= 1
            self.failed_requests += 1
            raise VaultConnectionError(
                f"Vault Transient Error (500 Internal Glitch): Path '{path}'. "
                f"Remaining glitches: {self.flaky_failures_remaining}"
            )

        if path not in self._secrets:
            return {}

        return self._secrets[path]
