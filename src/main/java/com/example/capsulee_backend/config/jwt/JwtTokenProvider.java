package com.example.capsulee_backend.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

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

    // Access Token 발급
    public String generateAccessToken(String loginID) {
        // 현재 시간
        final Date now = new Date();
        // 만료 시간
        Date expiry = new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION_TIME);

        // JWT의 정보
        final Claims claims = Jwts.claims()
                .setIssuedAt(now)
                .setExpiration(expiry); // 만료 시간
        claims.put("loginID", loginID); // loginID로 인증

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // 헤더 typ을 JWT로 설정
                .setSubject("ACCESS_TOKEN") // 토큰의 용도 표시
                .setClaims(claims) // 클레임 지정
                .signWith(secretKey, SignatureAlgorithm.HS256) // 서명
                .compact();
    }

    // Refresh Token 발급
    public String generateRefreshToken(String loginID) {
        // 현재 시간
        final Date now = new Date();
        // 만료 시간
        Date expiry = new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION_TIME);

        final Claims claims = Jwts.claims()
                .setIssuedAt(now)
                .setExpiration(expiry);
        claims.put("loginID", loginID);

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject("REFRESH_TOKEN")
                .setClaims(claims)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
