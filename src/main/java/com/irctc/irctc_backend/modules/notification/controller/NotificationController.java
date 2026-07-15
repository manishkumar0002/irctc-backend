package com.irctc.irctc_backend.modules.notification.controller;

import com.irctc.irctc_backend.modules.common.dto.ApiResponse;
import com.irctc.irctc_backend.modules.notification.entity.NotificationHistory;
import com.irctc.irctc_backend.modules.notification.service.NotificationService;
import com.irctc.irctc_backend.modules.notification.repository.NotificationHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationHistoryRepository notificationHistoryRepository;

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<NotificationHistory>>> getNotificationHistory(@RequestParam String recipient) {
        List<NotificationHistory> data = notificationHistoryRepository.findByRecipient(recipient);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PostMapping("/retry")
    public ResponseEntity<ApiResponse<Void>> triggerRetry() {
        notificationService.retryFailedNotifications();
        return ResponseEntity.ok(ApiResponse.success("Triggered retry job successfully", null));
    }
}
