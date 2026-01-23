package com.irctc.irctc_backend.controller;

import com.irctc.irctc_backend.entity.ClassType;
import com.irctc.irctc_backend.entity.SeatAvailability;
import com.irctc.irctc_backend.service.SeatAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/seats")
public class AdminSeatController {

    private final SeatAvailabilityService seatService;


    @PostMapping("/init")
    public SeatAvailability initializeSeats(
            @RequestParam Long trainId,
            @RequestParam LocalDate travelDate,
            @RequestParam String classType,
            @RequestParam int seats
    ) {
        ClassType type = mapClassType(classType);

        return seatService.initializeSeats(
                trainId,
                travelDate,
                type,
                seats
        );
    }

    //  SAFE ENUM MAPPING
    private ClassType mapClassType(String classType) {
        return switch (classType.toUpperCase()) {
            case "SL" -> ClassType.SL;
            case "3A" -> ClassType._3A;
            case "2A" -> ClassType._2A;
            case "1A" -> ClassType._1A;
            case "CC" -> ClassType.CC;
            default -> throw new RuntimeException(
                    "Invalid class type: " + classType
            );
        };
    }
}
