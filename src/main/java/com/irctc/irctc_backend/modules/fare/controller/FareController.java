package com.irctc.irctc_backend.modules.fare.controller;

import com.irctc.irctc_backend.modules.common.dto.ApiResponse;
import com.irctc.irctc_backend.modules.fare.entity.FareBreakdown;
import com.irctc.irctc_backend.modules.fare.service.FareService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/fare")
@RequiredArgsConstructor
public class FareController {

    private final FareService fareService;

    @GetMapping("/breakdown/{bookingId}")
    public ResponseEntity<ApiResponse<FareBreakdown>> getFareBreakdown(@PathVariable Long bookingId) {
        FareBreakdown data = fareService.getFareBreakdownByBookingId(bookingId);
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
