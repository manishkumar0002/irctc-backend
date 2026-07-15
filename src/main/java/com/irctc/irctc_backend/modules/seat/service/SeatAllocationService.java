package com.irctc.irctc_backend.modules.seat.service;

import com.irctc.irctc_backend.entity.Booking;
import com.irctc.irctc_backend.entity.ClassType;
import com.irctc.irctc_backend.entity.Passenger;
import com.irctc.irctc_backend.modules.coach.entity.Coach;
import com.irctc.irctc_backend.modules.coach.entity.CoachStatus;
import com.irctc.irctc_backend.modules.coach.entity.CoachType;
import com.irctc.irctc_backend.modules.coach.repository.CoachRepository;
import com.irctc.irctc_backend.modules.seat.entity.BerthType;
import com.irctc.irctc_backend.modules.seat.entity.PassengerAllocation;
import com.irctc.irctc_backend.modules.seat.entity.Seat;
import com.irctc.irctc_backend.modules.seat.repository.PassengerAllocationRepository;
import com.irctc.irctc_backend.modules.seat.repository.SeatRepository;
import com.irctc.irctc_backend.modules.waitlist.service.WaitlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatAllocationService {

    private final CoachRepository coachRepository;
    private final SeatRepository seatRepository;
    private final PassengerAllocationRepository passengerAllocationRepository;
    private final WaitlistService waitlistService;

    @Transactional
    public List<PassengerAllocation> allocateSeats(Booking booking, List<Passenger> passengers) {
        Long trainId = booking.getTrain().getId();
        ClassType classType = booking.getClassType();

        // 1. Get coaches of matching class type for this train that are ACTIVE
        CoachType targetCoachType = mapClassToCoachType(classType);
        List<Coach> coaches = coachRepository.findByTrainIdOrderByCoachPositionAsc(trainId).stream()
                .filter(c -> c.getCoachType() == targetCoachType && c.getStatus() == CoachStatus.ACTIVE)
                .toList();

        if (coaches.isEmpty()) {
            throw new RuntimeException("No active coaches found for class: " + classType);
        }

        // 2. Fetch all physical seats in these coaches
        List<Long> coachIds = coaches.stream().map(Coach::getId).toList();
        List<Seat> allSeats = seatRepository.findAll().stream()
                .filter(s -> coachIds.contains(s.getCoach().getId()))
                .toList();

        // 3. Fetch already allocated seats for this train and travel date
        List<PassengerAllocation> activeAllocations = passengerAllocationRepository
                .findActiveAllocationsByTrainAndDate(trainId, booking.getTravelDate());

        Set<Long> allocatedSeatIds = activeAllocations.stream()
                .map(pa -> pa.getSeat() != null ? pa.getSeat().getId() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 4. Determine available seats
        List<Seat> availableSeats = allSeats.stream()
                .filter(s -> !allocatedSeatIds.contains(s.getId()))
                .collect(Collectors.toList());

        int requestedSeats = passengers.size();

        // If not enough seats, we will assign RAC or Waitlist status (handled in Module 3)
        if (availableSeats.size() < requestedSeats) {
            return handleWaitlistAllocation(booking, passengers, availableSeats.size());
        }

        // 5. Try to group passengers in a single coach if possible
        Map<Long, List<Seat>> seatsByCoach = availableSeats.stream()
                .collect(Collectors.groupingBy(s -> s.getCoach().getId()));

        Long bestCoachId = null;
        for (Map.Entry<Long, List<Seat>> entry : seatsByCoach.entrySet()) {
            if (entry.getValue().size() >= requestedSeats) {
                bestCoachId = entry.getKey();
                break; // Found a coach that can accommodate everyone
            }
        }

        List<Seat> pool = availableSeats;
        if (bestCoachId != null) {
            Long finalBestCoachId = bestCoachId;
            pool = availableSeats.stream()
                    .filter(s -> s.getCoach().getId().equals(finalBestCoachId))
                    .collect(Collectors.toList());
        }

        // 6. Sort passengers by priority (Senior Citizens first, then children, then women)
        List<Passenger> sortedPassengers = new ArrayList<>(passengers);
        sortedPassengers.sort((p1, p2) -> {
            boolean p1Priority = isPriorityPassenger(p1);
            boolean p2Priority = isPriorityPassenger(p2);
            if (p1Priority && !p2Priority) return -1;
            if (!p1Priority && p2Priority) return 1;
            return Integer.compare(p2.getAge(), p1.getAge());
        });

        List<PassengerAllocation> allocations = new ArrayList<>();

        for (Passenger passenger : sortedPassengers) {
            Seat allocatedSeat = findBestSeat(pool, passenger);
            pool.remove(allocatedSeat);

            PassengerAllocation allocation = PassengerAllocation.builder()
                    .booking(booking)
                    .passenger(passenger)
                    .seat(allocatedSeat)
                    .coachNumber(allocatedSeat.getCoach().getCoachNumber())
                    .seatNumber(allocatedSeat.getSeatNumber())
                    .berthType(allocatedSeat.getBerthType())
                    .status("PENDING") // Pending payment
                    .build();

            // Set formatted seatNumber string directly on passenger for backward compatibility
            passenger.setSeatNumber(allocatedSeat.getCoach().getCoachNumber() + "-" +
                    allocatedSeat.getSeatNumber() + " (" + allocatedSeat.getBerthType() + ")");

            allocations.add(passengerAllocationRepository.save(allocation));
        }

        return allocations;
    }

    private Seat findBestSeat(List<Seat> pool, Passenger passenger) {
        if (pool.isEmpty()) {
            throw new RuntimeException("No available seats left in pool");
        }

        // Rule: Senior citizen / Divyang prefers Lower Berth
        boolean prefersLower = passenger.getAge() >= 60 || passenger.getName().toLowerCase().contains("divyang");
        if (prefersLower) {
            Optional<Seat> lowerSeat = pool.stream()
                    .filter(s -> s.getBerthType() == BerthType.LOWER || s.getBerthType() == BerthType.SIDE_LOWER)
                    .findFirst();
            if (lowerSeat.isPresent()) return lowerSeat.get();
        }

        // Rule: Children prefer Window Berth
        boolean prefersWindow = passenger.getAge() < 12;
        if (prefersWindow) {
            Optional<Seat> windowSeat = pool.stream()
                    .filter(Seat::getIsWindow)
                    .findFirst();
            if (windowSeat.isPresent()) return windowSeat.get();
        }

        // Try to allocate lower/middle seats for families
        return pool.get(0);
    }

    private boolean isPriorityPassenger(Passenger p) {
        return p.getAge() >= 60 || p.getAge() < 12 || "F".equalsIgnoreCase(p.getGender());
    }

    private List<PassengerAllocation> handleWaitlistAllocation(Booking booking, List<Passenger> passengers, int availableSeatsCount) {
        waitlistService.addToWaitlist(booking, passengers);
        return passengerAllocationRepository.findByBookingId(booking.getId());
    }

    public static CoachType mapClassToCoachType(ClassType classType) {
        return switch (classType) {
            case SL -> CoachType.SL;
            case _3A -> CoachType._3A;
            case _2A -> CoachType._2A;
            case _1A -> CoachType._1A;
            case CC -> CoachType.CC;
        };
    }
}
