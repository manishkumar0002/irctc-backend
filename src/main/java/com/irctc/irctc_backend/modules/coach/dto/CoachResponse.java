package com.irctc.irctc_backend.modules.coach.dto;

import com.irctc.irctc_backend.modules.coach.entity.CoachStatus;
import com.irctc.irctc_backend.modules.coach.entity.CoachType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CoachResponse {
    private Long id;
    private String coachNumber;
    private String coachName;
    private CoachType coachType;
    private Integer seatCapacity;
    private Integer coachPosition;
    private CoachStatus status;
    private Long trainId;
    private String trainNumber;
}
