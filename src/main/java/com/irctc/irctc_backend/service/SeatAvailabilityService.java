package com.irctc.irctc_backend.service;

import com.irctc.irctc_backend.entity.ClassType;
import com.irctc.irctc_backend.entity.SeatAvailability;
import com.irctc.irctc_backend.entity.Train;
import com.irctc.irctc_backend.repository.SeatAvailabilityRepository;
import com.irctc.irctc_backend.repository.TrainRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class SeatAvailabilityService {

    private final SeatAvailabilityRepository seatAvailabilityRepository;
    private final TrainRepository trainRepository;

    public SeatAvailabilityService(
            SeatAvailabilityRepository seatAvailabilityRepository,
            TrainRepository trainRepository
    ) {
        this.seatAvailabilityRepository = seatAvailabilityRepository;
        this.trainRepository = trainRepository;
    }

    // ADMIN (manual init – optional)
    public SeatAvailability initializeSeats(
            Long trainId,
            LocalDate travelDate,
            ClassType classType,
            int seats
    ) {
        Train train = trainRepository.findById(trainId)
                .orElseThrow(() -> new RuntimeException("Train not found"));

        seatAvailabilityRepository
                .findByTrainAndTravelDateAndClassType(train, travelDate, classType)
                .ifPresent(sa -> {
                    throw new RuntimeException("Seats already initialized");
                });

        SeatAvailability sa = new SeatAvailability();
        sa.setTrain(train);
        sa.setTravelDate(travelDate);
        sa.setClassType(classType);
        sa.setAvailableSeats(seats);

        return seatAvailabilityRepository.save(sa);
    }

    //  AUTO CREATE
    @Transactional
    public SeatAvailability getOrCreateAvailability(
            Train train,
            LocalDate travelDate,
            ClassType classType
    ) {
        return seatAvailabilityRepository
                .lockSeatForUpdate(train, travelDate, classType)
                .orElseGet(() -> {
                    SeatAvailability sa = new SeatAvailability();
                    sa.setTrain(train);
                    sa.setTravelDate(travelDate);
                    sa.setClassType(classType);
                    sa.setAvailableSeats(train.getTotalSeats()); // default seats
                    return seatAvailabilityRepository.save(sa);
                });
    }
}
