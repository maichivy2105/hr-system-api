package com.tech.hr_system.controller;

import com.tech.hr_system.DTO.DepartmentDTO;
import com.tech.hr_system.entity.Department;
import com.tech.hr_system.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<DepartmentDTO> createDepartment(@RequestBody DepartmentDTO departmentDTO)
    {
        // 1. Chuyển từ DTO sang Entity
        Department department = new Department();
        department.setName(departmentDTO.getName());

        // 2. Lưu xuống DB
        Department savedDept = departmentService.createDepartment(department); // Giả sử bạn có hàm này

        // 3. Ép ngược lại thành DTO để trả về
        DepartmentDTO responseDTO = new DepartmentDTO();
        responseDTO.setId(savedDept.getId());
        responseDTO.setName(savedDept.getName());

        return ResponseEntity.ok(responseDTO);
    }
}
