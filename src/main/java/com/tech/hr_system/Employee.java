package com.tech.hr_system;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

@Entity //đánh dấu đây là 1 thực thể ( sẽ biến thành table)
@Table(name = "tbl_employees")//đặt tên bảng trong database
@Data //Tự động sinh ra Getter, Setter
@NoArgsConstructor//Tự động sinh Constructor rỗng
@AllArgsConstructor // tự động sinh Constructor đầy đủ tham số
public class Employee {
    @Id // Đánh dấu thuộc tính này là khóa chính
    @GeneratedValue(strategy = GenerationType.IDENTITY) // cho sql tự động tăng Id
    private Integer id;

    @Column(name="full_name")
    private String name;
    private double salary;

}
