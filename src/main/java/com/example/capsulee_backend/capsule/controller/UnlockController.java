package com.example.capsulee_backend.capsule.controller;

import com.example.capsulee_backend.capsule.domain.Capsule;
import com.example.capsulee_backend.capsule.dto.request.ReadyStatusRequestDto;
import com.example.capsulee_backend.capsule.dto.response.CapsuleParticipantsResponseDto;
import com.example.capsulee_backend.capsule.dto.response.ReadyStatusResponseDto;
import com.example.capsulee_backend.capsule.service.CapsuleService;
import com.example.capsulee_backend.capsule.service.UnlockService;
import com.example.capsulee_backend.config.jwt.PrincipalHandler;
import com.example.capsulee_backend.user.domain.User;
import com.example.capsulee_backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/unlock")
@RequiredArgsConstructor
public class UnlockController {

    private final UserService userService;
    private final CapsuleService capsuleService;
    private final UnlockService unlockService;

    @PostMapping("/ready/{capsuleId}")
    public ResponseEntity<ReadyStatusResponseDto> changeReadyStatus(
            @PathVariable Long capsuleId,
            @RequestBody ReadyStatusRequestDto readyStatusRequestDto
    ) {
        // 토큰에서 내 정보 가져오기
        String userLoginID = PrincipalHandler.getLoginIDFromPrincipal();;
        User user = userService.getUserByLoginID(userLoginID);

        // 캡슐 정보 가져오기
        Capsule capsule = capsuleService.getCapsuleById(capsuleId);

        // ready 상태 업데이트
        ReadyStatusResponseDto readyStatusResponseDto = unlockService.changeReadyStatus(capsule, user, readyStatusRequestDto);
        return ResponseEntity.ok(readyStatusResponseDto);
    }

    @GetMapping("/check/{capsuleId}")
    public ResponseEntity<CapsuleParticipantsResponseDto> getCapsuleParticipants(
            @PathVariable Long capsuleId
    ) {
        // 캡슐 정보 가져오기
        Capsule capsule = capsuleService.getCapsuleById(capsuleId);

        return ResponseEntity.ok(unlockService.getCapsuleParticipants(capsule));
    }
}
