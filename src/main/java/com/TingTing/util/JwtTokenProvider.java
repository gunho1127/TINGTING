package com.TingTing.util;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    private final long ACCESS_TOKEN_EXPIRES_IN = 1000 * 60 * 60;         // 1시간
    private final long REFRESH_TOKEN_EXPIRES_IN = 1000 * 60 * 60 * 24 * 7; // 7일

    // ACCESS 토큰 생성
    public String generateAccessToken(String email) {
        return generateToken(email, ACCESS_TOKEN_EXPIRES_IN);
    }

    // REFRESH 토큰 생성
    public String generateRefreshToken(String email) {
        return generateToken(email, REFRESH_TOKEN_EXPIRES_IN);
    }

    // 내부 공통 토큰 생성 로직
    private String generateToken(String email, long expiresIn) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiresIn);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // 이메일 (subject) 추출
    public String getEmailFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            // 만료는 true로 반환하지 않음
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Claims 파싱
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}
