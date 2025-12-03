package com.example.capsulee_backend.capsule.service;

import com.example.capsulee_backend.capsule.domain.*;
import com.example.capsulee_backend.capsule.dto.request.ActionConditionRequestDto;
import com.example.capsulee_backend.capsule.dto.request.LocationConditionRequestDto;
import com.example.capsulee_backend.capsule.dto.response.ActionConditionResponseDto;
import com.example.capsulee_backend.capsule.dto.response.LocationConditionResponseDto;
import com.example.capsulee_backend.capsule.repository.CapsuleRepository;
import com.example.capsulee_backend.capsule.repository.ReceptionRepository;
import com.example.capsulee_backend.capsule.repository.RecipientConditionsRepository;
import com.example.capsulee_backend.user.domain.User;
import com.example.capsulee_backend.user.repository.UserRepository;
import com.example.capsulee_backend.weather.service.OpenWeatherService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConditionService {

    private final UserRepository userRepository;
    private final CapsuleRepository capsuleRepository;
    private final OpenWeatherService weatherService;
    private final RecipientConditionsRepository recipientConditionsRepository;
    private final ReceptionRepository receptionRepository;

    @Transactional
    public LocationConditionResponseDto verifyLocationConditions(
            LocationConditionRequestDto request, String loginID
    ) {
        User user = userRepository.findByLoginID(loginID)
                .orElseThrow(() -> new EntityNotFoundException("[ERROR] 유저를 찾을 수 없습니다."));

        Capsule capsule = capsuleRepository.findById(request.getCapsuleId())
                .orElseThrow(() -> new EntityNotFoundException("[ERROR] 캡슐을 찾을 수 없습니다."));

        // 캡슐 잠금 조건
        List<Conditions> conditions = capsule.getCondition();

        // 사용자의 위치
        double userLat = request.getLatitude();
        double userLon = request.getLongitude();

        LocationConditionResponseDto.LocationResult locationResult = null;
        LocationConditionResponseDto.WeatherResult weatherResult = null;

        // LOCATION 조건 확인
        Optional<Conditions> locationConditionOpt = conditions.stream()
                .filter(c -> c.getType() == ConditionType.LOCATION)
                .findFirst();

        // LOCATION 조건이 있다면
        if (locationConditionOpt.isPresent()) {
            Conditions locationCondition = locationConditionOpt.get();
            String targetValue = locationCondition.getValue();

            String[] parts = targetValue.split(",");
            double targetLat = Double.parseDouble(parts[0]);
            double targetLon = Double.parseDouble(parts[1]);
            String conditionValue = parts[2];

            double distance = calculateDistance(targetLat, targetLon, userLat, userLon);  // m 기준
            boolean isMatched = distance <= 100;

            locationResult = new LocationConditionResponseDto.LocationResult(
                    isMatched, distance, userLat, userLon, conditionValue
            );

            RecipientConditions rc = recipientConditionsRepository.findByRecipientAndCondition(user, locationCondition)
                    .orElseThrow(() -> new RuntimeException("[ERROR] 조건 매핑이 없습니다."));
            rc.updateStatus(isMatched);   // 조건 상태 저장
        }

        // WEATHER 조건 확인
        Optional<Conditions> weatherConditionOpt = conditions.stream()
                .filter(c -> c.getType() == ConditionType.WEATHER)
                .findFirst();

        // WEATHER 조건이 있다면
        if (weatherConditionOpt.isPresent()) {
            Conditions weatherCondition = weatherConditionOpt.get();
            String targetWeather = weatherCondition.getValue();

            boolean isMatched = weatherService.checkWeatherCondition(userLat, userLon, targetWeather);

            weatherResult = new LocationConditionResponseDto.WeatherResult(
                    isMatched, targetWeather
            );

            RecipientConditions rc = recipientConditionsRepository.findByRecipientAndCondition(user, weatherCondition)
                    .orElseThrow(() -> new RuntimeException("[ERROR] 조건 매핑이 없습니다."));
            rc.updateStatus(isMatched);  // 해당 조건 isAccepted = true로 변경
        }

        List<RecipientConditions> recipientConditionsList = conditions.stream()
                .map(condition -> recipientConditionsRepository.findByRecipientAndCondition(user, condition)
                        .orElse(null))  // null로 처리해서 필터링
                .filter(Objects::nonNull)
                .toList();

        if (recipientConditionsList.size() != conditions.size()) {
            throw new RuntimeException("[ERROR] 일부 조건에 대한 매핑 정보가 없습니다.");
        }

        for (RecipientConditions rc : recipientConditionsList) {
            System.out.printf("✔️ 조건 타입: %s | 만족 여부: %s\n", rc.getCondition().getType(), rc.isAccepted());
        }

        boolean isReadyAvailable = recipientConditionsList.stream()
                .allMatch(RecipientConditions::isAccepted);

        // 수신 정보 상태 변경 (Ready 버튼 활성화 / 비활성화)
        Reception reception = receptionRepository.findByCapsuleAndRecipient(capsule, user)
                .orElseThrow(() -> new RuntimeException("[ERROR] 수신자 정보를 찾을 수 없습니다."));
        reception.setReady(isReadyAvailable);

        return new LocationConditionResponseDto(locationResult, weatherResult, isReadyAvailable);
    }

    private double calculateDistance(double targetLat, double targetLon, double userLat, double userLon) {
        double R = 6371e3; // m
        double userRad1 = Math.toRadians(userLat);
        double userRad2 = Math.toRadians(userLon);
        double diffRad1 = Math.toRadians(targetLat - userLat);
        double diffRad2 = Math.toRadians(targetLon - userLon);

        double a = Math.sin(diffRad1 / 2) * Math.sin(diffRad1 / 2) +
                Math.cos(userRad1) * Math.cos(userRad2) *
                        Math.sin(diffRad2 / 2) * Math.sin(diffRad2 / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // return m
    }

}
