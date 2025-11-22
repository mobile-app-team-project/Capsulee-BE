package com.example.capsulee_backend.user.dto.response;

import com.example.capsulee_backend.user.domain.FriendRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FriendShipResponseDto {
    private Long friendShipId;
    private FriendRequest status;
    private String senderLoginId;
    private String receiverLoginId;
}
