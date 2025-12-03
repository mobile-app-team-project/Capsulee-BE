package com.example.capsulee_backend.weather.service;

import com.example.capsulee_backend.config.OpenWeatherConfig;
import com.example.capsulee_backend.weather.dto.response.OpenWeatherResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class OpenWeatherService {

    private final WebClient.Builder webClientBuilder;
    private final OpenWeatherConfig config; // API KEY, BASE_URL 저장

    /**
     * 지정된 위경도(lat, lon)의 현재 날씨를 조회하고
     * targetCondition(CLEAR, CLOUD, RAINY, SNOW)과 비교하여 충족 여부 반환
     */
    public boolean checkWeatherCondition(double lat, double lon, String targetCondition) {

        String url = String.format(
                "%s/weather?lat=%f&lon=%f&appid=%s&units=metric",
                config.getBaseUrl(),
                lat,
                lon,
                config.getKey()
        );

        // OpenWeather API 호출
        OpenWeatherResponse response = webClientBuilder.build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(OpenWeatherResponse.class)
                .block();

        if (response == null || response.getWeather() == null || response.getWeather().isEmpty()) {
            throw new RuntimeException("[ERROR] OpenWeather API 응답 없음 또는 파싱 실패");
        }

        // 현재 날씨 코드(main 값: Clear, Clouds, Rain, Snow ...)
        String currentWeather = response.getWeather().get(0).getMain().toUpperCase();
        String description = response.getWeather().get(0).getDescription();

        System.out.println("🌤 [현재 날씨] main=" + currentWeather + ", desc=" + description);

        // 조건 비교
        switch (targetCondition.toUpperCase()) {

            case "CLEAR":
                return currentWeather.equals("CLEAR");

            case "CLOUD":
                // Clouds / Overcast 포함
                return currentWeather.equals("CLOUDS") || currentWeather.equals("OVERCAST");

            case "RAINY":
                // Drizzle, Thunderstorm 포함
                return currentWeather.equals("RAIN")
                        || currentWeather.equals("DRIZZLE")
                        || currentWeather.equals("THUNDERSTORM");

            case "SNOW":
                return currentWeather.equals("SNOW");

            default:
                System.out.println("⚠️ 지원하지 않는 weather 조건: " + targetCondition);
                return false;
        }
    }
}