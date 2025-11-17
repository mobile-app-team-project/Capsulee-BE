package com.example.capsulee_backend.user.service;

import com.example.capsulee_backend.user.domain.FriendRequest;
import com.example.capsulee_backend.user.domain.FriendShip;
import com.example.capsulee_backend.user.domain.User;
import com.example.capsulee_backend.user.dto.request.FriendShipUpdateRequestDto;
import com.example.capsulee_backend.user.dto.response.FriendInfoResponseDto;
import com.example.capsulee_backend.user.dto.response.FriendShipResponseDto;
import com.example.capsulee_backend.user.repository.FriendShipRepository;
import com.example.capsulee_backend.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class FriendShipService {
    private final UserRepository userRepository;
    private final FriendShipRepository friendShipRepository;

    @Transactional
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

    @Transactional
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
                friendShip.getId(), friendShip.getStatus(), sender.getLoginID(), receiver.getLoginID());
        return responseDto;
    }

    @Transactional
    public List<FriendInfoResponseDto> getPendingList(String userLoginID) {
        User receiver = userRepository.findByLoginID(userLoginID)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 해당 유저가 받은 친구 요청 리스트
        List<FriendShip> friendShipList = friendShipRepository.findAllByReceiver(receiver);

        List<FriendInfoResponseDto> responseDtoList = new ArrayList<>();
        for (FriendShip friendShip : friendShipList) {
            if (friendShip.getStatus().equals(FriendRequest.PENDING)) {
                // 친구 요청한 경우만 추가
                User sender = friendShip.getSender();
                FriendInfoResponseDto friendPendingResponseDto = new FriendInfoResponseDto(
                        friendShip.getId(), sender.getId(), sender.getLoginID(), sender.getUsername()
                );
                responseDtoList.add(friendPendingResponseDto);
            }
        }
        return responseDtoList;
    }

    @Transactional
    public List<FriendInfoResponseDto> getFriendList(String userLoginID) {
        User user = userRepository.findByLoginID(userLoginID)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 친구 상태인 유저 리스트
        List<FriendInfoResponseDto> responseDtoList = new ArrayList<>();

        // 해당 유저가 친구 신청을 한 경우
        List<FriendShip> sendFriendShip = friendShipRepository.findAllBySender(user);
        for (FriendShip friendShip : sendFriendShip) {
            if (friendShip.getStatus().equals(FriendRequest.ACCEPTED)) {
                // 친구 요청을 accept한 경우에만 친구 상태
                User friend = friendShip.getReceiver();
                FriendInfoResponseDto friendInfoResponseDto = new FriendInfoResponseDto(
                        friendShip.getId(), friend.getId(), friend.getLoginID(), friend.getUsername()
                );
                responseDtoList.add(friendInfoResponseDto);
            }
        }

        // 해당 유저가 친구 신청을 받은 경우
        List<FriendShip> receiveFriendShip = friendShipRepository.findAllByReceiver(user);
        for (FriendShip friendShip : receiveFriendShip) {
            if (friendShip.getStatus().equals(FriendRequest.ACCEPTED)) {
                // 친구 요청을 accept한 경우에만 친구 상태
                User friend = friendShip.getSender();
                FriendInfoResponseDto friendInfoResponseDto = new FriendInfoResponseDto(
                        friendShip.getId(), friend.getId(), friend.getLoginID(), friend.getUsername()
                );
                responseDtoList.add(friendInfoResponseDto);
            }
        }

        return responseDtoList;
    }
}
