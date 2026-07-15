package com.irctc.irctc_backend.modules.waitlist.service;

import com.irctc.irctc_backend.entity.Booking;
import com.irctc.irctc_backend.entity.ClassType;
import com.irctc.irctc_backend.entity.Passenger;
import com.irctc.irctc_backend.modules.seat.entity.PassengerAllocation;
import com.irctc.irctc_backend.modules.seat.entity.Seat;
import com.irctc.irctc_backend.modules.seat.repository.PassengerAllocationRepository;
import com.irctc.irctc_backend.modules.waitlist.entity.WaitlistBooking;
import com.irctc.irctc_backend.modules.waitlist.repository.WaitlistBookingRepository;
import com.irctc.irctc_backend.repository.PassengerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WaitlistService {

    private final WaitlistBookingRepository waitlistBookingRepository;
    private final PassengerAllocationRepository passengerAllocationRepository;
    private final PassengerRepository passengerRepository;

    private static final int MAX_RAC_COUNT = 20; // 10 side-lower berths shared by 20 passengers

    @Transactional
    public void addToWaitlist(Booking booking, List<Passenger> passengers) {
        Long trainId = booking.getTrain().getId();
        LocalDate date = booking.getTravelDate();
        ClassType classType = booking.getClassType();

        for (Passenger passenger : passengers) {
            // Find current active RAC list size
            List<WaitlistBooking> activeRac = waitlistBookingRepository
                    .findActiveByTrainAndDateAndClass(trainId, date, classType, "RAC");

            String queueType = "WL";
            if (activeRac.size() < MAX_RAC_COUNT) {
                queueType = "RAC";
            }

            int position = waitlistBookingRepository
                    .findMaxQueuePosition(trainId, date, classType, queueType)
                    .orElse(0) + 1;

            // Create WaitlistBooking record
            WaitlistBooking waitlistBooking = WaitlistBooking.builder()
                    .booking(booking)
                    .passenger(passenger)
                    .queueType(queueType)
                    .queuePosition(position)
                    .status("ACTIVE")
                    .build();

            waitlistBookingRepository.save(waitlistBooking);

            // Update Passenger seatNumber string
            passenger.setSeatNumber(queueType + "-" + position);
            passengerRepository.save(passenger);

            // Create PassengerAllocation
            PassengerAllocation allocation = PassengerAllocation.builder()
                    .booking(booking)
                    .passenger(passenger)
                    .status(queueType)
                    .build();

            passengerAllocationRepository.save(allocation);
        }
    }

    @Transactional
    public void promoteWaitlistedPassengers(Long trainId, LocalDate date, ClassType classType, Seat vacantSeat) {
        // 1. Get first active RAC passenger
        List<WaitlistBooking> activeRac = waitlistBookingRepository
                .findActiveByTrainAndDateAndClass(trainId, date, classType, "RAC");

        if (!activeRac.isEmpty()) {
            WaitlistBooking firstRac = activeRac.get(0);
            Passenger passenger = firstRac.getPassenger();

            // Promote RAC -> CONFIRMED (Assign vacant seat)
            firstRac.setStatus("PROMOTED");
            waitlistBookingRepository.save(firstRac);

            PassengerAllocation allocation = passengerAllocationRepository.findByBookingId(firstRac.getBooking().getId())
                    .stream()
                    .filter(pa -> pa.getPassenger().getId().equals(passenger.getId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Allocation not found"));

            allocation.setSeat(vacantSeat);
            allocation.setCoachNumber(vacantSeat.getCoach().getCoachNumber());
            allocation.setSeatNumber(vacantSeat.getSeatNumber());
            allocation.setBerthType(vacantSeat.getBerthType());
            allocation.setStatus("CONFIRMED");
            passengerAllocationRepository.save(allocation);

            passenger.setSeatNumber(vacantSeat.getCoach().getCoachNumber() + "-" +
                    vacantSeat.getSeatNumber() + " (" + vacantSeat.getBerthType() + ")");
            passengerRepository.save(passenger);

            // 2. Promote first active WL -> RAC to take the vacant RAC slot
            List<WaitlistBooking> activeWl = waitlistBookingRepository
                    .findActiveByTrainAndDateAndClass(trainId, date, classType, "WL");

            if (!activeWl.isEmpty()) {
                WaitlistBooking firstWl = activeWl.get(0);
                Passenger wlPassenger = firstWl.getPassenger();

                int newRacPos = waitlistBookingRepository
                        .findMaxQueuePosition(trainId, date, classType, "RAC")
                        .orElse(0) + 1;

                firstWl.setQueueType("RAC");
                firstWl.setQueuePosition(newRacPos);
                waitlistBookingRepository.save(firstWl);

                PassengerAllocation wlAllocation = passengerAllocationRepository.findByBookingId(firstWl.getBooking().getId())
                        .stream()
                        .filter(pa -> pa.getPassenger().getId().equals(wlPassenger.getId()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Allocation not found"));

                wlAllocation.setStatus("RAC");
                passengerAllocationRepository.save(wlAllocation);

                wlPassenger.setSeatNumber("RAC-" + newRacPos);
                passengerRepository.save(wlPassenger);
            }
        } else {
            // No RAC passengers. Check if there are any WL passengers to promote directly to CONFIRMED
            List<WaitlistBooking> activeWl = waitlistBookingRepository
                    .findActiveByTrainAndDateAndClass(trainId, date, classType, "WL");

            if (!activeWl.isEmpty()) {
                WaitlistBooking firstWl = activeWl.get(0);
                Passenger passenger = firstWl.getPassenger();

                firstWl.setStatus("PROMOTED");
                waitlistBookingRepository.save(firstWl);

                PassengerAllocation allocation = passengerAllocationRepository.findByBookingId(firstWl.getBooking().getId())
                        .stream()
                        .filter(pa -> pa.getPassenger().getId().equals(passenger.getId()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Allocation not found"));

                allocation.setSeat(vacantSeat);
                allocation.setCoachNumber(vacantSeat.getCoach().getCoachNumber());
                allocation.setSeatNumber(vacantSeat.getSeatNumber());
                allocation.setBerthType(vacantSeat.getBerthType());
                allocation.setStatus("CONFIRMED");
                passengerAllocationRepository.save(allocation);

                passenger.setSeatNumber(vacantSeat.getCoach().getCoachNumber() + "-" +
                        vacantSeat.getSeatNumber() + " (" + vacantSeat.getBerthType() + ")");
                passengerRepository.save(passenger);
            }
        }
    }
}
