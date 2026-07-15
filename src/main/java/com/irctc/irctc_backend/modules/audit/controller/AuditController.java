package com.irctc.irctc_backend.modules.audit.controller;

import com.irctc.irctc_backend.modules.audit.entity.AuditLog;
import com.irctc.irctc_backend.modules.audit.service.AuditLogService;
import com.irctc.irctc_backend.modules.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/v1/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditLogService auditLogService;

    @GetMapping("/logs")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getAllLogs() {
        List<AuditLog> data = auditLogService.getAllLogs();
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/logs/user")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getUserLogs(@RequestParam String email) {
        List<AuditLog> data = auditLogService.getLogsByUser(email);
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
