package com.irctc.irctc_backend.controller;

import com.irctc.irctc_backend.Mapper.BookingMapper;
import com.irctc.irctc_backend.dto.BookingRequest;
import com.irctc.irctc_backend.dto.BookingResponse;
import com.irctc.irctc_backend.entity.Booking;
import com.irctc.irctc_backend.service.BookingService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    //  BOOK TICKET
    @PostMapping
    public BookingResponse bookTicket(
            @RequestBody BookingRequest request,
            Authentication authentication
    ) {
        Booking booking = bookingService.bookTicket(
                authentication.getName(),
                request    //  PASS FULL REQUEST
        );

        return BookingMapper.toDto(booking);
    }

    //  VIEW MY BOOKINGS
    @GetMapping("/my-bookings")
    public List<BookingResponse> myBookings(Authentication authentication) {
        return bookingService
                .getBookingsByUser(authentication.getName())
                .stream()
                .map(BookingMapper::toDto)
                .toList();
    }
}
