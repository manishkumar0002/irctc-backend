package com.irctc.irctc_backend.modules.chart.service;

import com.irctc.irctc_backend.entity.ClassType;
import com.irctc.irctc_backend.entity.Train;
import com.irctc.irctc_backend.modules.chart.entity.ReservationChart;
import com.irctc.irctc_backend.modules.chart.repository.ReservationChartRepository;
import com.irctc.irctc_backend.modules.coach.entity.Coach;
import com.irctc.irctc_backend.modules.coach.entity.CoachStatus;
import com.irctc.irctc_backend.modules.coach.entity.CoachType;
import com.irctc.irctc_backend.modules.coach.repository.CoachRepository;
import com.irctc.irctc_backend.modules.seat.entity.PassengerAllocation;
import com.irctc.irctc_backend.modules.seat.entity.Seat;
import com.irctc.irctc_backend.modules.seat.repository.PassengerAllocationRepository;
import com.irctc.irctc_backend.modules.seat.repository.SeatRepository;
import com.irctc.irctc_backend.modules.seat.service.SeatAllocationService;
import com.irctc.irctc_backend.modules.waitlist.service.WaitlistService;
import com.irctc.irctc_backend.repository.TrainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChartService {

    private final ReservationChartRepository reservationChartRepository;
    private final TrainRepository trainRepository;
    private final CoachRepository coachRepository;
    private final SeatRepository seatRepository;
    private final PassengerAllocationRepository passengerAllocationRepository;
    private final WaitlistService waitlistService;

    @Transactional
    public ReservationChart prepareChart(Long trainId, LocalDate travelDate) {
        Train train = trainRepository.findById(trainId)
                .orElseThrow(() -> new RuntimeException("Train not found"));

        ReservationChart chart = reservationChartRepository.findByTrainIdAndTravelDate(trainId, travelDate)
                .orElse(ReservationChart.builder()
                        .train(train)
                        .travelDate(travelDate)
                        .chartStatus("PENDING")
                        .build());

        if ("PREPARED".equals(chart.getChartStatus())) {
            throw new RuntimeException("Chart is already prepared for train " + train.getTrainNumber() + " on " + travelDate);
        }

        // For each class type (SL, 3A, 2A, 1A, CC), promote waitlisted/RAC passengers to vacant seats
        for (ClassType classType : ClassType.values()) {
            CoachType targetCoachType = SeatAllocationService.mapClassToCoachType(classType);

            // Fetch active coaches
            List<Coach> coaches = coachRepository.findByTrainIdOrderByCoachPositionAsc(trainId).stream()
                    .filter(c -> c.getCoachType() == targetCoachType && c.getStatus() == CoachStatus.ACTIVE)
                    .toList();

            if (coaches.isEmpty()) continue;

            List<Long> coachIds = coaches.stream().map(Coach::getId).toList();

            // Fetch all seats in these coaches
            List<Seat> classSeats = seatRepository.findAll().stream()
                    .filter(s -> coachIds.contains(s.getCoach().getId()))
                    .toList();

            // Fetch occupied seats
            List<PassengerAllocation> activeAllocations = passengerAllocationRepository
                    .findActiveAllocationsByTrainAndDate(trainId, travelDate);

            Set<Long> occupiedSeatIds = activeAllocations.stream()
                    .map(pa -> pa.getSeat() != null ? pa.getSeat().getId() : null)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            // Get vacant seats
            List<Seat> vacantSeats = classSeats.stream()
                    .filter(s -> !occupiedSeatIds.contains(s.getId()))
                    .collect(Collectors.toList());

            // Promote queue passengers for each vacant seat
            for (Seat vacantSeat : vacantSeats) {
                waitlistService.promoteWaitlistedPassengers(trainId, travelDate, classType, vacantSeat);
            }
        }

        chart.setChartStatus("PREPARED");
        chart.setPreparedAt(LocalDateTime.now());
        return reservationChartRepository.save(chart);
    }

    public ReservationChart getChartStatus(Long trainId, LocalDate travelDate) {
        return reservationChartRepository.findByTrainIdAndTravelDate(trainId, travelDate)
                .orElse(ReservationChart.builder()
                        .chartStatus("PENDING")
                        .build());
    }
}
