package com.irctc.irctc_backend.modules.coach.dto;

import com.irctc.irctc_backend.modules.coach.entity.CoachStatus;
import com.irctc.irctc_backend.modules.coach.entity.CoachType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CoachRequest {

    @NotBlank(message = "Coach number is required")
    private String coachNumber;

    @NotBlank(message = "Coach name is required")
    private String coachName;

    @NotNull(message = "Coach type is required")
    private CoachType coachType;

    @NotNull(message = "Seat capacity is required")
    @Min(value = 1, message = "Seat capacity must be at least 1")
    private Integer seatCapacity;

    private Integer coachPosition;

    @NotNull(message = "Coach status is required")
    private CoachStatus status;
}
