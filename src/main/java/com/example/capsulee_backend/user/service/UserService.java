package com.example.capsulee_backend.user.service;

import com.example.capsulee_backend.user.domain.User;
import com.example.capsulee_backend.user.dto.request.JoinRequestDto;
import com.example.capsulee_backend.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 회원가입
    public void join(JoinRequestDto joinRequestDto) {
        // 해당 name이 이미 존재하는 경우
        if (userRepository.existsByLoginID(joinRequestDto.getLoginID())) {
            // 로그인할 수 없음
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        // 유저 객체 생성
        User user = joinRequestDto.toEntity(bCryptPasswordEncoder);

        // 유저 정보 저장
        userRepository.save(user);
    }
}
