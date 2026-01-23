package com.irctc.irctc_backend.service;

import com.irctc.irctc_backend.entity.Booking;
import com.irctc.irctc_backend.entity.SeatAvailability;
import com.irctc.irctc_backend.repository.BookingRepository;
import com.irctc.irctc_backend.repository.PassengerRepository;
import com.irctc.irctc_backend.repository.SeatAvailabilityRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class BookingCancellationService {

    private final BookingRepository bookingRepository;
    private final SeatAvailabilityRepository seatAvailabilityRepository;
    private final PassengerRepository passengerRepository;

    @Transactional
    public void cancelBooking(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new RuntimeException("Booking not found: " + bookingId));

        if ("CANCELLED".equals(booking.getStatus())) {
            throw new RuntimeException("Booking already cancelled");
        }

        SeatAvailability availability =
                seatAvailabilityRepository.lockSeatForUpdate(
                        booking.getTrain(),
                        booking.getTravelDate(),
                        booking.getClassType()
                ).orElseThrow(() ->
                        new RuntimeException("Seat availability not found"));

        //  restore seats
        availability.setAvailableSeats(
                availability.getAvailableSeats() + booking.getSeatCount()
        );

        //  remove passengers
        passengerRepository.deleteByBooking(booking);

        //  cancel booking
        booking.setStatus("CANCELLED");

        seatAvailabilityRepository.save(availability);
        bookingRepository.save(booking);
    }
}
