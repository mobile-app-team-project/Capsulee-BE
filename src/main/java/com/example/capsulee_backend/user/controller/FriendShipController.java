package com.example.capsulee_backend.user.controller;

import com.example.capsulee_backend.config.jwt.PrincipalHandler;
import com.example.capsulee_backend.user.dto.request.FriendShipRequestDto;
import com.example.capsulee_backend.user.dto.request.FriendShipUpdateRequestDto;
import com.example.capsulee_backend.user.dto.response.FriendInfoResponseDto;
import com.example.capsulee_backend.user.dto.response.FriendShipResponseDto;
import com.example.capsulee_backend.user.service.FriendShipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/friends")
public class FriendShipController {
    private final FriendShipService friendShipService;

    @GetMapping("")
    public ResponseEntity<List<FriendInfoResponseDto>> getAllFriends() {
        // 토큰에서 내 정보 가져오기
        String userLoginID = PrincipalHandler.getLoginIDFromPrincipal();;

        // 해당 유저의 친구 리스트
        List<FriendInfoResponseDto> responseDtoList = friendShipService.getFriendList(userLoginID);

        return ResponseEntity.ok(responseDtoList);
    }

    @PostMapping("/request")
    public ResponseEntity<FriendShipResponseDto> requestFriendShip(@RequestBody FriendShipRequestDto friendShipRequestDto) {
        // 토큰에서 내 정보 가져오기
        String senderLoginID = PrincipalHandler.getLoginIDFromPrincipal();

        String receiverLoginId = friendShipRequestDto.getReceiverLoginId();

        FriendShipResponseDto friendRequestResponseDto = friendShipService.createFriendShip(
                senderLoginID, receiverLoginId
        );

        return ResponseEntity.ok(friendRequestResponseDto);
    }

    @PutMapping("/response")
    public ResponseEntity<FriendShipResponseDto> updateFriendShip(@RequestBody FriendShipUpdateRequestDto friendShipUpdateRequestDto) {
        return ResponseEntity.ok(friendShipService.updateFriendShip(friendShipUpdateRequestDto));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<FriendInfoResponseDto>> getFriendPending() {
        // 토큰에서 내 정보 가져오기
        String userLoginID = PrincipalHandler.getLoginIDFromPrincipal();;

        // 해당 유저가 친구 요청 받은 리스트
        List<FriendInfoResponseDto> responseDtoList = friendShipService.getPendingList(userLoginID);

        return ResponseEntity.ok(responseDtoList);
    }
}
