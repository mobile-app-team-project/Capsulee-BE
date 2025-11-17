package com.example.capsulee_backend.user.controller;

import com.example.capsulee_backend.config.jwt.PrincipalHandler;
import com.example.capsulee_backend.user.dto.response.FriendRequestResponseDto;
import com.example.capsulee_backend.user.service.FriendShipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/friends")
public class FriendShipController {
    private final FriendShipService friendShipService;

    @PostMapping("/request")
    public ResponseEntity<FriendRequestResponseDto> requestFriendShip(@RequestBody String receiverLoginId) {
        // 토큰에서 내 정보 가져오기
        String senderLoginID = PrincipalHandler.getLoginIDFromPrincipal();

        FriendRequestResponseDto friendRequestResponseDto = friendShipService.createFriendShip(
                senderLoginID, receiverLoginId
        );

        return ResponseEntity.ok(friendRequestResponseDto);
    }
}
