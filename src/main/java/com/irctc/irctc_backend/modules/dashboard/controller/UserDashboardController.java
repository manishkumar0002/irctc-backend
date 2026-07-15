package com.irctc.irctc_backend.modules.dashboard.controller;

import com.irctc.irctc_backend.modules.common.dto.ApiResponse;
import com.irctc.irctc_backend.modules.dashboard.dto.UserDashboardResponse;
import com.irctc.irctc_backend.modules.dashboard.service.UserDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class UserDashboardController {

    private final UserDashboardService userDashboardService;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<UserDashboardResponse>> getSummary(Principal principal) {
        UserDashboardResponse data = userDashboardService.getUserDashboard(principal.getName());
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
