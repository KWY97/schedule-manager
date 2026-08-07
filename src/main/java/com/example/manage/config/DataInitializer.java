package com.example.manage.config;

import com.example.manage.service.AdminService;
import com.example.manage.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
// Spring Boot 서버가 정상적으로 시작된 뒤 run() 메서드를 자동 실행
public class DataInitializer implements CommandLineRunner {

    private final AdminService adminService;
    private final MemberService memberService;

    @Override
    public void run(String... args) {
        adminService.createAdmin("admin", "1234");
        memberService.createMember(1, 1, "p001", "0001");
        memberService.createMember(4, 2, "p004", "0004");
        memberService.createMember(8, 3, "p008", "0008");
    }
}
