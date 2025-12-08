package com.example.capsulee_backend.capsule.controller;

import com.example.capsulee_backend.capsule.dto.request.CreateCapsuleRequestDto;
import com.example.capsulee_backend.capsule.dto.response.CapsuleDetailResponseDto;
import com.example.capsulee_backend.capsule.dto.response.CreateCapsuleResponseDto;
import com.example.capsulee_backend.capsule.dto.response.CapsuleListResponseDto;
import com.example.capsulee_backend.capsule.service.CapsuleService;
import com.example.capsulee_backend.weather.service.OpenWeatherService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/capsules")
@RequiredArgsConstructor
public class CapsuleController {

    private final CapsuleService capsuleService;
    private final OpenWeatherService weatherService;

    @Operation(
            summary = "캡슐 생성",
            description = "캡슐을 생성하고, 조건과 수신자 정보를 함께 저장합니다." +
                    "위치(LOCATION)는 '위도, 경도, 장소명'의 형식으로 작성해야 합니다" +
                    "날씨(WEATHER)는 CLEAR | CLOUD | RAINY | SNOW 여야 합니다" +
                    "행동(ACTION)은 자유롭게 작성해주세요"
    )
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CreateCapsuleResponseDto> createCapsule(
            Authentication authentication,
            @RequestPart("data") CreateCapsuleRequestDto request,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        String loginID = authentication.getName();
        CreateCapsuleResponseDto response = capsuleService.createCapsule(request, loginID, imageFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 캡슐 목록 조회
    @Operation(
            summary = "캡슐 목록 조회",
            description = "캡슐 목록을 조회합니다. type을 받습니다.(sent | received)"
    )
    @GetMapping("")
    public ResponseEntity<CapsuleListResponseDto> getCapsules(
            Authentication authentication,
            @RequestParam("type") String type
    ) {
        String loginID = authentication.getName();
        CapsuleListResponseDto capsules = capsuleService.getCapsules(loginID, type);
        return ResponseEntity.status(HttpStatus.OK).body(capsules);
    }

    // 캡슐 상세 조회
    @Operation(
            summary = "캡슐 상세 조회",
            description = "캡슐 상세 정보를 조회합니다."
    )
    @GetMapping("/{capsuleId}")
    public ResponseEntity<CapsuleDetailResponseDto> getCapsule(
            Authentication authentication,
            @PathVariable("capsuleId") Long capsuleId
    ) {
        String loginID = authentication.getName();
        CapsuleDetailResponseDto capsuleDetail = capsuleService.getCapsule(loginID, capsuleId);
        return ResponseEntity.status(HttpStatus.OK).body(capsuleDetail);
    }
    @GetMapping("/home")
    public ResponseEntity<CapsuleDetailResponseDto> getLatestCapsule(
            Authentication authentication
    ) {
        String loginID = authentication.getName();
        Long capsuleId = capsuleService.getLatestCapsuleId(loginID);
        if (capsuleId == null) { // 아직 아무런 캡슐이 없는 경우
            return ResponseEntity.noContent().build();
        }
        CapsuleDetailResponseDto capsuleDetail = capsuleService.getCapsule(loginID, capsuleId);
        return ResponseEntity.status(HttpStatus.OK).body(capsuleDetail);
    }
}
