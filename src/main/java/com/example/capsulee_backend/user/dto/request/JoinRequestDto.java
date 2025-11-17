package com.example.capsulee_backend.user.dto.request;

import com.example.capsulee_backend.user.domain.User;
import lombok.Getter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Getter
public class JoinRequestDto {
    private String loginID;
    private String password;
    private String username;

    public User toEntity(BCryptPasswordEncoder bCryptPasswordEncoder) {
        return User.builder()
                .loginID(loginID)
                .password(bCryptPasswordEncoder.encode(password))
                .username(username)
                .isOkAlarm(true)
                .build();
    }
}
