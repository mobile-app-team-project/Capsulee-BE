package com.example.capsulee_backend.user.dto.response;

import com.example.capsulee_backend.user.domain.FriendRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OtherUserInfoResponseDto {
    private Long id;
    private String loginID;
    private String username;
    private FriendRequest status;
}
