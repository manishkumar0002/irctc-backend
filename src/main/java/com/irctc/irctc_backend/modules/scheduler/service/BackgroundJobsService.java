package com.irctc.irctc_backend.modules.scheduler.service;

import com.irctc.irctc_backend.entity.Booking;
import com.irctc.irctc_backend.modules.notification.service.NotificationService;
import com.irctc.irctc_backend.repository.BookingRepository;
import com.irctc.irctc_backend.service.BookingCancellationService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BackgroundJobsService {

    private final BookingRepository bookingRepository;
    private final BookingCancellationService bookingCancellationService;
    private final NotificationService notificationService;
    private final EntityManager entityManager;

    // 1. Cancel bookings pending payment for more than 15 minutes (runs every 5 minutes)
    @Scheduled(fixedRate = 300000)
    @Transactional
    public void cancelExpiredBookings() {
        LocalDateTime limit = LocalDateTime.now().minusMinutes(15);
        List<Booking> expired = bookingRepository.findByStatusAndCreatedAtBefore("PAYMENT_PENDING", limit);

        for (Booking booking : expired) {
            try {
                System.out.println("Auto-cancelling expired payment pending booking: " + booking.getId());
                bookingCancellationService.cancelBooking(booking.getId());
            } catch (Exception e) {
                System.err.println("Failed to auto-cancel booking " + booking.getId() + ": " + e.getMessage());
            }
        }
    }

    // 2. Retry failed notifications (runs every 5 minutes)
    @Scheduled(fixedRate = 300000)
    public void retryNotifications() {
        try {
            System.out.println("Running background job to retry failed notifications...");
            notificationService.retryFailedNotifications();
        } catch (Exception e) {
            System.err.println("Notification retry job failed: " + e.getMessage());
        }
    }

    // 3. Clear audit logs older than 30 days (runs daily at midnight)
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void cleanupAuditLogs() {
        try {
            System.out.println("Running background job to clean up audit logs older than 30 days...");
            LocalDateTime limit = LocalDateTime.now().minusDays(30);
            int deleted = entityManager.createQuery("DELETE FROM AuditLog a WHERE a.timestamp < :limit")
                    .setParameter("limit", limit)
                    .executeUpdate();
            System.out.println("Cleaned up " + deleted + " old audit log records.");
        } catch (Exception e) {
            System.err.println("Audit log cleanup job failed: " + e.getMessage());
        }
    }
}
