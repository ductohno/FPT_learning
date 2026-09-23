# Thực hành: Resilience Patterns trong Tương tác Vault

Tài liệu hướng dẫn triển khai hai mẫu thiết kế chịu lỗi (Resilience Patterns) trong kiến trúc hệ thống phân tán khi tích hợp hệ thống quản lý khóa bí mật (Secret Vault):
1. **Exponential Backoff Retry**: Kiểm soát tần suất thử lại khi dịch vụ đích gặp sự cố tạm thời.
2. **Circuit Breaker**: Cơ chế ngắt mạch tự động nhằm ngăn chặn hiện tượng quá tải lan truyền (Cascading Failure).

---

## Yêu cầu Triển khai

Toàn bộ mã nguồn cần thực hiện được giới hạn trong file:
`app/vault/resilience.py`

Yêu cầu hoàn thiện các phương thức được định danh `TODO` thuộc hai lớp sau:

### Lớp `ExponentialBackoff` (5.0 điểm)
- **`get_delay(attempt)`**: Tính toán thời gian trễ theo hàm mũ:
  $$\text{delay} = \min(\text{max\_delay}, \text{base\_delay} \times (\text{factor}^{\text{attempt}}))$$
- **`execute_with_retry(func, *args, **kwargs)`**: Thực thi hàm mục tiêu. Khi xuất hiện ngoại lệ `VaultConnectionError`, tiến hành tạm dừng theo thời gian trễ và thử lại tối đa `max_retries` lần.

### Lớp `CircuitBreaker` (5.0 điểm)
Mô hình hóa máy trạng thái hữu hạn với ba trạng thái: `CLOSED`, `OPEN`, `HALF-OPEN`:
- **`can_execute()`**:
  - `CLOSED`: Cho phép thực thi (`True`).
  - `OPEN`: Sau khoảng thời gian `recovery_timeout` tính từ lần lỗi gần nhất, chuyển trạng thái sang `HALF-OPEN` và cho phép thực thi (`True`); ngược lại từ chối (`False`).
  - `HALF-OPEN`: Cho phép thực thi thử nghiệm (`True`).
- **`record_success()`**: Thiết lập lại `failure_count = 0` và chuyển về trạng thái `CLOSED`.
- **`record_failure()`**: Ghi nhận thời điểm lỗi. Chuyển sang trạng thái `OPEN` nếu đang ở `HALF-OPEN` hoặc khi số lỗi liên tiếp đạt ngưỡng `failure_threshold`.
- **`call(func, *args, **kwargs)`**:
  - Khi `can_execute() == False`: Khởi tạo ngoại lệ `CircuitBreakerOpenError` (Fail-fast).
  - Khi thực thi thành công: Kích hoạt `record_success()` và trả về giá trị kết quả.
  - Khi phát sinh `VaultConnectionError`: Kích hoạt `record_failure()` và tái phát sinh (re-raise) ngoại lệ.
