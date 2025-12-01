package com.example.capsulee_backend.capsule.service;

import com.example.capsulee_backend.capsule.domain.Capsule;
import com.example.capsulee_backend.capsule.domain.Reception;
import com.example.capsulee_backend.capsule.dto.request.ReadyStatusRequestDto;
import com.example.capsulee_backend.capsule.dto.response.ReadyStatusResponseDto;
import com.example.capsulee_backend.capsule.repository.ReceptionRepository;
import com.example.capsulee_backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UnlockService {
    private final ReceptionRepository receptionRepository;

    @Transactional
    public ReadyStatusResponseDto changeReadyStatus(Capsule capsule, User recipient, ReadyStatusRequestDto readyStatusRequestDto) {
        // 캡슐을 수신한 정보를 가져옴
        Reception reception = receptionRepository.findReceptionByCapsuleAndRecipient(capsule, recipient);

        // ready 버튼을 눌렀는지 여부에 따라 업데이트
        reception.update(readyStatusRequestDto.isReady());

        return new ReadyStatusResponseDto(reception.isReady());
    }
}
