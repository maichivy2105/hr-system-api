package com.tech.hr_system.repository;
import com.tech.hr_system.DTO.EmployeeDTO;
import com.tech.hr_system.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository // Đánh dấu đây là một Bean đảm nhận việc nói chuyện với Database
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    // Để trống hoàn toàn! Bạn không cần viết thêm 1 dòng code nào ở đây cả.
    // Integer ở trên đại diện cho kiểu dữ liệu của Khóa chính (id của bạn int).

    // 2. Tìm nhân viên mà tên có chứa từ khóa (Giống câu lệnh LIKE %abc% trong SQL)
    List<Employee> findByNameContaining(String keyword);

//    // 3. Tìm nhân viên theo phòng ban
//    List<Employee> findByDepartment(String department);

    // 4. Tìm nhân viên có lương cao hơn một mức nào đó
    List<Employee> findBySalaryGreaterThan(Double salary);

    //5. Tìm tên nhân viên theo tên và lương
    List<Employee> findByNameContainingAndSalaryGreaterThan(String name, Double salary);
}