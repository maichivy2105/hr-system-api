package com.tech.hr_system.security;

import com.tech.hr_system.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ApplicationConfig {
    private final UserRepository userRepository;

    public ApplicationConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 1. CHỮA LỖI LAMBDA: Viết rành mạch ra bằng Interface ẩn (Anonymous Class), Lấy tên từ Sổ Nam Tào đưa cho anh Bảo vệ
    @Bean
    public UserDetailsService userDetailsService() {
      return new UserDetailsService() {
          @Override
          public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
              //ép kiểu rõ ràng
              return userRepository.findByUsername(username)
                      .orElseThrow(()-> new UsernameNotFoundException("Không tìm thấy user: "+username));
          }
      };
    }

    // 2. MÁY MÃ HÓA: Mật khẩu lưu trong DB sẽ bị băm nát ra (VD: 123456 -> $2a$10$xyz...) để chống hacker
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 3. NGƯỜI DUYỆT HỒ SƠ: Nắm giữ Sổ Nam Tào và Máy Mã Hóa để so sánh pass khách nhập với pass trong DB
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService());
//        cài đặt máy mã hóa mật khẩu
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // 4. TỔNG TƯ LỆNH: Quản lý toàn bộ quá trình đăng nhập
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
