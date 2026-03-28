package com.tech.hr_system.controller;

import com.tech.hr_system.DTO.EmployeeDTO;
import com.tech.hr_system.DTO.EmployeeRequestDTO;
import com.tech.hr_system.entity.Department;
import com.tech.hr_system.entity.Employee;
import com.tech.hr_system.service.DepartmentService;
import com.tech.hr_system.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity; // BẮT BUỘC IMPORT CÁI NÀY
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.File;
import java.io.IOException;
import java.util.List;

//Nhãn này báo cho Spring Boot biết đây là nơi nhận yêu cầu từ web, đây là 1 API Controller + return JSON
@RestController
@RequestMapping("/api/employees") // gom chung đường dẫn gốc vào đây cho gọn
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService; // tiêm Service vào

    @Autowired
    private DepartmentService departmentService;

    //trả về danh sách DTO
    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getEmployees()
    {
        List<EmployeeDTO> dtoList = employeeService.getAllEmployees();
        if(!dtoList.isEmpty())
        {
            return ResponseEntity.ok(dtoList);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    //@PathVariable dùng để khi bạn biết chính xác đối tượng bạn muốn tìm + lấy value từ URL
    public ResponseEntity<EmployeeDTO> findById(@PathVariable Integer id)
    {
        Employee employee = employeeService.getEmployeeById(id);
        EmployeeDTO dto = employeeService.mapToDTO(employee);
        if(employee!=null)
        {
            return ResponseEntity.ok(dto); // trả về 200 OK kèm thông tin
        }
        return ResponseEntity.notFound().build(); //trả về lỗi 404 not found
    }

    @GetMapping("/search")
    public ResponseEntity<List<EmployeeDTO>> searchNameContaining(@RequestParam("keyword") String keyWord)
    {
        List<Employee> containName = employeeService.searchNameContaining(keyWord);
        List<EmployeeDTO> dto = containName.stream()
                .map(employee -> employeeService.mapToDTO(employee))
                .toList();
        if(!containName.isEmpty())
        {
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search-salary")
    public ResponseEntity<List<EmployeeDTO>> searchSalaryGreater (@RequestParam("min") Double salary)
    {
        List<Employee> findSalary = employeeService.searchSalaryGreater(salary);
        List<EmployeeDTO> dto = findSalary.stream()
                .map(employee -> employeeService.mapToDTO(employee))
                .toList();
        if(!findSalary.isEmpty())
        {
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.noContent().build()
;    }

    @GetMapping("/filter")
    public ResponseEntity<List<EmployeeDTO>> searchNameandSalary (@RequestParam String name,@RequestParam Double min)
    {
        List<Employee> filter = employeeService.searchNameAndSalary(name,min);
        if(!filter.isEmpty())
        {
//            1. Đưa vào dây chuyền ép thành DTO
            List<EmployeeDTO> dtoList = filter.stream()
                    .map(employee -> employeeService.mapToDTO(employee))
                    .toList();

//            2. Trả về danh sách DTO
            return ResponseEntity.ok(dtoList);
        }
        return ResponseEntity.noContent().build();
    }

    // API MỚI: Phân trang
    // URL mẫu: GET http://localhost:8080/api/employees/page?page=0&size=5
    @GetMapping("/page")
    public ResponseEntity<Page<EmployeeDTO>> getEmployeesWithPagination(
            @RequestParam(defaultValue = "0") int page, // Mặc định là trang đầu tiên (trang 0)
            @RequestParam(defaultValue = "5") int size  // Mặc định lấy 5 người mỗi trang
    ) {
        Page<EmployeeDTO> employeePage = employeeService.getEmployeesWithPagination(page, size);
        return ResponseEntity.ok(employeePage);
    }

    // API thêm nhân viên
    @PostMapping
    // Chỉ ADMIN mới được thêm nhân viên
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addEmployee(@Valid @RequestBody EmployeeRequestDTO requestDTO) {

        // 1. Tạo entity mới ( ID là null)
        Employee employee = new Employee();
        employee.setName(requestDTO.getFullName()); // Dùng getFullName() theo đúng DTO của bạn
        employee.setSalary(requestDTO.getSalary());
        employee.setAvatarUrl(requestDTO.getAvatarUrl());

        // 2. Tìm phòng ban
        Department dept = departmentService.findDepartment(requestDTO.getDepartmentName());
        employee.setDepartment(dept);

        //khi lưu xuống DB sẽ tự sinh ID và gán vào đối tượng Employee
        Employee saveEmployee = employeeService.saveEmployee(employee);

        //khi trả về thì chuyển sang EmployeeDTO (có chứa ID) để người dùng biết ID
        EmployeeDTO responseDTO = employeeService.mapToDTO(saveEmployee);

        return ResponseEntity.ok(responseDTO);

    }
//    // API MỚI: Upload ảnh đại diện
//    // URL: POST http://localhost:8080/api/employees/upload-avatar
//    @PostMapping(value = "/upload-avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
//        // Kiểm tra xem khách có gửi file rỗng không
//        if (file.isEmpty()) {
//            return ResponseEntity.badRequest().body("Lỗi: Bạn chưa chọn file nào!");
//        }
//
//        try {
//            // 1. Lấy tên file gốc do người dùng upload (ví dụ: anh-the.jpg)
//            String fileName = file.getOriginalFilename();
//
//            // 2. Chỉ định thư mục lưu file trên máy của bạn
//            // LƯU Ý: Đổi đường dẫn này thành thư mục có thật trên máy bạn nhé!
//            String uploadDir = "D:/uploads/";
//
//            // Nếu thư mục chưa tồn tại thì tự động tạo mới
//            File directory = new File(uploadDir);
//            if (!directory.exists()) {
//                directory.mkdirs();
//            }
//
//            // 3. Ghép thành đường dẫn tuyệt đối
//            String filePath = uploadDir + fileName;
//
//            // 4. Lệnh "phép thuật": Lưu file vào ổ cứng
//            file.transferTo(new File(filePath));
//
//            return ResponseEntity.ok("Đã tải ảnh lên thành công tại: " + filePath);
//
//        } catch (IOException e) {
//            return ResponseEntity.internalServerError().body("Lỗi khi lưu ảnh: " + e.getMessage());
//        }
//    }

//    Nâng cấp API upload ảnh
@PostMapping(value = "/{id}/upload-avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAvatar(
            @PathVariable("id") Integer Id, // Lấy id từ đường dẫn
            @RequestParam("file") MultipartFile file) {
        // Kiểm tra xem khách có gửi file rỗng không
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Lỗi: Bạn chưa chọn file nào!");
        }

        try {
            // 1. Lấy tên file gốc do người dùng upload (ví dụ: anh-the.jpg)
            String fileName = file.getOriginalFilename();

            // 2. Chỉ định thư mục lưu file trên máy của bạn
            // LƯU Ý: Đổi đường dẫn này thành thư mục có thật trên máy bạn nhé!
            String uploadDir = "D:/uploads/";

            // Nếu thư mục chưa tồn tại thì tự động tạo mới
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 3. Ghép thành đường dẫn tuyệt đối
            String filePath = uploadDir + fileName;

            // 4. Lệnh "phép thuật": Lưu file vào ổ cứng
            file.transferTo(new File(filePath));

            //5. Tìm nhân viên và cập nhật DB
            //Gọi Service để tìm nhân viên theo ID
            Employee employee = employeeService.getEmployeeById(Id);
            if(employee != null)
            {
                employee.setAvatarUrl(fileName);
                employeeService.saveEmployee(employee);
                return ResponseEntity.ok("Đã tải ảnh lên thành công và cập nhật hồ sơ thành công ");
            }
            else
            {
                return ResponseEntity.badRequest().body("Lưu ảnh thành công nhưng không tìm thấy nhân viên ID: "+Id);
            }

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Lỗi khi lưu ảnh: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Integer id)
    {
        Boolean isDeleted = employeeService.deleteEmployee(id);
        if(isDeleted)
        {
            return  ResponseEntity.ok("Đã xóa thành công ID: "+id);
        }
        //trả về 404 kèm câu thông báo
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy nhân viên có ID: "+id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateEmployee(@PathVariable Integer id,@Valid @RequestBody EmployeeDTO employeeDTO)
    {
        //1. Tìm nhân viên trong DB xem có tồn tại không
        Employee existingEmployee = employeeService.getEmployeeById(id);
        if(existingEmployee ==null)
        {
            return ResponseEntity.notFound().build();//báo lỗi 404 nếu không tìm thấy
        }

        //2. Cập nhật các thông tin từ DTO sang Entity
        existingEmployee.setName(employeeDTO.getFullName());
        existingEmployee.setSalary(employeeDTO.getSalary());
        existingEmployee.setAvatarUrl(employeeDTO.getAvatarUrl());

//        3. Xử lý phòng ban
       String deptName = employeeDTO.getDepartmentName();
       if(deptName != null && !deptName.trim().isEmpty())
       {
           Department department = departmentService.findDepartment(deptName);
           if (department == null) {
               return ResponseEntity.badRequest().body("Lỗi: Không tìm thấy phòng ban mang tên '" + deptName + "'");
           }
           existingEmployee.setDepartment(department);
       }
        // 4. Lưu nhân viên đã được cập nhật xuống Database
        Employee updatedEmployee = employeeService.saveEmployee(existingEmployee);

        // 5. Đóng gói lại thành DTO để trả về cho giao diện
        EmployeeDTO responseDTO = employeeService.mapToDTO(updatedEmployee);

        return ResponseEntity.ok(responseDTO);
    }

    //API cập nhật phòng ban cho nhân viên
    // URL mẫu: PUT http://localhost:8080/api/employees/3/department/1
    @PutMapping("/{empId}/department/{deptId}")
    public ResponseEntity<String> assignDerpartment(@PathVariable Integer empId, @PathVariable Integer deptId)
    {
//        1. Thực hiện chuyển phòng ban
        Employee updateEmp = employeeService.assignDepartment(empId,deptId);
//        2. Kiểm tra null
        if(updateEmp!=null)
        {
            return ResponseEntity.ok("Đã chuyển nhân viên vào phòng ban thành công");
        }
        return ResponseEntity.badRequest().body("Lỗi: không tìm thấy nhân viên hoặc phòng ban");
    }


}