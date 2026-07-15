package com.irctc.irctc_backend.modules.analytics.controller;

import com.irctc.irctc_backend.modules.analytics.dto.AdminAnalyticsResponse;
import com.irctc.irctc_backend.modules.analytics.service.AnalyticsService;
import com.irctc.irctc_backend.modules.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdminAnalyticsResponse>> getDashboardAnalytics() {
        AdminAnalyticsResponse data = analyticsService.getAdminAnalytics();
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
