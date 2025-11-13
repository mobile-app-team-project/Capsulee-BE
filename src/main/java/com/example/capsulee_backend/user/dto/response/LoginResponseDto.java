package com.example.capsulee_backend.user.dto.response;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LoginResponseDto {
    private String accessToken;
    private String refreshToken;
}