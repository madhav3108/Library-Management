package com.example.libraryManagement.LibraryManagement.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Builder

public class LoginResponseDto {
    private String message;
    private String token;

    public LoginResponseDto(String message, String token) {
        this.message = message;
        this.token = token;
    }

    // Constructor with only message
    public LoginResponseDto(String message) {
        this.message = message;
    }
}
