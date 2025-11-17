package com.example.capsulee_backend.user.service;

import com.example.capsulee_backend.config.jwt.JwtTokenProvider;
import com.example.capsulee_backend.user.domain.User;
import com.example.capsulee_backend.user.dto.request.JoinRequestDto;
import com.example.capsulee_backend.user.dto.request.LoginRequestDto;
import com.example.capsulee_backend.user.dto.response.LoginResponseDto;
import com.example.capsulee_backend.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

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

    // 로그인
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        // 해당 ID로 유저를 찾음
        User user = userRepository.findByLoginID(loginRequestDto.getLoginID())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 ID입니다."));

        // 비밀번호 일치 확인
        if (!bCryptPasswordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 토큰 발급
        String accessToken = jwtTokenProvider.generateAccessToken(user.getLoginID());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getLoginID());

        return new LoginResponseDto(accessToken, refreshToken);
    }

    public User getUserByLoginID(String loginID) {
        User user = userRepository.findByLoginID(loginID)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 ID입니다."));
        return user;
    }
}
