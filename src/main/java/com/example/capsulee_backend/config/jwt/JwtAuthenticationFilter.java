package com.example.capsulee_backend.config.jwt;

import com.example.capsulee_backend.user.domain.User;
import com.example.capsulee_backend.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = getJwtFromRequest(request);
            // 토큰 존재 + 검증 성공 시 인증 처리
            if (token != null && jwtTokenProvider.validateToken(token)) {
                // token으로 loginID 찾기
                String loginID = jwtTokenProvider.getLoginIDFromToken(token);

                // loginID를 principal로 하여 user 인증 객체 생성
                UserAuthentication userAuthentication = UserAuthentication.createUserAuthentication(loginID);

                // SecurityContextHoler에 넣음
                SecurityContextHolder.getContext().setAuthentication(userAuthentication);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        // 요청을 필터로 보냄
        filterChain.doFilter(request, response);
    }

    // 요청에서 Jwt token만 반환
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring("Bearer ".length());
        }
        return null;
    }
}
