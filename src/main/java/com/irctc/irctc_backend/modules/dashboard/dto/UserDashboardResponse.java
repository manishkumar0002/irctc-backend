package com.irctc.irctc_backend.modules.dashboard.dto;

import com.irctc.irctc_backend.entity.Booking;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserDashboardResponse {
    private String userEmail;
    private String name;
    private Long totalBookings;
    private Long upcomingTripsCount;
    private Long completedTripsCount;
    private Long cancelledTripsCount;
    private Double totalSpent;
    private List<Booking> recentBookings;
}
