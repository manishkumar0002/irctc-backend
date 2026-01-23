package com.irctc.irctc_backend.scheduler;

import com.irctc.irctc_backend.entity.Booking;
import com.irctc.irctc_backend.repository.BookingRepository;
import com.irctc.irctc_backend.service.BookingCancellationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@EnableScheduling
@Component
@RequiredArgsConstructor
public class AutoCancelScheduler {

    private final BookingRepository bookingRepository;
    private final BookingCancellationService bookingCancellationService;

    @Scheduled(fixedRate = 60000)
    public void cancelUnpaidBookings() {

        LocalDateTime expiry = LocalDateTime.now().minusMinutes(10);

        List<Booking> bookings =
                bookingRepository.findByStatusAndCreatedAtBefore(
                        "PAYMENT_PENDING", expiry);

        bookings.forEach(b ->
                bookingCancellationService.cancelBooking(b.getId()));
    }
}
