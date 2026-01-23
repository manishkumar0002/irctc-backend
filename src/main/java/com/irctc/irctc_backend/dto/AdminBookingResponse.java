package com.irctc.irctc_backend.dto;

import java.time.LocalDate;

public record AdminBookingResponse(
        Long id,
        String pnr,
        String userEmail,
        String trainNumber,
        LocalDate travelDate,
        String status
) {}

