package com.irctc.irctc_backend.modules.pnr.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class PnrStatusResponse {
    private String pnr;
    private String trainNumber;
    private String trainName;
    private LocalDate travelDate;
    private String classType;
    private String fromStationCode;
    private String toStationCode;
    private String bookingStatus; // PAYMENT_PENDING, CONFIRMED, CANCELLED
    private String chartStatus;    // PENDING, PREPARED
    private List<PassengerPnrStatus> passengers;
}
