package com.example.capsulee_backend.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class OpenWeatherConfig {

    @Value("${openweatherapi.api.key}")
    private String key;

    @Value("${openweatherapi.api.base-url}")
    private String baseUrl;
}