package com.example.capsulee_backend.capsule.controller;

import com.example.capsulee_backend.capsule.dto.request.CreateCapsuleRequestDto;
import com.example.capsulee_backend.capsule.dto.response.CreateCapsuleResponseDto;
import com.example.capsulee_backend.capsule.dto.response.CapsuleListResponseDto;
import com.example.capsulee_backend.capsule.service.CapsuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/capsules")
@RequiredArgsConstructor
public class CapsuleController {

    private final CapsuleService capsuleService;

    @PostMapping("")
    public ResponseEntity<CreateCapsuleResponseDto> createCapsule(
            Authentication authentication,
            @RequestBody CreateCapsuleRequestDto request
    ) {
        String loginID = authentication.getName();
        CreateCapsuleResponseDto response = capsuleService.createCapsule(request, loginID);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 캡슐 목록 조회
    @GetMapping("")
    public ResponseEntity<CapsuleListResponseDto> getCapsules(
            Authentication authentication,
            @RequestParam("type") String type
    ) {
        String loginID = authentication.getName();
        CapsuleListResponseDto capsules = capsuleService.getCapsules(loginID, type);
        return ResponseEntity.status(HttpStatus.OK).body(capsules);
    }
}
