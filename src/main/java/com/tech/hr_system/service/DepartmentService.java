package com.tech.hr_system.service;


import com.tech.hr_system.entity.Department;
import com.tech.hr_system.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    public Department createDepartment(Department department)
    {
        return departmentRepository.save(department);
    }

    public Department findDepartment(String name)
    {
        return departmentRepository.findByName(name);
    }
}
