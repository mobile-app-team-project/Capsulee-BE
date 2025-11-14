package com.example.capsulee_backend.user.controller;

import com.example.capsulee_backend.user.dto.request.JoinRequestDto;
import com.example.capsulee_backend.user.dto.request.LoginRequestDto;
import com.example.capsulee_backend.user.dto.response.LoginResponseDto;
import com.example.capsulee_backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public void join (@RequestBody JoinRequestDto joinRequestDto) {
        userService.join(joinRequestDto);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login (@RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok(userService.login(loginRequestDto));
    }
}
