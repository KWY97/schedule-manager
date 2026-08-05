package com.example.manage.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class AdminLoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {

        // 기존 세션이 있으면 가져오고 없으면 null 반환
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loginAdminId") == null) {
            response.sendRedirect("/admin/login");
            return false;
        }

        return true;
    }
}
