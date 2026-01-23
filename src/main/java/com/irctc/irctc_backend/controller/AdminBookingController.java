package com.irctc.irctc_backend.controller;

import com.irctc.irctc_backend.Mapper.AdminBookingMapper;
import com.irctc.irctc_backend.dto.AdminBookingResponse;
import com.irctc.irctc_backend.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/bookings")
@RequiredArgsConstructor
public class AdminBookingController {

    private final BookingRepository bookingRepository;

    @GetMapping
    public List<AdminBookingResponse> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(AdminBookingMapper::toDto)
                .toList();
    }
}
