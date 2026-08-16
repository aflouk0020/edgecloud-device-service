package com.edgecloud.device.security;

import com.edgecloud.device.web.SecurityExceptionHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class EdgeCloudJwtAuthenticationFilter extends OncePerRequestFilter {

    private static final List<String> PROTECTED_PATH_PREFIXES = List.of("/internal/device-health", "/inventory");

    private final JwtService jwtService;

    public EdgeCloudJwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return PROTECTED_PATH_PREFIXES.stream().noneMatch(request.getRequestURI()::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            SecurityExceptionHandler.writeUnauthorized(response, "Missing authentication");
            return;
        }

        String token = header.substring(7).trim();
        if (token.isEmpty() || !jwtService.isValid(token)) {
            SecurityExceptionHandler.writeUnauthorized(response, "Invalid or expired token");
            return;
        }

        UUID userId;
        String role;
        try {
            userId = jwtService.extractUserId(token);
            role = jwtService.extractRole(token);
        } catch (Exception ex) {
            SecurityExceptionHandler.writeUnauthorized(response, "Invalid or expired token");
            return;
        }

        SecurityContextHolder.getContext().setAuthentication(new EdgeCloudJwtAuthenticationToken(userId, role, token));
        try {
            filterChain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
