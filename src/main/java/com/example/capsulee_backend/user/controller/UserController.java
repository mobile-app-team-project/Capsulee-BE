package com.example.capsulee_backend.user.controller;

import com.example.capsulee_backend.config.jwt.PrincipalHandler;
import com.example.capsulee_backend.user.domain.User;
import com.example.capsulee_backend.user.dto.request.UserUpdateRequestDto;
import com.example.capsulee_backend.user.dto.response.UserInfoResponseDto;
import com.example.capsulee_backend.user.dto.response.UserSearchResponseDto;
import com.example.capsulee_backend.user.dto.response.UserUpdateResponseDto;
import com.example.capsulee_backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/me")
    public ResponseEntity<UserUpdateResponseDto> updateUserInfo(@RequestBody UserUpdateRequestDto userUpdateRequestDto) {
        // 토큰에서 내 정보 가져오기
        String userLoginID = PrincipalHandler.getLoginIDFromPrincipal();;
        User user = userService.getUserByLoginID(userLoginID);

        return ResponseEntity.ok(userService.updateUserInfo(user, userUpdateRequestDto));
    }

    @GetMapping("/search")
    public ResponseEntity<UserSearchResponseDto> searchUser(@RequestParam(required = false) Long id) {
        User user = userService.getUserById(id);

        if (user == null) { // 유저가 없으면
            return ResponseEntity.ok(null);
        }
        // 유저가 있으면
        return ResponseEntity.ok(new UserSearchResponseDto(user.getLoginID(), user.getUsername()));
    }
}
