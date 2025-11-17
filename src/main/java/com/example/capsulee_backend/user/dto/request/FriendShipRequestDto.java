package com.example.capsulee_backend.user.dto.request;

import com.example.capsulee_backend.user.domain.FriendRequest;
import lombok.Getter;

@Getter
public class FriendShipRequestDto {
    private String receiverLoginId;
}
