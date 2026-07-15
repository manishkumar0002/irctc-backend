package com.irctc.irctc_backend.modules.analytics.service;

import com.irctc.irctc_backend.modules.analytics.dto.AdminAnalyticsResponse;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final EntityManager entityManager;

    public AdminAnalyticsResponse getAdminAnalytics() {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();

        // 1. Today's Revenue
        Double todayRevenue = entityManager.createQuery(
                "SELECT COALESCE(SUM(p.amount), 0.0) FROM Payment p WHERE p.paymentStatus = 'SUCCESS' AND p.paymentTimestamp >= :start", Double.class)
                .setParameter("start", startOfToday)
                .getSingleResult();

        // 2. Monthly Revenue
        Double monthlyRevenue = entityManager.createQuery(
                "SELECT COALESCE(SUM(p.amount), 0.0) FROM Payment p WHERE p.paymentStatus = 'SUCCESS' AND p.paymentTimestamp >= :start", Double.class)
                .setParameter("start", startOfMonth)
                .getSingleResult();

        // 3. Booking statistics
        Long totalBookings = entityManager.createQuery(
                "SELECT COUNT(b) FROM Booking b", Long.class)
                .getSingleResult();

        Long totalRefunds = entityManager.createQuery(
                "SELECT COUNT(p) FROM Payment p WHERE p.refundStatus = 'SUCCESS'", Long.class)
                .getSingleResult();

        Long totalCancellations = entityManager.createQuery(
                "SELECT COUNT(b) FROM Booking b WHERE b.status = 'CANCELLED'", Long.class)
                .getSingleResult();

        // 4. Most booked train
        String mostBookedTrain = "N/A";
        List<String> trainResult = entityManager.createQuery(
                "SELECT b.train.trainName FROM Booking b GROUP BY b.train.trainName ORDER BY COUNT(b) DESC", String.class)
                .setMaxResults(1)
                .getResultList();
        if (!trainResult.isEmpty()) {
            mostBookedTrain = trainResult.get(0);
        }

        // 5. Popular routes
        List<Object[]> routeResults = entityManager.createQuery(
                "SELECT CONCAT(b.sourceStationCode, '-', b.destinationStationCode), COUNT(b) FROM Booking b GROUP BY b.sourceStationCode, b.destinationStationCode ORDER BY COUNT(b) DESC", Object[].class)
                .setMaxResults(5)
                .getResultList();
        Map<String, Long> popularRoutes = new HashMap<>();
        for (Object[] res : routeResults) {
            popularRoutes.put((String) res[0], (Long) res[1]);
        }

        // 6. Occupancy Rate Estimate (Allocated physical seats / Total physical seats)
        Long allocatedSeats = entityManager.createQuery(
                "SELECT COUNT(pa) FROM PassengerAllocation pa WHERE pa.seat IS NOT NULL AND pa.status = 'CONFIRMED'", Long.class)
                .getSingleResult();
        Long totalSeats = entityManager.createQuery(
                "SELECT COUNT(s) FROM Seat s", Long.class)
                .getSingleResult();

        double occupancyRate = (totalSeats > 0) ? ((double) allocatedSeats / totalSeats) * 100.0 : 0.0;

        // 7. Trends (Mock structure populated with group by)
        Map<String, Double> revenueTrend = new HashMap<>();
        revenueTrend.put(LocalDate.now().toString(), monthlyRevenue);

        Map<String, Long> registrationTrend = new HashMap<>();
        Long userCount = entityManager.createQuery("SELECT COUNT(u) FROM User u", Long.class).getSingleResult();
        registrationTrend.put(LocalDate.now().toString(), userCount);

        return AdminAnalyticsResponse.builder()
                .todayRevenue(todayRevenue)
                .monthlyRevenue(monthlyRevenue)
                .totalBookings(totalBookings)
                .totalRefunds(totalRefunds)
                .totalCancellations(totalCancellations)
                .mostBookedTrain(mostBookedTrain)
                .averageOccupancyRate(occupancyRate)
                .popularRoutes(popularRoutes)
                .monthlyRevenueTrend(revenueTrend)
                .userRegistrationTrend(registrationTrend)
                .build();
    }
}
