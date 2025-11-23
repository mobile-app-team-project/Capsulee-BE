package com.example.capsulee_backend.user.controller;

import com.example.capsulee_backend.config.jwt.PrincipalHandler;
import com.example.capsulee_backend.user.domain.User;
import com.example.capsulee_backend.user.dto.response.UserInfoResponseDto;
import com.example.capsulee_backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserInfoResponseDto> getUserInfo() {
        // 토큰에서 내 정보 가져오기
        String userLoginID = PrincipalHandler.getLoginIDFromPrincipal();;
        User user = userService.getUserByLoginID(userLoginID);

        UserInfoResponseDto userInfoResponseDto = userService.getUserInfo(user);
        return ResponseEntity.ok(userInfoResponseDto);
    }
}
