package com.example.capsulee_backend.user.dto.request;

import com.example.capsulee_backend.user.domain.User;
import lombok.Getter;

@Getter
public class JoinRequestDto {
    private String loginID;
    private String password;
    private String username;
}
