package com.tech.hr_system.service;

import com.tech.hr_system.DTO.AuthRequest;
import com.tech.hr_system.DTO.AuthResponse;
import com.tech.hr_system.entity.User;
import com.tech.hr_system.repository.UserRepository;
import com.tech.hr_system.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserRepository repository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    // 1. CHỨC NĂNG ĐĂNG KÝ
    public AuthResponse register(AuthRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        // Bắt buộc phải băm nát mật khẩu ra trước khi lưu xuống DB
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("ROLE_ADMIN"); // Cấp quyền mặc định

        repository.save(user); // Ghi vào Sổ Nam Tào

        // In thẻ JWT cho người mới
        String jwtToken = jwtService.generateToken(user);
        return new AuthResponse(jwtToken);
    }

    // 2. CHỨC NĂNG ĐĂNG NHẬP
    public AuthResponse authenticate(AuthRequest request) {
        // Gọi bảo vệ kiểm tra mật khẩu. Nếu sai, nó sẽ văng lỗi ngay lập tức!
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // Nếu pass đúng, lôi thông tin từ DB ra và in thẻ JWT
        User user = repository.findByUsername(request.getUsername()).orElseThrow();
        String jwtToken = jwtService.generateToken(user);

        return new AuthResponse(jwtToken);
    }
}
