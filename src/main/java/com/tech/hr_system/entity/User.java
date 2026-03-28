package com.tech.hr_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name="tbl_users")
@Getter
@Setter

public class User implements UserDetails { //Thêm implement
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    //Quyền hạn
    @Column(nullable = false)
    private String role; //VD: ADMIN hoặc USER

//    Các hàm bắt buộc mà Spring Security yêu cầu
@Override
public Collection<? extends GrantedAuthority> getAuthorities() {
    // Biến chữ "ADMIN" thành cái Quyền hạn mà bảo vệ hiểu được
    return List.of(new SimpleGrantedAuthority(role));
}

    // Các hàm dưới đây hỏi xem tài khoản có bị khóa/hết hạn không.
    // Tạm thời cho return true hết (tài khoản luôn hoạt động tốt)
    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

}
