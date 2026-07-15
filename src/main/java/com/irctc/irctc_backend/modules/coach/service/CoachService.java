package com.irctc.irctc_backend.modules.coach.service;

import com.irctc.irctc_backend.entity.Train;
import com.irctc.irctc_backend.modules.coach.dto.CoachRequest;
import com.irctc.irctc_backend.modules.coach.dto.CoachResponse;
import com.irctc.irctc_backend.modules.coach.entity.Coach;
import com.irctc.irctc_backend.modules.coach.entity.CoachStatus;
import com.irctc.irctc_backend.modules.coach.entity.CoachType;
import com.irctc.irctc_backend.modules.coach.mapper.CoachMapper;
import com.irctc.irctc_backend.modules.coach.repository.CoachRepository;
import com.irctc.irctc_backend.modules.seat.entity.BerthType;
import com.irctc.irctc_backend.modules.seat.entity.Seat;
import com.irctc.irctc_backend.modules.seat.repository.SeatRepository;
import com.irctc.irctc_backend.repository.TrainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CoachService {

    private final CoachRepository coachRepository;
    private final SeatRepository seatRepository;
    private final TrainRepository trainRepository;

    @Transactional
    public CoachResponse createCoach(CoachRequest request) {
        Coach coach = Coach.builder()
                .coachNumber(request.getCoachNumber())
                .coachName(request.getCoachName())
                .coachType(request.getCoachType())
                .seatCapacity(request.getSeatCapacity())
                .coachPosition(request.getCoachPosition())
                .status(request.getStatus())
                .build();
        return CoachMapper.toDto(coachRepository.save(coach));
    }

    @Transactional
    public CoachResponse updateCoach(Long id, CoachRequest request) {
        Coach coach = coachRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coach not found with ID: " + id));
        
        coach.setCoachNumber(request.getCoachNumber());
        coach.setCoachName(request.getCoachName());
        coach.setCoachType(request.getCoachType());
        
        // If capacity changes, we might need to regenerate seats (only if attached)
        boolean capacityChanged = !coach.getSeatCapacity().equals(request.getSeatCapacity());
        coach.setSeatCapacity(request.getSeatCapacity());
        coach.setCoachPosition(request.getCoachPosition());
        coach.setStatus(request.getStatus());

        Coach updated = coachRepository.save(coach);
        if (capacityChanged && updated.getTrain() != null) {
            // Regenerate seats
            seatRepository.deleteByCoachId(updated.getId());
            generateSeatsForCoach(updated);
        }

        return CoachMapper.toDto(updated);
    }

    @Transactional
    public void deleteCoach(Long id) {
        Coach coach = coachRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coach not found with ID: " + id));
        seatRepository.deleteByCoachId(coach.getId());
        coachRepository.delete(coach);
    }

    public CoachResponse getCoachById(Long id) {
        Coach coach = coachRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coach not found with ID: " + id));
        return CoachMapper.toDto(coach);
    }

    public List<CoachResponse> getAllCoaches() {
        return coachRepository.findAll().stream()
                .map(CoachMapper::toDto)
                .toList();
    }

    @Transactional
    public CoachResponse attachCoachToTrain(Long coachId, Long trainId, Integer position) {
        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new RuntimeException("Coach not found"));
        Train train = trainRepository.findById(trainId)
                .orElseThrow(() -> new RuntimeException("Train not found"));

        if (coach.getTrain() != null) {
            throw new RuntimeException("Coach is already attached to train: " + coach.getTrain().getTrainNumber());
        }

        coach.setTrain(train);
        coach.setCoachPosition(position);
        Coach saved = coachRepository.save(coach);

        // Generate physical seats in DB automatically
        generateSeatsForCoach(saved);

        return CoachMapper.toDto(saved);
    }

    @Transactional
    public CoachResponse detachCoachFromTrain(Long coachId) {
        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new RuntimeException("Coach not found"));

        if (coach.getTrain() == null) {
            throw new RuntimeException("Coach is not attached to any train");
        }

        // Delete generated seats from seat inventory
        seatRepository.deleteByCoachId(coach.getId());

        coach.setTrain(null);
        coach.setCoachPosition(null);
        return CoachMapper.toDto(coachRepository.save(coach));
    }

    private void generateSeatsForCoach(Coach coach) {
        CoachType type = coach.getCoachType();
        int capacity = coach.getSeatCapacity();

        for (int seatNum = 1; seatNum <= capacity; seatNum++) {
            BerthType berthType = determineBerthType(type, seatNum);
            boolean isWindow = determineIsWindow(type, seatNum, berthType);

            Seat seat = Seat.builder()
                    .coach(coach)
                    .seatNumber(seatNum)
                    .berthType(berthType)
                    .isWindow(isWindow)
                    .build();
            seatRepository.save(seat);
        }
    }

    private BerthType determineBerthType(CoachType type, int seatNum) {
        if (type == CoachType.SL || type == CoachType._3A) {
            // 8 berths per compartment: 1-Lower, 2-Middle, 3-Upper, 4-Lower, 5-Middle, 6-Upper, 7-Side Lower, 8-Side Upper
            int mod = seatNum % 8;
            if (mod == 1 || mod == 4) return BerthType.LOWER;
            if (mod == 2 || mod == 5) return BerthType.MIDDLE;
            if (mod == 3 || mod == 6) return BerthType.UPPER;
            if (mod == 7) return BerthType.SIDE_LOWER;
            return BerthType.SIDE_UPPER; // mod == 0
        } else if (type == CoachType._2A) {
            // 6 berths per compartment: 1-Lower, 2-Upper, 3-Lower, 4-Upper, 5-Side Lower, 6-Side Upper
            int mod = seatNum % 6;
            if (mod == 1 || mod == 3) return BerthType.LOWER;
            if (mod == 2 || mod == 4) return BerthType.UPPER;
            if (mod == 5) return BerthType.SIDE_LOWER;
            return BerthType.SIDE_UPPER; // mod == 0
        } else if (type == CoachType._1A) {
            // 4 berths per cabin/coupe: LOWER and UPPER
            return (seatNum % 2 == 1) ? BerthType.LOWER : BerthType.UPPER;
        } else if (type == CoachType.CC || type == CoachType.EC) {
            // Chair car arrangement (usually 3+2 or 2+2).
            // Let's assume 3+2 layout: 1-Window, 2-Aisle, 3-Middle, 4-Aisle, 5-Window
            int mod = seatNum % 5;
            if (mod == 1 || mod == 0) return BerthType.WINDOW;
            if (mod == 3) return BerthType.MIDDLE;
            return BerthType.AISLE; // mod == 2 or 4
        } else {
            // General / Unreserved: Window vs Aisle
            return (seatNum % 2 == 1) ? BerthType.WINDOW : BerthType.AISLE;
        }
    }

    private boolean determineIsWindow(CoachType type, int seatNum, BerthType berthType) {
        if (type == CoachType.SL || type == CoachType._3A || type == CoachType._2A) {
            // Side lower/upper are window facing
            return berthType == BerthType.SIDE_LOWER || berthType == BerthType.SIDE_UPPER;
        } else if (type == CoachType._1A) {
            // Lower berths are usually near window
            return berthType == BerthType.LOWER;
        } else {
            return berthType == BerthType.WINDOW;
        }
    }
}
