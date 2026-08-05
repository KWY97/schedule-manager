package com.example.manage.config;

import com.example.manage.interceptor.AdminLoginInterceptor;
import com.example.manage.interceptor.MemberLoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AdminLoginInterceptor())
                .addPathPatterns("/admin/**")
                .excludePathPatterns(
                        "/admin/login",
                        "/admin/logout"
                );

        registry.addInterceptor(new MemberLoginInterceptor())
                .addPathPatterns("/member/**")
                .excludePathPatterns(
                        "/member/login",
                        "/member/logout"
                );
    }
}
