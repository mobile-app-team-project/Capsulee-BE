package com.example.capsulee_backend.weather.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WeatherTimeUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("HH");
    private static final int API_UPDATE_MINUTE = 40;  // 공식 기준: 매시간 40분에 갱신됨

    /**
     * 기준 시간(base_time)을 계산 (HH00)
     * 예) 15:32 → 1400, 15:45 → 1500
     */
    public static String getBaseTime() {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime baseDateTime =
                (now.getMinute() < API_UPDATE_MINUTE)
                        ? now.minusHours(1)
                        : now;

        return baseDateTime.format(HOUR_FORMATTER) + "00";
    }

    /**
     * 기준 날짜(base_date)를 계산 (YYYYMMDD)
     * baseTime이 전날 기준(예: 2300)으로 바뀌는 경우 처리
     */
    public static String getBaseDate(String baseTime) {
        LocalDateTime now = LocalDateTime.now();

        // baseTime을 숫자로 변환 (예: "2300" → 23)
        int baseHour = Integer.parseInt(baseTime.substring(0, 2));

        // 현재 시간이 00시이고 baseTime이 23시이면 날짜는 전날
        if (now.getHour() == 0 && baseHour == 23) {
            return now.minusDays(1).format(DATE_FORMATTER);
        }

        return now.format(DATE_FORMATTER);
    }

    /**
     * 이전 기준 시간 계산
     * 예) 1500 → 1400, 0000 → 전날 2300
     */
    public static String getPreviousBaseTime(String currentBaseTime, int hoursBack) {
        int hour = Integer.parseInt(currentBaseTime.substring(0, 2));
        int prevHour = hour - hoursBack;
        if (prevHour < 0) prevHour += 24;
        return String.format("%02d00", prevHour);
    }

    /**
     * fallback에서 날짜 조정
     * 예) baseTime=0000 → previous=2300인 경우 날짜는 -1
     */
    public static String adjustDateIfNeeded(String baseDate, String currentTime, String fallbackTime) {
        int curHour = Integer.parseInt(currentTime.substring(0, 2));
        int fbHour = Integer.parseInt(fallbackTime.substring(0, 2));

        // fallbackTime(23시) > currentTime(00시) → 날짜 -1
        if (fbHour > curHour) {
            LocalDate date = LocalDate.parse(baseDate, DATE_FORMATTER);
            return date.minusDays(1).format(DATE_FORMATTER);
        }

        return baseDate;
    }
}