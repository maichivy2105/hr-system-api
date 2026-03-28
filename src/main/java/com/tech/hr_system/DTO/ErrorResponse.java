package com.tech.hr_system.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp; //thời gian xảy ra lỗi
    private int status; // mã lỗi : (400,404,500,...)
    private String error; //tên lỗi
    private String message; // Lời nhắn cho người dùng
}
