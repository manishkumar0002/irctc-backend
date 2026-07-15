package com.irctc.irctc_backend.modules.tracking.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrainLiveLocation {
    private Long trainId;
    private String trainNumber;
    private String trainName;
    private String status; // NOT_STARTED, RUNNING, ARRIVED
    private String lastStationCode;
    private String nextStationCode;
    private Double latitude;
    private Double longitude;
    private Double currentSpeedKmph;
    private Integer expectedDelayMinutes;
}
