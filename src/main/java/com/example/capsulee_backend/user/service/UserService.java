package com.example.capsulee_backend.user.service;

import com.example.capsulee_backend.capsule.domain.Reception;
import com.example.capsulee_backend.capsule.repository.ReceptionRepository;
import com.example.capsulee_backend.config.jwt.JwtTokenProvider;
import com.example.capsulee_backend.user.domain.User;
import com.example.capsulee_backend.user.dto.request.JoinRequestDto;
import com.example.capsulee_backend.user.dto.request.LoginRequestDto;
import com.example.capsulee_backend.user.dto.request.UserUpdateRequestDto;
import com.example.capsulee_backend.user.dto.response.LoginResponseDto;
import com.example.capsulee_backend.user.dto.response.UserInfoResponseDto;
import com.example.capsulee_backend.user.dto.response.UserStatResponseDto;
import com.example.capsulee_backend.user.dto.response.UserUpdateResponseDto;
import com.example.capsulee_backend.user.repository.FriendShipRepository;
import com.example.capsulee_backend.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final FriendShipRepository friendShipRepository;
    private final ReceptionRepository receptionRepository;

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

    @Transactional
    public User getUserByLoginID(String loginID) {
        User user = userRepository.findByLoginID(loginID)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 ID입니다."));
        return user;
    }

    @Transactional
    public User getUserById(Long Id) {
        User user = userRepository.findById(Id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 ID입니다."));
        return user;
    }

    @Transactional
    public UserStatResponseDto getUserStat(User user) {
        // 해당 사용자가 수신받은 캡슐 수신 정보들
        List<Reception> receptions = receptionRepository.findByRecipient(user);

        // 캡슐 정보
        int totals = receptions.size(); // 총 수신된 캡슐 개수
        int opened = 0; // 열린 캡슐 개수
        for (Reception reception : receptions) {
            if (reception.getCapsule().isOpened()) opened++;
        }

        // 친구 수
        int friends = 0;
        friends += friendShipRepository.countByReceiver(user);
        friends += friendShipRepository.countBySender(user);

        return new UserStatResponseDto(totals, opened, friends);
    }

    @Transactional
    public UserInfoResponseDto getUserInfo(User user) {
        // 해당 사용자의 정보
        UserStatResponseDto stat = getUserStat(user);

        return new UserInfoResponseDto(
                user.getId(), user.getLoginID(), user.getUsername(), user.isOkAlarm(), stat
        );
    }

    @Transactional
    public UserUpdateResponseDto updateUserInfo(User user, UserUpdateRequestDto requestDto) {
        user.update(requestDto.getLoginID(), requestDto.getUsername());

        return new UserUpdateResponseDto(user.getLoginID(), user.getUsername());
    }
}
