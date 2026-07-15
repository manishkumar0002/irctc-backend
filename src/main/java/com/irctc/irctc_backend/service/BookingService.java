package com.irctc.irctc_backend.service;

import com.irctc.irctc_backend.dto.BookingRequest;
import com.irctc.irctc_backend.dto.PassengerRequest;
import com.irctc.irctc_backend.entity.*;
import com.irctc.irctc_backend.repository.BookingRepository;
import com.irctc.irctc_backend.repository.PassengerRepository;
import com.irctc.irctc_backend.repository.TrainRepository;
import com.irctc.irctc_backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import com.irctc.irctc_backend.modules.seat.service.SeatAllocationService;
import com.irctc.irctc_backend.modules.fare.service.FareService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final PassengerRepository passengerRepository;
    private final TrainRepository trainRepository;
    private final UserRepository userRepository;
    private final RouteValidationService routeValidationService;
    private final SeatAvailabilityService seatAvailabilityService;
    private final SeatAllocationService seatAllocationService;
    private final FareService fareService;
    private final com.irctc.irctc_backend.modules.chart.repository.ReservationChartRepository reservationChartRepository;
    private final com.irctc.irctc_backend.modules.timeline.service.ActivityTimelineService activityTimelineService;


    // BOOK TICKET + SAVE PASSENGERS

    @Transactional
    public Booking bookTicket(String userEmail, BookingRequest request) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Train train = trainRepository.findById(request.getTrainId())
                .orElseThrow(() -> new RuntimeException("Train not found"));

        // Check if chart is prepared to freeze bookings
        reservationChartRepository.findByTrainIdAndTravelDate(request.getTrainId(), request.getTravelDate())
                .ifPresent(chart -> {
                    if ("PREPARED".equals(chart.getChartStatus())) {
                        throw new RuntimeException("Bookings are frozen as chart is already prepared for this train and date.");
                    }
                });

        routeValidationService.validateRoute(
                train,
                request.getSourceStationCode(),
                request.getDestinationStationCode()
            );
        if (request.getPassengers() == null || request.getPassengers().isEmpty()) {
            throw new RuntimeException("At least one passenger is required");
        }

        int seatCount = request.getPassengers().size();

        SeatAvailability seatAvailability =
                seatAvailabilityService.getOrCreateAvailability(
                        train,
                        request.getTravelDate(),
                        request.getClassType()
                );

        boolean seatsAvailable = seatAvailability.getAvailableSeats() >= seatCount;
        if (seatsAvailable) {
            seatAvailability.setAvailableSeats(
                    seatAvailability.getAvailableSeats() - seatCount
            );
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setTrain(train);
        booking.setTravelDate(request.getTravelDate());
        booking.setClassType(request.getClassType());
        booking.setSeatCount(seatCount);
        booking.setStatus("PAYMENT_PENDING");
        booking.setPnr(generatePNR());
        booking.setSourceStationCode(request.getSourceStationCode());
        booking.setDestinationStationCode(request.getDestinationStationCode());
        booking.setCreatedAt(LocalDateTime.now());

        booking = bookingRepository.save(booking);

        // SAVE PASSENGERS
        List<Passenger> savedPassengers = new ArrayList<>();
        for (PassengerRequest pr : request.getPassengers()) {
            Passenger passenger = new Passenger();
            passenger.setName(pr.getName());
            passenger.setAge(pr.getAge());
            passenger.setGender(pr.getGender());
            passenger.setBooking(booking);

            savedPassengers.add(passengerRepository.save(passenger));
        }

        // Allocate seats/berths in DB
        seatAllocationService.allocateSeats(booking, savedPassengers);

        // Calculate and save fare breakdown
        fareService.calculateAndSaveFare(booking, savedPassengers);

        // Record timeline milestone
        activityTimelineService.addEvent(booking, "CREATED", "Ticket reservation created with PNR: " + booking.getPnr() + " in state PAYMENT_PENDING.");

        return booking;
    }


    //  MY BOOKINGS (THIS WAS MISSING AGAIN)

    public List<Booking> getBookingsByUser(String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return bookingRepository.findByUser(user);
    }

    private String generatePNR() {
        return "PNR-" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }
}
