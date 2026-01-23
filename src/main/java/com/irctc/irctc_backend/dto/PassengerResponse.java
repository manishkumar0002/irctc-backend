package com.irctc.irctc_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PassengerResponse {
    private Long id;
    private String name;
    private int age;
    private String gender;
    private String seatNumber;
}
