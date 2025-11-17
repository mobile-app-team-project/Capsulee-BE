package com.example.capsulee_backend.user.dto.response;

import com.example.capsulee_backend.user.domain.FriendRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FriendRequestResponseDto {
    public String loginId;
    public FriendRequest status;
    public String senderLoginId;
    public String receiverLoginId;
}
