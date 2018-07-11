package com.example.web;

import com.example.web.security.AdminLoginInterceptor;
import com.example.web.security.UserLoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("page");
        registry.addViewController("/page").setViewName("page");
        registry.addViewController("/user").setViewName("user");
        registry.addViewController("/admin").setViewName("admin");
        registry.addViewController("/user-login").setViewName("user-login");
        registry.addViewController("/admin-login").setViewName("admin-login");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserLoginInterceptor()).addPathPatterns("/user");
        registry.addInterceptor(new AdminLoginInterceptor()).addPathPatterns("/admin");
    }
}
