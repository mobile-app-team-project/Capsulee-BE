package com.example.capsulee_backend.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoResponseDto {
    private Long id;
    private String loginID;
    private String username;
    private boolean isOkAlarm;
    private UserStatResponseDto stat;
}
