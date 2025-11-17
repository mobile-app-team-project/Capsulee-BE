package com.example.capsulee_backend.capsule.service;

import com.example.capsulee_backend.capsule.domain.Capsule;
import com.example.capsulee_backend.capsule.domain.ConditionType;
import com.example.capsulee_backend.capsule.domain.Conditions;
import com.example.capsulee_backend.capsule.domain.Reception;
import com.example.capsulee_backend.capsule.dto.request.CapsuleCreateRequest;
import com.example.capsulee_backend.capsule.dto.response.CapsuleCreateResponse;
import com.example.capsulee_backend.capsule.repository.CapsuleRepository;
import com.example.capsulee_backend.capsule.repository.ConditionRepository;
import com.example.capsulee_backend.capsule.repository.ReceptionRepository;
import com.example.capsulee_backend.user.domain.User;
import com.example.capsulee_backend.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CapsuleService {

    private final UserRepository userRepository;
    private final CapsuleRepository capsuleRepository;
    private final ReceptionRepository receptionRepository;
    private final ConditionRepository conditionRepository;

    @Transactional
    public CapsuleCreateResponse createCapsule(CapsuleCreateRequest request, String loginID) {
        /*
        S3 연동 후 이미지 업로드
        String s3Url = s3Uploader.uplodImageFromUrl(request.getImageUrl(), "capsule-" + UUID.randomUUID());
         */

        User user = userRepository.findByLoginID(loginID)
                .orElseThrow(() -> new EntityNotFoundException("[ERROR] 유저를 찾을 수 없습니다."));

        // 캡슐 저장
        Capsule capsule = Capsule.builder()
                .creator(user)
                .title(request.getTitle())
                .content(request.getContent())
                .imageURL(request.getImageUrl())  // 임시 URL -> 추후 S3 url로 변경
                .openTime(request.getOpenTime())
                .isOpened(false)
                .receptions(new ArrayList<>())
                .condition(new ArrayList<>())
                .build();

        Capsule savedCapsule = capsuleRepository.save(capsule);

        // 수신자 저장
        for (Long recipientId : request.getRecipientIds()) {
            User recipient = userRepository.findById(recipientId)
                    .orElseThrow(() -> new EntityNotFoundException("[ERROR] 수신 유저를 찾을 수 없습니다."));

            Reception reception = new Reception(capsule, recipient);
            receptionRepository.save(reception);
        }

        // 조건 저장
        List<CapsuleCreateResponse.ConditionRequest> savedConditionsDto = new ArrayList<>();
        if (request.getConditions() != null) {
            for (CapsuleCreateRequest.ConditionRequest conditionRequest : request.getConditions()) {
                ConditionType conditionType = ConditionType.valueOf(conditionRequest.getType());

                Conditions condition = Conditions.builder()
                        .capsule(savedCapsule)
                        .type(conditionType)
                        .value(conditionRequest.getValue())
                        .build();

                conditionRepository.save(condition);

                savedConditionsDto.add(new CapsuleCreateResponse.ConditionRequest(
                        condition.getType().name(),
                        condition.getValue()
                ));
            }
        }

        return new CapsuleCreateResponse(
                savedCapsule.getTitle(),
                savedCapsule.getContent(),
                savedCapsule.getImageURL(),
                savedCapsule.getOpenTime(),
                request.getRecipientIds(),
                savedConditionsDto
        );
    }
}
