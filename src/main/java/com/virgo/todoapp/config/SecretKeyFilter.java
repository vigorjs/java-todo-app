package com.virgo.todoapp.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SecretKeyFilter extends OncePerRequestFilter {

    @Value("${secret.key.super-admin}")
    private String superAdminSecretKey;

    @Value("${secret.key.admin}")
    private String adminSecretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        String superAdminKey = request.getHeader("X-Super-Admin-Secret-Key");
        String adminKey = request.getHeader("X-Admin-Secret-Key");

        if (path.startsWith("/api/admin/super-admin")) {
            if (superAdminKey == null || !superAdminKey.equals(superAdminSecretKey)) {
                response.sendError(HttpStatus.FORBIDDEN.value(), "Invalid Super Admin Secret Key");
                return;
            }
        } else if (path.startsWith("/api/admin/users/") && path.contains("role")) {
            if (adminKey == null || !adminKey.equals(adminSecretKey)) {
                response.sendError(HttpStatus.FORBIDDEN.value(), "Invalid Admin Secret Key");
                return;
            }
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("SUPER_ADMIN"))) {
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "authentication required");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
