package com.example.manage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberController {

    @GetMapping("/member/login")
    public String memberLogin() {
        return "member/login";
    }
}
