package com.example.capsulee_backend.weather.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class OpenWeatherResponse {

    private List<Weather> weather;

    @Data
    public static class Weather {
        private String main;        // Clear, Clouds, Rain, Snow ...
        private String description; // scattered clouds, light rain 등
    }
}