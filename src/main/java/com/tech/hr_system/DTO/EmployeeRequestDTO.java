package com.tech.hr_system.DTO;

import lombok.Data;

@Data
public class EmployeeRequestDTO {
    private String fullName;
    private Double salary;
    private String departmentName;
    private String avatarUrl;
    //Không khai báo id ở đây vì đây là để Post nên không thể để ID
}
