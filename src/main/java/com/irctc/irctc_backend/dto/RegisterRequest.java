package com.irctc.irctc_backend.dto;


//import lombok.AllArgsConstructor;
import lombok.Getter;
//import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter

public class RegisterRequest {
    private String name;
    private String email;
    private String password;
}
