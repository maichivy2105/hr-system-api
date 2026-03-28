package com.tech.hr_system.repository;

import com.tech.hr_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    //tự động tìm tài khoản bằng username
    Optional<User> findByUsername(String username);
}
