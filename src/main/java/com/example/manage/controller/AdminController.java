package com.example.manage.controller;

import com.example.manage.domain.Admin;
import com.example.manage.domain.Member;
import com.example.manage.service.AdminService;
import com.example.manage.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final MemberService memberService;

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
        return "admin/home";
    }

    @GetMapping("/admin/logout")
    public String adminLogout(HttpSession session) {

        session.invalidate();

        return "redirect:/";
    }

    @GetMapping("admin/members")
    public String memberList(Model model) {

        List<Member> members = memberService.findAllMembers();

        model.addAttribute("members", members);

        return "admin/member-list";
    }

    @GetMapping("/admin/members/{memberId}")
    public String memberDetail(
            @PathVariable Long memberId,
            Model model
    ) {
        Member member = memberService.findMember(memberId);

        if (member == null) {
            return "redirect:/admin/members";
        }

        model.addAttribute("member", member);

        return "admin/member-detail";
    }

    @GetMapping("/admin/schedules/new")
    public String scheduleForm(Model model) {

        List<Member> members = memberService.findAllMembers();

        model.addAttribute("members", members);

        return "admin/schedule-form";
    }
}
