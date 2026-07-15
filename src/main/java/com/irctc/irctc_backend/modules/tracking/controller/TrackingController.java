package com.irctc.irctc_backend.modules.tracking.controller;

import com.irctc.irctc_backend.modules.common.dto.ApiResponse;
import com.irctc.irctc_backend.modules.tracking.dto.TrainLiveLocation;
import com.irctc.irctc_backend.modules.tracking.service.TrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/v1/tracking")
@RequiredArgsConstructor
public class TrackingController {

    private final TrackingService trackingService;

    @GetMapping("/{trainId}/live")
    public ResponseEntity<ApiResponse<TrainLiveLocation>> getLiveLocation(@PathVariable Long trainId) {
        TrainLiveLocation location = trackingService.getLiveLocation(trainId);
        return ResponseEntity.ok(ApiResponse.success(location));
    }
}
