package com.tech.hr_system.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// @Component biến class này thành một Bean để Spring Boot tự động quản lý
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
             HttpServletRequest request,
             HttpServletResponse response,
             FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. KIỂM TRA ÁO KHÁC (Header): Khách giấu thẻ JWT trong túi "Authorization"
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Nếu không có túi "Authorization" hoặc thẻ không bắt đầu bằng chữ "Bearer " -> Đuổi đi (hoặc cho đi tiếp để các bảo vệ khác xử lý)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. RÚT THẺ RA ĐỌC: Cắt bỏ 7 chữ cái đầu ("Bearer ") để lấy đúng cái lõi thẻ loằng ngoằng
        jwt = authHeader.substring(7);

        // Nhờ Máy đọc thẻ dịch xem tên người dùng là gì
        username = jwtService.extractUsername(jwt);

        // 3. XÁC MINH DANH TÍNH: Nếu có tên và người này chưa được mở cửa
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Lôi Sổ Nam Tào ra kiểm tra xem tên này có thật trong công ty không
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Nhờ Máy soi thẻ kiểm tra thẻ thật/giả và hạn sử dụng
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // Đóng mộc "ĐÃ KIỂM DUYỆT" và mở cửa cho khách đi vào Controller
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Cập nhật thẻ vào hệ thống an ninh trung tâm
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Chuyền cho người tiếp theo (Filter tiếp theo) xử lý
        filterChain.doFilter(request, response);
    }
}
