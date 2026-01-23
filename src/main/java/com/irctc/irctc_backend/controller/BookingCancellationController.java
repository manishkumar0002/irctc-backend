package com.irctc.irctc_backend.controller;

import com.irctc.irctc_backend.service.BookingCancellationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingCancellationController {

    private final BookingCancellationService bookingCancellationService;

    public BookingCancellationController(
            BookingCancellationService bookingCancellationService
    ) {
        this.bookingCancellationService = bookingCancellationService;
    }

    // CANCEL BOOKING
    @DeleteMapping("/cancellations/{bookingId}")
    public void cancelBooking(@PathVariable Long bookingId) {
        bookingCancellationService.cancelBooking(bookingId);
    }
}
