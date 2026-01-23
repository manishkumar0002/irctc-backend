package com.irctc.irctc_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PassengerRequest {
    private String name;
    private int age;
    private String gender;
}
