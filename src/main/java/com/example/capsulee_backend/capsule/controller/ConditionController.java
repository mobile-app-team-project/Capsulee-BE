package com.example.capsulee_backend.capsule.controller;

import com.example.capsulee_backend.capsule.dto.request.ActionConditionRequestDto;
import com.example.capsulee_backend.capsule.dto.request.LocationConditionRequestDto;
import com.example.capsulee_backend.capsule.dto.response.ActionConditionResponseDto;
import com.example.capsulee_backend.capsule.dto.response.LocationConditionResponseDto;
import com.example.capsulee_backend.capsule.service.ConditionService;
import com.example.capsulee_backend.config.jwt.PrincipalHandler;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ConditionController {

    private final ConditionService conditionService;

    @Operation(
            summary = "조건 달성 (위치, 날씨)",
            description = "조건(위치, 날씨) 달성 여부를 확인합니다. Ready 버튼을 활성화할 수 있는지 반환합니다." +
                    "위치는 좌표로 줘야 합니다."
    )
    @PostMapping("/capsules/{capsuleId}/conditions/location")
    public ResponseEntity<LocationConditionResponseDto> verifyLocationConditions(
            @RequestBody LocationConditionRequestDto request
    ) {
        String loginID = PrincipalHandler.getLoginIDFromPrincipal();
        return ResponseEntity.ok(conditionService.verifyLocationConditions(request, loginID));
    }

    @Operation(
            summary = "조건 달성 (행동)",
            description = "조건(행동) 달성 여부를 확인합니다. (true || false)"
    )
    @PostMapping("/capsules/{capsuleId}/conditions/action")
    public ResponseEntity<ActionConditionResponseDto> updateActionCondition(
            @PathVariable Long capsuleId,
            @RequestBody ActionConditionRequestDto request
    ) {
        String loginID = PrincipalHandler.getLoginIDFromPrincipal();
        return ResponseEntity.ok(conditionService.updateActionCondition(capsuleId, loginID, request));
    }
}
