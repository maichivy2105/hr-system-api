package com.tech.hr_system;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Đánh dấu đây là một Bean đảm nhận việc nói chuyện với Database
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    // Để trống hoàn toàn! Bạn không cần viết thêm 1 dòng code nào ở đây cả.
    // Integer ở trên đại diện cho kiểu dữ liệu của Khóa chính (id của bạn là int).
}