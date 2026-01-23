package com.irctc.irctc_backend.Mapper;

import com.irctc.irctc_backend.dto.AdminBookingResponse;
import com.irctc.irctc_backend.entity.Booking;

public class AdminBookingMapper {

    public static AdminBookingResponse toDto(Booking b) {
        return new AdminBookingResponse(
                b.getId(),
                b.getPnr(),
                b.getUser().getEmail(),
                b.getTrain().getTrainNumber(),
                b.getTravelDate(),
                b.getStatus()
        );
    }
}
