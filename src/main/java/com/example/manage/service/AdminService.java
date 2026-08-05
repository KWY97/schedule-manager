package com.example.manage.service;

import com.example.manage.domain.Admin;
import com.example.manage.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
// final 필드를 매개변수로 받는 생성자를 Lombok이 자동으로 만들어줌
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public void createAdmin(String loginId, String rawPassword) {

        if (adminRepository.findByLoginId(loginId).isPresent()) {
            return;
        }

        String encodedPassword = passwordEncoder.encode(rawPassword);
        Admin admin = new Admin(loginId, encodedPassword);

        adminRepository.save(admin);
    }

    public Admin login(String loginId, String rawPassword) {

        Admin admin = adminRepository.findByLoginId(loginId).orElse(null);

        if (admin == null) {
            return null;
        }

        if (!passwordEncoder.matches(rawPassword, admin.getPassword())) {
            return null;
        }

        return admin;
    }
}
