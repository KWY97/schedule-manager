package com.example.manage.service;

import com.example.manage.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
// final 필드를 매개변수로 받는 생성자를 Lombok이 자동으로 만들어줌
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
}
