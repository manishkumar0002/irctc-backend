package com.irctc.irctc_backend.Mapper;



import com.irctc.irctc_backend.dto.BookingResponse;
import com.irctc.irctc_backend.entity.Booking;

public class BookingMapper {

    public static BookingResponse toDto(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getPnr(),
                booking.getTrain().getTrainNumber(),
                booking.getTrain().getTrainName(),
                booking.getTravelDate(),
                booking.getClassType().name(),
                booking.getSeatCount(),
                booking.getStatus(),
                booking.getSourceStationCode(),
                booking.getDestinationStationCode()
        );
    }
}
