package com.irctc.irctc_backend.dto;

import com.irctc.irctc_backend.entity.ClassType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class BookingRequest {

    private Long trainId;
    private String sourceStationCode;
    private String destinationStationCode;
    private LocalDate travelDate;
    private ClassType classType;

    // 🔥 passenger list (mandatory)
    private List<PassengerRequest> passengers;
}
