package com.tech.hr_system.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    public SecurityConfiguration(JwtAuthenticationFilter jwtAuthFilter, AuthenticationProvider authenticationProvider) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Tắt tính năng bảo vệ CSRF (Vì chúng ta dùng JWT Token nên không cần cái này)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. PHÂN QUYỀN ĐƯỜNG ĐI (Đóng/Mở cửa cho từng API)
                .authorizeHttpRequests(auth -> auth
                        // Cửa 1: Cho phép tất cả mọi người được Đăng ký, Đăng nhập và xem tài liệu Swagger (Không cần thẻ)
                        .requestMatchers(
                                "/api/auth/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // Cửa 2: BẤT KỲ đường dẫn nào khác (Thêm, sửa, xóa, lấy danh sách) ĐỀU PHẢI XUẤT TRÌNH THẺ
                        .anyRequest().authenticated()
                )

                // 3. Không lưu phiên đăng nhập (Stateless) - Khách cứ ra khỏi cửa là quên mặt, lần sau vào lại phải đưa thẻ JWT
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. Bố trí nhân sự: Giao Sổ Nam Tào cho hệ thống
                .authenticationProvider(authenticationProvider)

                // 5. Bố trí anh Bảo vệ (JwtAuthFilter) đứng ngay TRƯỚC cái máy kiểm tra Username/Password mặc định của Spring
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
