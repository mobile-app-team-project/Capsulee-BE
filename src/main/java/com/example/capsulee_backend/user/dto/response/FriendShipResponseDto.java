package com.example.capsulee_backend.user.dto.response;

import com.example.capsulee_backend.user.domain.FriendRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FriendShipResponseDto {
    public Long friendShipId;
    public FriendRequest status;
    public String senderLoginId;
    public String receiverLoginId;
}
