package com.tech.hr_system.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DepartmentDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) // Vẫn dùng tuyệt chiêu giấu ID khi POST
    private Integer id;

    private String name;

    // Tuyệt đối KHÔNG khai báo List<Employee> ở đây!
}
