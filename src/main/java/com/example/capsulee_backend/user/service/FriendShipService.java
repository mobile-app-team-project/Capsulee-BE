package com.example.capsulee_backend.user.service;

import com.example.capsulee_backend.user.domain.FriendRequest;
import com.example.capsulee_backend.user.domain.FriendShip;
import com.example.capsulee_backend.user.domain.User;
import com.example.capsulee_backend.user.dto.request.FriendShipRequestDto;
import com.example.capsulee_backend.user.dto.request.FriendShipUpdateRequestDto;
import com.example.capsulee_backend.user.dto.response.FriendShipResponseDto;
import com.example.capsulee_backend.user.repository.FriendShipRepository;
import com.example.capsulee_backend.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FriendShipService {
    private final UserRepository userRepository;
    private final FriendShipRepository friendShipRepository;

    public FriendShipResponseDto createFriendShip(String senderLoginID, String receiverLoginID) {
        if (senderLoginID.equals(receiverLoginID)) {
            throw new IllegalArgumentException("본인에게 친구 요청을 할 수 없습니다.");
        }

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

        return new FriendShipResponseDto(
                saved.getId(),
                saved.getStatus(),
                saved.getSender().getLoginID(),
                saved.getReceiver().getLoginID());
    }

    public FriendShipResponseDto updateFriendShip(FriendShipUpdateRequestDto requestDto) {
        User sender = userRepository.findByLoginID(requestDto.getSenderLoginId())
                .orElseThrow(() -> new IllegalArgumentException("발신자가 존재하지 않는 유저입니다."));

        User receiver = userRepository.findByLoginID(requestDto.getReceiverLoginId())
                .orElseThrow(() -> new IllegalArgumentException("수신자가 존재하지 않는 유저입니다."));

        FriendShip friendShip = friendShipRepository.findBySenderAndReceiver(sender, receiver);

        // 상태 변경
        FriendRequest status = requestDto.getStatus();
        friendShip.update(status);

        FriendShipResponseDto responseDto = new FriendShipResponseDto(
                friendShip.getId(), status, sender.getLoginID(), receiver.getLoginID());
        return responseDto;
    }
}
