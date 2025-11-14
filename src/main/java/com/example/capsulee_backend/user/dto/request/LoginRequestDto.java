package com.example.capsulee_backend.user.dto.request;

import lombok.Getter;

@Getter
public class LoginRequestDto {
    private String loginID;
    private String password;
}
