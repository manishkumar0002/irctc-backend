package com.irctc.irctc_backend.controller;

import com.irctc.irctc_backend.dto.PassengerResponse;
import com.irctc.irctc_backend.entity.Booking;
import com.irctc.irctc_backend.repository.BookingRepository;
import com.irctc.irctc_backend.repository.PassengerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/passengers")
public class PassengerController {

  private final PassengerRepository passengerRepository;
  private final BookingRepository bookingRepository;



  @GetMapping("/{bookingId}")
  public List<PassengerResponse> getPassengersByBooking(
          @PathVariable Long bookingId,
          Authentication authentication
  ) {
    Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));

    //  security
    if (!booking.getUser().getEmail().equals(authentication.getName())) {
      throw new RuntimeException("Access denied");
    }

    return passengerRepository.findByBooking(booking)
            .stream()
            .map(p -> new PassengerResponse(
                    p.getId(),
                    p.getName(),
                    p.getAge(),
                    p.getGender(),
                    p.getSeatNumber()
            ))
            .toList();
  }
}
