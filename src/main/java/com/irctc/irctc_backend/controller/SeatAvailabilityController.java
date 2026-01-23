package com.irctc.irctc_backend.controller;

import com.irctc.irctc_backend.entity.ClassType;
import com.irctc.irctc_backend.entity.SeatAvailability;
import com.irctc.irctc_backend.entity.Train;
import com.irctc.irctc_backend.repository.TrainRepository;
import com.irctc.irctc_backend.service.SeatAvailabilityService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/seat-availability")
public class SeatAvailabilityController {

    private final TrainRepository trainRepository;
    private final SeatAvailabilityService seatAvailabilityService;

    public SeatAvailabilityController(
            TrainRepository trainRepository,
            SeatAvailabilityService seatAvailabilityService
    ) {
        this.trainRepository = trainRepository;
        this.seatAvailabilityService = seatAvailabilityService;
    }

    @GetMapping
    public SeatAvailability getAvailability(
            @RequestParam Long trainId,
            @RequestParam LocalDate travelDate,
            @RequestParam ClassType classType
    ) {
        Train train = trainRepository.findById(trainId)
                .orElseThrow(() -> new RuntimeException("Train not found"));

        return seatAvailabilityService.getOrCreateAvailability(
                train, travelDate, classType
        );
    }
}
