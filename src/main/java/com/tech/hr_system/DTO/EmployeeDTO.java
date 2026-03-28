package com.tech.hr_system.DTO;
import com.fasterxml.jackson.annotation.JsonPropertyOrder; // để ép Json theo đúng thứ tự mong muốn
import jakarta.persistence.Column;
import lombok.Setter;
import lombok.Getter;

@Setter
@Getter
//Ép Json theo thứ tự mong muốn
@JsonPropertyOrder({"id","fullName","salary","departmentName","avatarUrl"})
public class EmployeeDTO {
    private Integer id;
    private String fullName;
    private Double salary;

    //thay vì trả nguyên cục Department, ta chỉ trả 1 dòng chữ
    private String departmentName;

    private String avatarUrl;
}
