package com.example.capsulee_backend.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FriendAcceptedInfoResponseDto {
    private Long friendShipId;
    private Long friendId;
    private String friendLoginId;
    private String friendUsername;
}
