package com.irctc.irctc_backend.modules.dashboard.service;

import com.irctc.irctc_backend.entity.Booking;
import com.irctc.irctc_backend.entity.User;
import com.irctc.irctc_backend.modules.dashboard.dto.UserDashboardResponse;
import com.irctc.irctc_backend.repository.BookingRepository;
import com.irctc.irctc_backend.repository.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDashboardService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final EntityManager entityManager;

    public UserDashboardResponse getUserDashboard(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        List<Booking> bookings = bookingRepository.findByUser(user);

        long totalBookings = bookings.size();
        long upcoming = bookings.stream()
                .filter(b -> b.getTravelDate().isAfter(LocalDate.now().minusDays(1)) && "CONFIRMED".equalsIgnoreCase(b.getStatus()))
                .count();
        long completed = bookings.stream()
                .filter(b -> b.getTravelDate().isBefore(LocalDate.now()) && "CONFIRMED".equalsIgnoreCase(b.getStatus()))
                .count();
        long cancelled = bookings.stream()
                .filter(b -> "CANCELLED".equalsIgnoreCase(b.getStatus()))
                .count();

        // Calculate total spent using JPQL query on payments table
        Double totalSpent = entityManager.createQuery(
                "SELECT COALESCE(SUM(p.amount), 0.0) FROM Payment p " +
                "WHERE p.booking.user.id = :userId AND p.paymentStatus = 'SUCCESS'", Double.class)
                .setParameter("userId", user.getId())
                .getSingleResult();

        // Get top 5 recent bookings
        List<Booking> recent = bookings.stream()
                .sorted((b1, b2) -> b2.getCreatedAt().compareTo(b1.getCreatedAt()))
                .limit(5)
                .toList();

        return UserDashboardResponse.builder()
                .userEmail(user.getEmail())
                .name(user.getName())
                .totalBookings(totalBookings)
                .upcomingTripsCount(upcoming)
                .completedTripsCount(completed)
                .cancelledTripsCount(cancelled)
                .totalSpent(totalSpent)
                .recentBookings(recent)
                .build();
    }
}
