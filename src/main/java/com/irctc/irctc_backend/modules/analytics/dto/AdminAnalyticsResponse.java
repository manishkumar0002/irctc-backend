package com.irctc.irctc_backend.modules.analytics.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class AdminAnalyticsResponse {
    private Double todayRevenue;
    private Double monthlyRevenue;
    private Long totalBookings;
    private Long totalRefunds;
    private Long totalCancellations;
    private String mostBookedTrain;
    private Double averageOccupancyRate;
    private Map<String, Long> popularRoutes; // e.g. "NDLS-BCT" -> 45
    private Map<String, Double> monthlyRevenueTrend; // e.g. "2026-07" -> 50000.0
    private Map<String, Long> userRegistrationTrend; // e.g. "2026-07" -> 120
}
