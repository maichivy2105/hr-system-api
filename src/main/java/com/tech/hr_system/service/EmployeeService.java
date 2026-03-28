package com.tech.hr_system.service;

import com.tech.hr_system.DTO.EmployeeDTO;
import com.tech.hr_system.entity.Department;
import com.tech.hr_system.entity.Employee;
import com.tech.hr_system.repository.DepartmentRepository;
import com.tech.hr_system.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service // Nhãn dán quan trọng để Spring Boot biết đây là lớp xử lý Logic (bean)
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

//    //Lấy danh sách nhân viên
//    public List<Employee> getAllEmployees()
//    {
//        return employeeRepository.findAll();
//    }

    //Lấy danh sách nhân viên (nâng cấp)
    public List<EmployeeDTO> getAllEmployees() {
        //lấy danh sách thô từ DB
        List<Employee> employees = employeeRepository.findAll();

        //Sử dụng Java 8 Stream API: code vòng lặp
        return employees.stream()
                .map(this::mapToDTO) // gọi hàm đóng gói cho từng người
                .toList(); // gom lại thành 1 danh sách mới
    }

    // HÀM MỚI: Lấy danh sách nhân viên có phân trang
    public Page<EmployeeDTO> getEmployeesWithPagination(int pageNo, int pageSize) {
        // Tạo yêu cầu phân trang (Trang số mấy, mỗi trang bao nhiêu người)
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        // Lấy dữ liệu thô từ Database (Lúc này nó trả về đối tượng Page thay vì List)
        Page<Employee> employeePage = employeeRepository.findAll(pageable);

        // Hô biến từng Employee thành EmployeeDTO cực kỳ ngắn gọn
        return employeePage.map(this::mapToDTO);
    }

    //Tìm nhân viên theo ID
    public Employee getEmployeeById(Integer id) {return employeeRepository.findById(id).orElse(null);}

    //Thêm nhân viên
    public Employee saveEmployee(Employee employee)
    {
        return employeeRepository.save(employee);
    }

    //Nâng cấp xóa nhân viên, trả về true nếu thấy, false nếu không tìm thấy
    public Boolean deleteEmployee(Integer id)
    {
        if(employeeRepository.existsById(id))
        {
            employeeRepository.deleteById(id);
            return true;
        }
        return false;
    }

    //Cập nhật nhân viên
    public Employee updateEmployee(Integer id, Employee newInfo)
    {
        Employee oldInfo = employeeRepository.findById(id).orElse(null);
        if(oldInfo != null)
        {
            oldInfo.setName(newInfo.getName());
            oldInfo.setSalary(newInfo.getSalary());
            return employeeRepository.save(oldInfo);
        }
        return null;
    }

    public List<Employee> searchNameContaining(String keyWord)
    {
        return employeeRepository.findByNameContaining(keyWord);
    }

    public List<Employee> searchSalaryGreater (Double salary)
    {
        return employeeRepository.findBySalaryGreaterThan(salary);
    }

    public List<Employee> searchNameAndSalary (String name, Double salary)
    {
        return employeeRepository.findByNameContainingAndSalaryGreaterThan(name,salary);
    }

    //Hàm đóng gói Mapping
    //Chuyển đổi Entity thành DTO
    public EmployeeDTO mapToDTO(Employee employee)
    {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(employee.getId());
        dto.setFullName(employee.getName());
        dto.setSalary(employee.getSalary());
        dto.setAvatarUrl(employee.getAvatarUrl());

        //kiểm tra nếu nhân viên có phòng ban thì mới lấy phòng ban
        if(employee.getDepartment() !=null)
        {
            dto.setDepartmentName(employee.getDepartment().getName());
        }
        else
        {
            dto.setDepartmentName("Chưa phân bổ");
        }
        return dto;
    }

    // Hàm chuyển phòng ban
    public Employee assignDepartment(Integer empId, Integer deptId)
    {
        //Tìm nhân viên và phòng ban trong DB
        Employee emp = employeeRepository.findById(empId).orElse(null);
        Department dept = departmentRepository.findById(deptId).orElse(null);
        //Nếu tìm thấy cả 2
        if(emp !=null&&dept!=null )
        {
            emp.setDepartment(dept);
            return employeeRepository.save(emp);
        }
        return null;
    }
}
