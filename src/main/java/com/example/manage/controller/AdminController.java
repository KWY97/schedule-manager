package com.example.manage.controller;

import com.example.manage.domain.Admin;
import com.example.manage.service.AdminService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/admin/login")
    public String adminLogin() {
        return "admin/login";
    }

    @PostMapping("/admin/login")
    public String adminLogin(
            @RequestParam String loginId,
            @RequestParam String password,
            HttpSession session,
            Model model
    ) {
        Admin admin = adminService.login(loginId, password);

        if (admin == null) {
            model.addAttribute("errorMessage", "아이디 또는 비밀번호가 올바르지 않습니다.");
            return "admin/login";
        }

        session.setAttribute("loginAdminId", admin.getAdminId());

        return "redirect:/admin";
    }

    @GetMapping("/admin")
    public String adminHome(HttpSession session) {

        if (session.getAttribute("loginAdminId") == null) {
            return "redirect:/admin/login";
        }
        return "admin/home";
    }

    @GetMapping("/admin/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/";
    }
}
