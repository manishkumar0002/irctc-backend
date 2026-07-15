package com.irctc.irctc_backend.modules.audit.service;

import com.irctc.irctc_backend.modules.audit.entity.AuditLog;
import com.irctc.irctc_backend.modules.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void logActivity(String email, String action, String ipAddress, String userAgent) {
        AuditLog log = AuditLog.builder()
                .userEmail(email)
                .action(action)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .timestamp(LocalDateTime.now())
                .build();
        auditLogRepository.save(log);
    }

    public List<AuditLog> getLogsByUser(String email) {
        return auditLogRepository.findByUserEmail(email);
    }

    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAll();
    }
}
