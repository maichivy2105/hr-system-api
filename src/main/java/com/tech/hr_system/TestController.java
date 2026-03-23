package com.tech.hr_system;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import java.util.List;

@RestController
public class TestController {

    // ĐÂY CHÍNH LÀ DI (DEPENDENCY INJECTION) TRONG TRUYỀN THUYẾT!
    // Bạn không cần viết: EmployeeRepository repo = new EmployeeRepository();
    // Chữ @Autowired sẽ nhờ Spring Boot tự động lấy cái Bean Repository tiêm thẳng vào đây cho bạn xài.
    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping("/api/employees")
    public List<Employee> getEmployees() {
        // Xóa sạch cái ArrayList cũ đi.
        // Gọi thẳng hàm findAll() của Repository. Nó sẽ chạy lệnh "SELECT * FROM tbl_employees" dưới SQL Server.
        return employeeRepository.findAll();

    }

    // API THÊM NHÂN VIÊN MỚI
    @PostMapping("/api/employees")
    public Employee addEmployee(@RequestBody Employee newEmployee) {
        // Chiếc nhãn @RequestBody sẽ tự động "hứng" cục JSON bạn gửi lên
        // và biến nó thành Object Employee (nhưng chưa có ID).

        // Nhờ Repository lưu thẳng xuống SQL Server.
        // Sau khi lưu xong, SQL Server sẽ cấp ID và trả về Object hoàn chỉnh!
        return employeeRepository.save(newEmployee);
    }

    // API XÓA NHÂN VIÊN
    @DeleteMapping("/api/employees/{id}")
    public String deleteEmployee(@PathVariable Integer id) {
        // Nhờ Repository dùng câu lệnh SQL DELETE có sẵn để xóa theo ID
        employeeRepository.deleteById(id);

        return "Đã đuổi việc thành công nhân viên có ID: " + id;
    }

    // API SỬA THÔNG TIN NHÂN VIÊN
    @PutMapping("/api/employees/{id}")
    public Employee updateEmployee(@PathVariable Integer id, @RequestBody Employee newInfo) {

        // 1. Tìm nhân viên cũ trong Database dựa vào ID
        // Hàm findById trả về kiểu Optional (có thể có hoặc không), ta dùng orElse(null) để lấy giá trị hoặc trả về null nếu không thấy.
        Employee oldEmployee = employeeRepository.findById(id).orElse(null);

        // 2. Nếu tìm thấy nhân viên đó
        if (oldEmployee != null) {
            // Cập nhật tên và lương mới từ gói hàng JSON (newInfo) đè lên dữ liệu cũ
            oldEmployee.setName(newInfo.getName());
            oldEmployee.setSalary(newInfo.getSalary());

            // 3. Lưu lại xuống Database.
            // Điều kỳ diệu của hàm save(): Nếu Object chưa có ID -> nó tự hiểu là Thêm mới (INSERT).
            // Nếu Object ĐÃ CÓ ID -> nó tự hiểu là Sửa (UPDATE)!
            return employeeRepository.save(oldEmployee);
        }

        // Trả về null nếu gõ sai ID không tồn tại
        return null;
    }
}