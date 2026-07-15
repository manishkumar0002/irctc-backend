package com.irctc.irctc_backend.modules.chart.controller;

import com.irctc.irctc_backend.modules.chart.entity.ReservationChart;
import com.irctc.irctc_backend.modules.chart.service.ChartService;
import com.irctc.irctc_backend.modules.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class ChartController {

    private final ChartService chartService;

    // Admin Chart Preparation API
    @PostMapping("/api/admin/v1/charts/prepare")
    public ResponseEntity<ApiResponse<ReservationChart>> prepareChart(
            @RequestParam Long trainId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate travelDate
    ) {
        ReservationChart chart = chartService.prepareChart(trainId, travelDate);
        return ResponseEntity.ok(ApiResponse.success("Reservation chart prepared successfully", chart));
    }

    // Public Chart Status API
    @GetMapping("/api/public/v1/charts/status")
    public ResponseEntity<ApiResponse<ReservationChart>> getChartStatus(
            @RequestParam Long trainId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate travelDate
    ) {
        ReservationChart chart = chartService.getChartStatus(trainId, travelDate);
        return ResponseEntity.ok(ApiResponse.success(chart));
    }
}
