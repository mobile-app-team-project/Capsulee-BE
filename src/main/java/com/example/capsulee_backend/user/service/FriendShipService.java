package com.example.capsulee_backend.user.service;

import com.example.capsulee_backend.user.domain.FriendRequest;
import com.example.capsulee_backend.user.domain.FriendShip;
import com.example.capsulee_backend.user.domain.User;
import com.example.capsulee_backend.user.dto.response.FriendRequestResponseDto;
import com.example.capsulee_backend.user.repository.FriendShipRepository;
import com.example.capsulee_backend.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FriendShipService {
    private final UserRepository userRepository;
    private final FriendShipRepository friendShipRepository;

    public FriendRequestResponseDto createFriendShip(String senderLoginID, String receiverLoginID) {
        User sender = userRepository.findByLoginID(senderLoginID)
                .orElseThrow(() -> new IllegalArgumentException("발신자가 존재하지 않는 유저입니다."));

        User receiver = userRepository.findByLoginID(receiverLoginID)
                .orElseThrow(() -> new IllegalArgumentException("수신자가 존재하지 않는 유저입니다."));

        if (friendShipRepository.existsBySenderAndReceiver(sender, receiver)) {
            throw new IllegalArgumentException("이미 친구 요청을 보냈습니다.");
        }

        // 친구 요청을 생성
        FriendShip friendShip = FriendShip.builder()
                .sender(sender)
                .receiver(receiver)
                .status(FriendRequest.PENDING)
                .build();
        FriendShip saved = friendShipRepository.save(friendShip);

        return new FriendRequestResponseDto(
                saved.getId(),
                saved.getStatus(),
                saved.getSender().getLoginID(),
                saved.getReceiver().getLoginID());
    }
}
