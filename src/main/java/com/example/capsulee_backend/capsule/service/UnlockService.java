package com.example.capsulee_backend.capsule.service;

import com.example.capsulee_backend.capsule.domain.Capsule;
import com.example.capsulee_backend.capsule.domain.Reception;
import com.example.capsulee_backend.capsule.dto.request.ReadyStatusRequestDto;
import com.example.capsulee_backend.capsule.dto.response.CapsuleDetailResponseDto;
import com.example.capsulee_backend.capsule.dto.response.CapsuleParticipantsResponseDto;
import com.example.capsulee_backend.capsule.dto.response.ParticipantStatusDto;
import com.example.capsulee_backend.capsule.dto.response.ReadyStatusResponseDto;
import com.example.capsulee_backend.capsule.repository.CapsuleRepository;
import com.example.capsulee_backend.capsule.repository.ReceptionRepository;
import com.example.capsulee_backend.user.domain.User;
import com.example.capsulee_backend.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UnlockService {
    private final ReceptionRepository receptionRepository;
    private final UserRepository userRepository;
    private final CapsuleRepository capsuleRepository;
    private final CapsuleService capsuleService;

    @Transactional
    public ReadyStatusResponseDto changeReadyStatus(Capsule capsule, User recipient, ReadyStatusRequestDto readyStatusRequestDto) {
        // 캡슐을 수신한 정보를 가져옴
        Reception reception = receptionRepository.findReceptionByCapsuleAndRecipient(capsule, recipient);

        // ready 버튼을 눌렀는지 여부에 따라 업데이트
        reception.update(readyStatusRequestDto.isReady());

        return new ReadyStatusResponseDto(reception.isReady());
    }

    @Transactional
    public CapsuleParticipantsResponseDto getCapsuleParticipants(Capsule capsule) {
        boolean opened = capsule.isOpened(); // 캡슐이 열렸는지
        List<ParticipantStatusDto> participantStatusDtos = new ArrayList<>(); // 수신자 정보를 담을 리스트

        // 캡슐을 수신한 모든 수신자 정보
        List<Reception> receptions = receptionRepository.findReceptionByCapsule(capsule);
        for (Reception reception : receptions) {
            User recipient = reception.getRecipient(); // 수신자
            String statusText = "Waiting..."; // 상태 텍스트
            if (reception.isReady()) {
                statusText = "Ready"; // 준비 상태
            }
            ParticipantStatusDto participantStatusDto = new ParticipantStatusDto(
                    recipient.getId(), recipient.getUsername(), reception.isReady(), statusText);
            participantStatusDtos.add(participantStatusDto);
        }

        return new CapsuleParticipantsResponseDto(opened, participantStatusDtos);
    }

    @Transactional
    public CapsuleDetailResponseDto openCapsule(String userLoginID, Long capsuleId) {
        User user = userRepository.findByLoginID(userLoginID)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Capsule capsule = capsuleRepository.findById(capsuleId)
                .orElseThrow(() -> new EntityNotFoundException("Capsule not found"));

        capsule.setOpened(true);

        return capsuleService.getCapsule(userLoginID, capsuleId);
    }
}
