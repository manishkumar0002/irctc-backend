package com.irctc.irctc_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainRequest {

    private String trainNumber;
    private String trainName;
    private Integer totalSeats;
}
