package com.example.capsulee_backend.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FriendPendingResponseDto {
    private Long friendShipId;
    private Long senderId;
    private String senderLoginId;
    private String senderUsername;
}
