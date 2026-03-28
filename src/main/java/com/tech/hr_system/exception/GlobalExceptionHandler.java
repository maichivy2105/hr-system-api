package com.tech.hr_system.exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import com.tech.hr_system.DTO.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // Đánh dấu là trạm xử lý lỗi toàn cục
public class GlobalExceptionHandler {
    // Chỉ định: Bắt đúng cái lỗi Validation dữ liệu (MethodArgumentNotValidException)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {

        // Gom tất cả các lỗi của các ô nhập liệu vào một chữ String
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Lỗi nhập liệu",
                errors.toString() // Trả về chi tiết ô nào sai cái gì
        );
        return ResponseEntity.badRequest().body(errorResponse);
    }

    // 2. Xử lý lỗi Chung (Khi bạn chủ động ném ra IllegalArgumentException, vd: không tìm thấy user)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Yêu cầu không hợp lệ",
                ex.getMessage()
        );
        return ResponseEntity.badRequest().body(errorResponse);
    }
    // 3. Xử lý lỗi do Database/Hibernate ném lên (Ví dụ: @Min, @NotBlank ở Entity)
    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(jakarta.validation.ConstraintViolationException ex) {

        // Gom các câu thông báo lỗi lại ("lương không được nhỏ hơn 0")
        StringBuilder errorMsg = new StringBuilder();
        ex.getConstraintViolations().forEach(violation -> {
            errorMsg.append(violation.getMessage()).append("; ");
        });

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Lỗi kiểm duyệt dữ liệu",
                errorMsg.toString()
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }
}
