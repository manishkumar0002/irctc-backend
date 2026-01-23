package com.irctc.irctc_backend.scheduler;

import com.irctc.irctc_backend.entity.Booking;
import com.irctc.irctc_backend.repository.BookingRepository;
import com.irctc.irctc_backend.service.BookingCancellationService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@EnableScheduling
@Service
@AllArgsConstructor
public class BookingAutoCancelScheduler {

    private final BookingRepository bookingRepository;
    private final BookingCancellationService bookingCancellationService;

    @Scheduled(fixedRate = 60000) // every 1 minute
    public void autoCancelUnpaidBookings() {

        LocalDateTime expiryTime = LocalDateTime.now().minusMinutes(10);

        List<Booking> expiredBookings =
                bookingRepository.findByStatusAndCreatedAtBefore(
                        "PAYMENT_PENDING",
                        expiryTime
                );

        expiredBookings.forEach(b ->
                bookingCancellationService.cancelBooking(b.getId())
        );
    }
}
