package com.example.capsulee_backend.capsule.service;

import com.example.capsulee_backend.capsule.domain.Capsule;
import com.example.capsulee_backend.capsule.domain.ConditionType;
import com.example.capsulee_backend.capsule.domain.Conditions;
import com.example.capsulee_backend.capsule.domain.Reception;
import com.example.capsulee_backend.capsule.dto.request.CreateCapsuleRequestDto;
import com.example.capsulee_backend.capsule.dto.response.*;
import com.example.capsulee_backend.capsule.repository.CapsuleRepository;
import com.example.capsulee_backend.capsule.repository.ConditionRepository;
import com.example.capsulee_backend.capsule.repository.ReceptionRepository;
import com.example.capsulee_backend.user.domain.User;
import com.example.capsulee_backend.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CapsuleService {

    private final UserRepository userRepository;
    private final CapsuleRepository capsuleRepository;
    private final ReceptionRepository receptionRepository;
    private final ConditionRepository conditionRepository;

    @Transactional
    public CreateCapsuleResponseDto createCapsule(CreateCapsuleRequestDto request, String loginID) {
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
        List<CreateCapsuleResponseDto.ConditionRequest> savedConditionsDto = new ArrayList<>();
        if (request.getConditions() != null) {
            for (CreateCapsuleRequestDto.ConditionRequest conditionRequest : request.getConditions()) {
                ConditionType conditionType = ConditionType.valueOf(conditionRequest.getType());

                Conditions condition = Conditions.builder()
                        .capsule(savedCapsule)
                        .type(conditionType)
                        .value(conditionRequest.getValue())
                        .build();

                conditionRepository.save(condition);

                savedConditionsDto.add(new CreateCapsuleResponseDto.ConditionRequest(
                        condition.getType().name(),
                        condition.getValue()
                ));
            }
        }

        return new CreateCapsuleResponseDto(
                savedCapsule.getTitle(),
                savedCapsule.getContent(),
                savedCapsule.getImageURL(),
                savedCapsule.getOpenTime(),
                request.getRecipientIds(),
                savedConditionsDto
        );
    }

    @Transactional
    public CapsuleListResponseDto getCapsules(String loginID, String type) {
        User currentUser = userRepository.findByLoginID(loginID)
                .orElseThrow(() -> new EntityNotFoundException("[ERROR] 유저를 찾을 수 없습니다."));

        // 1. 받은 캡슐, 보낸 캡슐 모두 조회 (openTime 기준 최신순 정렬)
        // 수신자가 currentUser인 경우 -> 받은 캡슐
        List<Reception> receptions = receptionRepository.findByRecipient(currentUser);
        List<Capsule> receivedCapsules = receptions.stream()
                                        .map(Reception::getCapsule)
                                        .sorted(Comparator.comparing(Capsule::getOpenTime).reversed())
                                        .collect(Collectors.toList());
        // currentUser가 보낸 캡슐
        List<Capsule> sentCapsules = capsuleRepository.findByCreatorOrderByOpenTimeDesc(currentUser);

        // 2. Stats 계산
        CapsuleStatsDto stats = getCapsuleStats(receivedCapsules, sentCapsules);

        // 3. 캡슐 리스트 (type에 따라 분기)
        List<Capsule> listCapsules;

        if ("sent".equalsIgnoreCase(type)) {
            listCapsules = sentCapsules;
        } else if ("received".equalsIgnoreCase(type)) {
            listCapsules = receivedCapsules;
        } else {
            throw new IllegalArgumentException("[ERROR] type 파라미터는 'sent' 또는 'received'여야 합니다.");
        }

        // 4. DTO 변환
        List<CapsuleSummaryDto> capsuleSummaries = new ArrayList<>();

        for (Capsule capsule : listCapsules) {
            String fromOrTo = calculateFromOrTo(capsule, type);
            List<ConditionSummaryDto> conditions = calculateConditionSummary(capsule);

            CapsuleSummaryDto summaryDto = new CapsuleSummaryDto(
                    capsule.getId(),
                    capsule.getTitle(),
                    fromOrTo,
                    capsule.isOpened(),
                    conditions
            );
            capsuleSummaries.add(summaryDto);
        }

        return new CapsuleListResponseDto(stats, capsuleSummaries);
    }

    private CapsuleStatsDto getCapsuleStats(List<Capsule> receivedCapsules, List<Capsule> sentCapsules) {
        // 중복 제거 (creator == recipient일 경우 대비)
        Set<Capsule> allCapsules = new HashSet<>();
        allCapsules.addAll(receivedCapsules);
        allCapsules.addAll(sentCapsules);

        int total = allCapsules.size();

        int canOpen = (int) allCapsules.stream()
                .filter(c -> c.isOpened() && c.getOpenTime().isBefore(LocalDateTime.now()))
                .count();

        int locked = total - canOpen;

        return new CapsuleStatsDto(total, canOpen, locked);
    }

    private String calculateFromOrTo(Capsule capsule, String type) {
        if ("sent".equalsIgnoreCase(type)) {
            String recipients = capsule.getReceptions().stream()
                    .map(r -> r.getRecipient().getUsername())
                    .collect(Collectors.joining(", "));
            return "To " + recipients;
        } else {
            return "From " + capsule.getCreator().getUsername();
        }
    }

    private List<ConditionSummaryDto> calculateConditionSummary(Capsule capsule) {
        List<ConditionSummaryDto> conditions = new ArrayList<>();

        // 수신자
        String recipients = capsule.getReceptions().stream()
                .map(r -> r.getRecipient().getUsername())
                .collect(Collectors.joining(", "));
        conditions.add(new ConditionSummaryDto("RECIPIENTS", recipients));

        // 나머지 조건들 (LOCATION, WEATHER, ACTION)
        for (Conditions condition : capsule.getCondition()) {
            conditions.add(new ConditionSummaryDto(condition.getType().name(), condition.getValue()));
        }

        return conditions;
    }
}
