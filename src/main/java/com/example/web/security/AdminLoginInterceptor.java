package com.example.web.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;

public class AdminLoginInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        Principal principal = request.getUserPrincipal();

        if (principal != null) {
            if (principal instanceof UsernamePasswordAuthenticationToken) {
                UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
                if (token.getCredentials() != null) {
                    return true;
                }
            } else {
                return true;
            }
        }

        response.sendRedirect(request.getContextPath() + "/admin-login");
        return false;
    }
}
