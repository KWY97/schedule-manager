package com.example.manage.repository;

import com.example.manage.domain.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    // 로그인 시 사용
    Optional<Admin> findByLoginId(String loginId);
}
