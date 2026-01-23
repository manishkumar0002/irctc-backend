package com.irctc.irctc_backend.dto;

import java.time.LocalDate;

public record AdminBooking(
        Long bookingId,
        String userEmail,
        String trainNumber,
        LocalDate travelDate,
        String classType,
        int seatCount,
        String status,
        String pnr
) {}
