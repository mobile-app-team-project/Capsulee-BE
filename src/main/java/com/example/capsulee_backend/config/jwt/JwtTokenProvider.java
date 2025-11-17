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

    // 토큰 유효성 확인
    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token); // 유효성 검사를 위해 파싱 시도
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // token으로 claims 받아옴
    public Claims getClaimsFromToken(String token) {
        // JWT 파서 생성
        // parserBuilder()로 JWT 파서를 만들기 위한 빌더 객체를 얻는다.
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey) // JWT 서명 검증을 위해 SecretKey 설정
                .build() // 빌더를 통해 실제 파서(parser) 객체 생성
                .parseClaimsJws(token) // 토큰을 파싱해서 Claims(JWT payload)만 추출
                .getBody(); // 파싱된 토큰에서 body(클레임 값들)을 가져온다
        return claims;
    }

    public String getLoginIDFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("loginID", String.class);
    }
}
