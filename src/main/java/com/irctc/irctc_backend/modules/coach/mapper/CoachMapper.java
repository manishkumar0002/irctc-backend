package com.irctc.irctc_backend.modules.coach.mapper;

import com.irctc.irctc_backend.modules.coach.dto.CoachResponse;
import com.irctc.irctc_backend.modules.coach.entity.Coach;

public class CoachMapper {

    public static CoachResponse toDto(Coach coach) {
        if (coach == null) return null;
        return CoachResponse.builder()
                .id(coach.getId())
                .coachNumber(coach.getCoachNumber())
                .coachName(coach.getCoachName())
                .coachType(coach.getCoachType())
                .seatCapacity(coach.getSeatCapacity())
                .coachPosition(coach.getCoachPosition())
                .status(coach.getStatus())
                .trainId(coach.getTrain() != null ? coach.getTrain().getId() : null)
                .trainNumber(coach.getTrain() != null ? coach.getTrain().getTrainNumber() : null)
                .build();
    }
}
