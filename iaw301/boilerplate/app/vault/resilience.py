import time
from typing import Callable, Any
from app.vault.exceptions import VaultConnectionError, CircuitBreakerOpenError


class ExponentialBackoff:
    """
    ===========================================================================
    BÀI TẬP 1: EXPONENTIAL BACKOFF RETRY
    ===========================================================================
    Mục tiêu:
    Khi dịch vụ Vault bị lỗi chập chờn (VaultConnectionError), client sẽ tự động
    thử lại (retry) nhiều lần. Thời gian chờ giữa mỗi lần thử tăng theo cấp số nhân.

    Công thức tính thời gian chờ (delay) tại lần thử thứ `attempt` (bắt đầu từ 0):
        delay = min(max_delay, base_delay * (factor ** attempt))

    Ví dụ với base_delay = 0.5s, factor = 2.0, max_delay = 8.0s:
        - attempt = 0 (lần retry 1): 0.5 * (2 ** 0) = 0.5s
        - attempt = 1 (lần retry 2): 0.5 * (2 ** 1) = 1.0s
        - attempt = 2 (lần retry 3): 0.5 * (2 ** 2) = 2.0s
        - nếu kết quả vượt quá max_delay thì giới hạn lại bằng max_delay.
    """

    def __init__(
        self,
        base_delay: float = 0.5,
        max_delay: float = 8.0,
        max_retries: int = 3,
        factor: float = 2.0
    ):
        self.base_delay = base_delay
        self.max_delay = max_delay
        self.max_retries = max_retries
        self.factor = factor

    def get_delay(self, attempt: int) -> float:
        """
        TODO 1.1: Sinh viên tính toán và trả về số giây delay cho lần retry `attempt`.
        Tham số:
            attempt (int): Chỉ số lần retry, bắt đầu từ 0 (0, 1, 2, ...).
        Trả về:
            float: Thời gian delay (giây), không được vượt quá self.max_delay.
        """
        delay = self.base_delay * (self.factor ** attempt)
        return min(self.max_delay, delay)

    def execute_with_retry(self, func: Callable, *args, **kwargs) -> Any:
        """
        TODO 1.2: Thực thi hàm `func(*args, **kwargs)` kèm cơ chế retry.
        
        Quy trình:
        1. Gọi func(*args, **kwargs). Nếu thành công -> trả về kết quả ngay.
        2. Nếu gặp ngoại lệ `VaultConnectionError`:
           - Thực hiện retry tối đa `self.max_retries` lần.
           - Trong mỗi lần retry `attempt` (từ 0 đến max_retries - 1):
             + Lấy delay bằng `self.get_delay(attempt)`
             + Tạm dừng bằng `time.sleep(delay)`
             + Thử gọi lại `func(*args, **kwargs)`. Nếu thành công -> trả về kết quả.
           - Nếu sau tất cả các lần retry vẫn lỗi -> ném ra (raise) ngoại lệ cuối cùng đó.
        3. Các ngoại lệ khác không phải VaultConnectionError: ném ra ngay lập tức (không retry).
        """
        try:
            return func(*args, **kwargs)

        except VaultConnectionError:
            for attempt in range(self.max_retries):
                delay = self.get_delay(attempt)
                time.sleep(delay)

                try:
                    return func(*args, **kwargs)

                except VaultConnectionError:
                    if attempt == self.max_retries - 1:
                        raise
                    continue


class CircuitBreaker:
    """
    ===========================================================================
    BÀI TẬP 2: CIRCUIT BREAKER PATTERN (BỘ NGẮT MẠCH TỰ ĐỘNG)
    ===========================================================================
    Mục tiêu:
    Bảo vệ hệ thống khi dịch vụ Vault bị sập hoàn toàn. Nếu liên tục gặp lỗi,
    Circuit Breaker sẽ "ngắt mạch" (chuyển sang OPEN) để từ chối các request tiếp theo
    ngay lập tức (Fail-fast), tránh lãng phí thời gian retry làm nghẽn server.

    Gồm 3 trạng thái (State):
    1. "CLOSED" (Bình thường): Cho phép request đi qua.
       - Đếm số lần lỗi liên tiếp (failure_count).
       - Nếu failure_count >= failure_threshold -> chuyển sang "OPEN".

    2. "OPEN" (Đang ngắt mạch): Không cho phép request đi qua.
       - Ném ngay CircuitBreakerOpenError (Fail-fast).
       - Sau khoảng thời gian `recovery_timeout` kể từ lần lỗi cuối cùng (last_failure_time),
         chuyển trạng thái sang "HALF-OPEN" để cho 1 request thử tải lại.

    3. "HALF-OPEN" (Thử tải lại): Cho phép 1 request đi qua để thăm dò.
       - Nếu request thành công: hồi phục về "CLOSED" và reset failure_count = 0.
       - Nếu request thất bại: chuyển lại về "OPEN" và cập nhật lại last_failure_time.
    """

    def __init__(self, failure_threshold: int = 3, recovery_timeout: float = 5.0):
        self.failure_threshold = failure_threshold
        self.recovery_timeout = recovery_timeout

        self.state: str = "CLOSED"         
        self.failure_count: int = 0      
        self.last_failure_time: float = 0.0  

    def can_execute(self) -> bool:
        """
        TODO 2.1: Kiểm tra xem tại thời điểm hiện tại có được phép gửi request không.
        
        Quy tắc:
        - Nếu state == "CLOSED": return True
        - Nếu state == "OPEN":
          + Kiểm tra xem đã qua recovery_timeout chưa: (time.time() - self.last_failure_time >= self.recovery_timeout)
          + Nếu đã qua: chuyển self.state = "HALF-OPEN" và return True
          + Nếu chưa: return False
        - Nếu state == "HALF-OPEN": return True
        """
        # --- VIẾT CODE CỦA BẠN VÀO ĐÂY ---
        if self.state == "CLOSED":
            return True

        if self.state == "OPEN":
            elapsed = time.time() - self.last_failure_time

            if elapsed >= self.recovery_timeout:
                self.state = "HALF-OPEN"
                return True

            return False

        if self.state == "HALF-OPEN":
            return True

        return False

    def record_success(self):
        """
        TODO 2.2: Ghi nhận một lần gọi Vault thành công.
        
        Quy tắc:
        - Reset self.failure_count = 0
        - Chuyển self.state = "CLOSED"
        """
        # --- VIẾT CODE CỦA BẠN VÀO ĐÂY ---
        self.failure_count = 0
        self.state = "CLOSED"


    def record_failure(self):
        """
        TODO 2.3: Ghi nhận một lần gọi Vault thất bại (gặp VaultConnectionError).
        
        Quy tắc:
        - Cập nhật self.last_failure_time = time.time()
        - Nếu self.state == "HALF-OPEN":
          + Chuyển ngay self.state = "OPEN"
        - Nếu self.state == "CLOSED":
          + Tăng self.failure_count += 1
          + Nếu self.failure_count >= self.failure_threshold:
            * Chuyển self.state = "OPEN"
        """
        self.last_failure_time = time.time()

        if self.state == "HALF-OPEN":
            self.state = "OPEN"
            return

        if self.state == "CLOSED":
            self.failure_count += 1

            if self.failure_count >= self.failure_threshold:
                self.state = "OPEN"

    def call(self, func: Callable, *args, **kwargs) -> Any:
        """
        TODO 2.4: Bọc việc thực thi func(*args, **kwargs) qua Circuit Breaker.
        
        Quy trình:
        1. Gọi self.can_execute(). Nếu trả về False:
           - Ném ra CircuitBreakerOpenError("Circuit is OPEN. Fast-failing request.")
        2. Nếu được phép chạy:
           - Thử gọi func(*args, **kwargs).
           - Nếu thành công:
             + Gọi self.record_success()
             + Trả về kết quả của func.
           - Nếu gặp ngoại lệ VaultConnectionError:
             + Gọi self.record_failure()
             + Ném lại (re-raise) ngoại lệ VaultConnectionError đó.
        """
        if not self.can_execute():
            raise CircuitBreakerOpenError(
                "Circuit is OPEN. Fast-failing request."
            )

        try:
            result = func(*args, **kwargs)
            self.record_success()
            return result

        except VaultConnectionError:
            self.record_failure()
            raise
