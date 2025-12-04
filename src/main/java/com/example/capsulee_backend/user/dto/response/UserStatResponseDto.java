package com.example.capsulee_backend.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserStatResponseDto {
    private int total;
    private int opened;
    private int friends;
}
