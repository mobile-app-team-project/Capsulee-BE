package com.example.capsulee_backend.capsule.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LocationConditionResponseDto {
    private LocationResult locationCondition;
    private WeatherResult weatherCondition;
    private boolean isReadyAvailable;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LocationResult {
        private boolean matched;
        private double distance; // meter
        private double userLat;  // 사용자 현재 latitude
        private double userLon;  // 사용자 현재 longitude
        private String conditionValue;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WeatherResult {
        private boolean matched;
        private String requiredWeather;
    }
}
