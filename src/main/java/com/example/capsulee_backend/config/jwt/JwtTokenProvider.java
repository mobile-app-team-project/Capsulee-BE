package com.example.capsulee_backend.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtTokenProvider {
    // ACCESS TOKEN -> 1시간
    private static final Long ACCESS_TOKEN_EXPIRATION_TIME = 1000L * 60 * 60;
    // REFRESH TOKEN -> 7일
    private static final Long REFRESH_TOKEN_EXPIRATION_TIME = 1000L * 60 * 60 * 24 * 7;

    @Value("${jwt.secret}")
    private String jwtSecret;

    private SecretKey secretKey;

    @PostConstruct
    private void init() {
        // JWT 서명용 SecretKey 생성
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
}
