package com.irctc.irctc_backend.modules.audit.interceptor;

import com.irctc.irctc_backend.modules.audit.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuditInterceptor implements HandlerInterceptor {

    private final AuditLogService auditLogService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String path = request.getRequestURI();

        // Skip static resources or error path
        if (path.contains("/swagger-ui") || path.contains("/v3/api-docs") || path.equals("/error")) {
            return;
        }

        String method = request.getMethod();
        String action = method + " " + path;

        // Extract user context
        String userEmail = "GUEST";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            userEmail = authentication.getName();
        }

        // Get IP
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }

        // Get User-Agent
        String userAgent = request.getHeader("User-Agent");

        // Save log asynchronously/synchronously
        auditLogService.logActivity(userEmail, action, ipAddress, userAgent);
    }
}
