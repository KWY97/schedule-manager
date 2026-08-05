package com.example.manage.controller;

import com.example.manage.domain.Member;
import com.example.manage.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/member/login")
    public String memberLogin() {
        return "member/login";
    }

    @PostMapping("/member/login")
    public String memberLogin(
            @RequestParam String loginId,
            @RequestParam String password,
            HttpSession session,
            Model model
    ) {
        Member member = memberService.login(loginId, password);

        if (member == null) {
            model.addAttribute("errorMessage", "아이디 또는 비밀번호가 일치하지 않습니다.");
            return "member/login";
        }

        session.setAttribute("loginMemberId", member.getMemberId());

        return "redirect:/member";
    }

    @GetMapping("/member")
    public String memberHome(HttpSession session) {

        if (session.getAttribute("loginMemberId") == null) {
            return "redirect:/member/login";
        }

        return "member/home";
    }

    @GetMapping("/member/logout")
    public String memberLogout(HttpSession session) {

        session.invalidate();

        return "redirect:/";
    }
}
