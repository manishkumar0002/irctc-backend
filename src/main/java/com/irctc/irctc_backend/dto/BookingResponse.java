package com.irctc.irctc_backend.dto;

import java.time.LocalDate;

public record BookingResponse (
        Long id,
        String pnr,
        String trainNumber,
        String trainName,
        LocalDate travelDate,
        String classType,
        int seatCount,
        String status,
        String sourceStationCode,
        String destinationStationCode
) {}