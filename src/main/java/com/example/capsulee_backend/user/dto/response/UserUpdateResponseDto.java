package com.example.capsulee_backend.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserUpdateResponseDto {
    private String loginID;
    private String username;
}
