package com.example.capsulee_backend.capsule.controller;

import com.example.capsulee_backend.capsule.dto.request.CapsuleCreateRequest;
import com.example.capsulee_backend.capsule.dto.response.CapsuleCreateResponse;
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
    public ResponseEntity<CapsuleCreateResponse> createCapsule(
            Authentication authentication,
            @RequestBody CapsuleCreateRequest request
    ) {
        String loginID = authentication.getName();
        CapsuleCreateResponse response = capsuleService.createCapsule(request, loginID);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
