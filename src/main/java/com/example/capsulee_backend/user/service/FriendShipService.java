package com.example.capsulee_backend.user.service;

import com.example.capsulee_backend.user.domain.FriendRequest;
import com.example.capsulee_backend.user.domain.FriendShip;
import com.example.capsulee_backend.user.domain.User;
import com.example.capsulee_backend.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FriendShipService {
    private final UserRepository userRepository;

    public void createFriendShip(String senderLoginID, String receiverLoginID) {
        User sender = userRepository.findByLoginID(senderLoginID)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));

        User receiver = userRepository.findByLoginID(senderLoginID)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));

        // 친구 요청을 생성
        FriendShip friendShip = FriendShip.builder()
                .sender(sender)
                .receiver(receiver)
                .status(FriendRequest.PENDING)
                .build();
    }
}
