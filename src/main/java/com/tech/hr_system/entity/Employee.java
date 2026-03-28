package com.tech.hr_system.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity //đánh dấu đây là 1 thực thể ( sẽ biến thành table)
@Table(name = "tbl_employees")//đặt tên bảng trong database
//@Data //Tự động sinh ra Getter, Setter, ToString
@Getter
@Setter
@NoArgsConstructor//Tự động sinh Constructor rỗng
@AllArgsConstructor // tự động sinh Constructor đầy đủ tham số
public class Employee {
    @Id // Đánh dấu thuộc tính này là khóa chính
    @GeneratedValue(strategy = GenerationType.IDENTITY) // cho sql tự động tăng Id
    private Integer id;

    @Column(name="full_name")
    @NotBlank(message = "tên nhân viên không được để trống")
    private String name;

    @Min(value = 0, message = "lương không được nhỏ hơn 0")
    private double salary;

    //Thêm phần quan hệss
    //Nhiều nhân viên thuộc về 1 phòng ban
    @ManyToOne
    @JoinColumn(name="department_id") // Tạo 1 cột khóa ngoại tên department_id trong SQL
    private Department department;

    @Column(name="avatar_url")
    private String avatarUrl;
}
