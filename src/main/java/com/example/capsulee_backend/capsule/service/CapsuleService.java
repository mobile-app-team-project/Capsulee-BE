package com.example.capsulee_backend.capsule.service;

import com.example.capsulee_backend.aws.s3.S3Uploader;
import com.example.capsulee_backend.capsule.domain.*;
import com.example.capsulee_backend.capsule.dto.request.CreateCapsuleRequestDto;
import com.example.capsulee_backend.capsule.dto.response.*;
import com.example.capsulee_backend.capsule.repository.CapsuleRepository;
import com.example.capsulee_backend.capsule.repository.ConditionRepository;
import com.example.capsulee_backend.capsule.repository.ReceptionRepository;
import com.example.capsulee_backend.capsule.repository.RecipientConditionsRepository;
import com.example.capsulee_backend.user.domain.User;
import com.example.capsulee_backend.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CapsuleService {

    private final UserRepository userRepository;
    private final CapsuleRepository capsuleRepository;
    private final ReceptionRepository receptionRepository;
    private final ConditionRepository conditionRepository;
    private final RecipientConditionsRepository recipientConditionsRepository;

    private static final DateTimeFormatter CAPSULE_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd 'at' HH:mm");
    private final S3Uploader s3Uploader;

    @Transactional
    public CreateCapsuleResponseDto createCapsule(CreateCapsuleRequestDto request, String loginID, MultipartFile imageFile) {
        /*
        S3 연동 후 이미지 업로드
        String s3Url = s3Uploader.uplodImageFromUrl(request.getImageUrl(), "capsule-" + UUID.randomUUID());
         */

        User user = userRepository.findByLoginID(loginID)
                .orElseThrow(() -> new EntityNotFoundException("[ERROR] 유저를 찾을 수 없습니다."));

        // s3 이미지 업로드
        String s3Url = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            s3Url = s3Uploader.upload(imageFile, "capsules");
        }


        // 캡슐 저장
        Capsule capsule = Capsule.builder()
                .creator(user)
                .title(request.getTitle())
                .content(request.getContent())
                .imageURL(s3Url)
                .openTime(request.getOpenTime())
                .createdAt(LocalDateTime.now())
                .isOpened(false)
                .receptions(new ArrayList<>())
                .condition(new ArrayList<>())
                .build();

        Capsule savedCapsule = capsuleRepository.save(capsule);

        // 수신자 미리 조회
        Map<Long, User> recipientMap = request.getRecipientIds().stream()
                .map(id -> userRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("[ERROR] 수신 유저를 찾을 수 없습니다. ID: " + id)))
                .collect(Collectors.toMap(User::getId, Function.identity()));

        // Reception 생성
        List<Reception> receptionList = new ArrayList<>();
        for (Long recipientId : request.getRecipientIds()) {
            Reception reception = new Reception(savedCapsule, recipientMap.get(recipientId));
            receptionList.add(reception);
        }
        receptionRepository.saveAll(receptionList);

        // Condition & RecipientConditions 생성
        List<CreateCapsuleResponseDto.ConditionRequest> savedConditionsDto = new ArrayList<>();
        List<RecipientConditions> recipientConditionsList = new ArrayList<>();

        if (request.getConditions() != null) {
            for (CreateCapsuleRequestDto.ConditionRequest conditionRequest : request.getConditions()) {
                ConditionType conditionType = ConditionType.valueOf(conditionRequest.getType());

                Conditions condition = Conditions.builder()
                        .capsule(savedCapsule)
                        .type(conditionType)
                        .value(conditionRequest.getValue())
                        .build();

                Conditions savedCondition = conditionRepository.save(condition);

                // response용 dto 저장
                savedConditionsDto.add(new CreateCapsuleResponseDto.ConditionRequest(
                        condition.getType().name(),
                        condition.getValue()
                ));

                // RecipientConditions 생성
                for (Long recipientId : request.getRecipientIds()) {
                    RecipientConditions rc = RecipientConditions.builder()
                            .recipient(recipientMap.get(recipientId))
                            .condition(savedCondition)
                            .isAccepted(false)
                            .build();
                    recipientConditionsList.add(rc);
                }
            }
        }

        if (!recipientConditionsList.isEmpty()) {
            recipientConditionsRepository.saveAll(recipientConditionsList);
        }

        return new CreateCapsuleResponseDto(
                savedCapsule.getId(),
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
//        String recipients = capsule.getReceptions().stream()
//                .map(r -> r.getRecipient().getUsername())
//                .collect(Collectors.joining(", "));
//        conditions.add(new ConditionSummaryDto("RECIPIENTS", recipients));

        // 나머지 조건들 (LOCATION, WEATHER, ACTION)
        for (Conditions condition : capsule.getCondition()) {
            conditions.add(new ConditionSummaryDto(condition.getType().name(), condition.getValue()));
        }

        return conditions;
    }

    @Transactional
    public CapsuleDetailResponseDto getCapsule(String loginID, Long capsuleId) {

        User user = userRepository.findByLoginID(loginID)
                .orElseThrow(() -> new EntityNotFoundException("[ERROR] 유저를 찾을 수 없습니다."));
        Capsule capsule = capsuleRepository.findById(capsuleId)
                .orElseThrow(() -> new EntityNotFoundException("[ERROR] 캡슐을 찾을 수 없습니다."));

        boolean isCreator = capsule.getCreator().equals(user);
        boolean isRecipient = capsule.getReceptions().stream()
                .anyMatch(r -> r.getRecipient().equals(user));

        if (!isCreator && !isRecipient) {
            throw new AccessDeniedException("[ERROR] 열람 권한이 없습니다.");
        }

        LocalDate openDate = capsule.getOpenTime().toLocalDate();
        LocalDate today = LocalDate.now();

        boolean isBeforeOpen = openDate.isAfter(today);
        boolean isTodayOpen = openDate.isEqual(today);
        boolean isAfterOpen = openDate.isBefore(today);
        boolean isUserReady = isUserReady(user, capsule);
        boolean isOpened = capsule.isOpened();

        // 1. openTime 이전 -> LOCKED
        if (isBeforeOpen) {
            CapsuleDetailResponseDto.LockedCapsuleDetailDto lockedDetail =
                    new CapsuleDetailResponseDto.LockedCapsuleDetailDto(
                            new CapsuleInfoDto.LockedCapsuleDto(
                                    capsule.getId(),
                                    capsule.getTitle(),
                                    capsule.getCreator().getUsername(),
                                    capsule.getOpenTime().format(CAPSULE_DATE_FORMATTER),
                                    calculateProgressPercent(capsule.getCreatedAt(), capsule.getOpenTime())
                            ),
                            getParticipant(capsule),
                            calculateConditionSummary(capsule)
                    );
            return new CapsuleDetailResponseDto(
                    "LOCKED",
                    lockedDetail
            );
        }

        // 2. 오픈 당일이거나 오픈날이 지났는데 사용자가 Ready 버튼을 아직 누르지 않았을 때 -> WAITING
        if ((isTodayOpen || isAfterOpen) && !isUserReady && !isOpened) {
            CapsuleDetailResponseDto.WaitingCapsuleDetailDto waitingDetail =
                new CapsuleDetailResponseDto.WaitingCapsuleDetailDto(
                        new CapsuleInfoDto.WaitingCapsuleDto(
                                capsule.getId(),
                                capsule.getTitle(),
                                capsule.getCreator().getUsername(),
                                capsule.getOpenTime().format(CAPSULE_DATE_FORMATTER)
                        ),
                        getParticipant(capsule),
                        calculateConditionSummary(capsule)
                );
            return new CapsuleDetailResponseDto(
                    "WAITING",
                    waitingDetail
            );
        }

        // 3. Ready인 경우 -> READY
        if (isUserReady && !isOpened) {
            CapsuleDetailResponseDto.ReadyProgressDto progress = calculateReadyProgress(capsule);
            CapsuleDetailResponseDto.ReadyCapsuleDetailDto readyDetail =
                    new CapsuleDetailResponseDto.ReadyCapsuleDetailDto(
                            new CapsuleInfoDto.ReadyCapsuleDto(
                                    capsule.getId(),
                                    capsule.getTitle(),
                                    capsule.getCreator().getUsername(),
                                    capsule.getOpenTime().format(CAPSULE_DATE_FORMATTER)
                            ),
                            progress,
                            getParticipant(capsule)
                    );
            return new CapsuleDetailResponseDto(
                    "READY",
                    readyDetail
            );
        }

        // 4. 캡슐이 열렸을 경우 -> OPENED
        CapsuleDetailResponseDto.ReadyProgressDto progress = calculateReadyProgress(capsule);
        CapsuleDetailResponseDto.OpenedCapsuleDetailDto readyDetail =
                new CapsuleDetailResponseDto.OpenedCapsuleDetailDto(
                        new CapsuleInfoDto.OpenedCapsuleDto(
                                capsule.getId(),
                                capsule.getTitle(),
                                capsule.getCreator().getUsername(),
                                capsule.getOpenTime().format(CAPSULE_DATE_FORMATTER),
                                capsule.getContent()
                        ),
                        getParticipant(capsule),
                        progress,
                        calculateConditionSummary(capsule)
                );
        return new CapsuleDetailResponseDto(
                "OPENED",
                readyDetail
        );
    }

    public Long getLatestCapsuleId(String loginID) {
        User user = userRepository.findByLoginID(loginID)
                .orElseThrow(() -> new EntityNotFoundException("[ERROR] 유저를 찾을 수 없습니다."));

        // 해당 유저가 가진 캡슐 중, open 날짜가 가장 가까운 캡슐을 반환
        LocalDateTime now = LocalDateTime.now(); // 현재 시간보다는 뒤여야 함
        Capsule latestCapsule = capsuleRepository
                .findTopByCreatorAndOpenTimeAfterOrderByOpenTimeAsc(user, now)
                .orElse(null);
        if (latestCapsule == null) {
            // 아직 캡슐이 하나도 없는 경우
            return null;
        }
        return latestCapsule.getId(); // capsule의 id 반환
    }

    private int calculateProgressPercent(LocalDateTime createdAt, LocalDateTime openTime) {
        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(openTime)) return 100;

        Duration total = Duration.between(createdAt, openTime);
        Duration passed = Duration.between(createdAt, now);

        double ratio = (double) passed.toMillis() / total.toMillis();
        return (int) (100 * ratio);
    }

    private List<ParticipantDto> getParticipant(Capsule capsule) {
        return capsule.getReceptions().stream()
                .map(reception -> {
                    User recipient = reception.getRecipient();
                    String status = reception.isReady() ? "Ready" : "Waiting..";
                    return new ParticipantDto(
                            recipient.getId(),
                            recipient.getUsername(),
                            status
                    );
                })
                .collect(Collectors.toList());
    }

    private boolean isUserReady(User user, Capsule capsule) {
        for (Reception reception : capsule.getReceptions()) {
            if (reception.getRecipient().equals(user)) {
                return reception.isReady();
            }
        }
        return false;
    }

    @Transactional
    public Capsule getCapsuleById(Long Id) {
        Capsule capsule = capsuleRepository.findById(Id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 ID입니다."));
        return capsule;
    }

    private CapsuleDetailResponseDto.ReadyProgressDto calculateReadyProgress(Capsule capsule) {
        // 총 참여자 수 (수신자 기준)
        int totalRecipients = capsule.getReceptions().size();

        // Ready 상태인 참여자 수 카운트
        long readyCount = capsule.getReceptions().stream()
                .filter(Reception::isReady)
                .count();

        return new CapsuleDetailResponseDto.ReadyProgressDto((int) readyCount, totalRecipients);
    }
}
