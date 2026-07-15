package com.irctc.irctc_backend.modules.pnr.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PassengerPnrStatus {
    private String name;
    private int age;
    private String gender;
    private String coachNumber;
    private Integer seatNumber;
    private String berthType;
    private String bookingStatus; // Allocation status at booking (e.g. CNF, RAC, WL)
    private String currentStatus; // Current status (promoted or still waitlisted)
}
