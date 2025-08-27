package com.TingTing.filter;

import com.TingTing.entity.User;
import com.TingTing.repository.UserRepository;
import com.TingTing.service.RefreshTokenService;
import com.TingTing.util.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    // ★ 공개 경로/OPTIONS은 아예 필터를 타지 않게 스킵
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;

        return uri.startsWith("/api/auth/")     // 회원가입/로그인/이메일 인증
                || uri.startsWith("/oauth2/")
                || uri.startsWith("/login/oauth2/")
                || uri.startsWith("/ws");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String accessToken = getTokenFromCookie(request, "accessToken");
        String refreshToken = getTokenFromCookie(request, "refreshToken");

        try {
            if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
                setAuthentication(accessToken, request);
            } else if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
                String email = jwtTokenProvider.getEmailFromToken(refreshToken);
                if (refreshTokenService.isValid(email, refreshToken)) {
                    String newAccessToken = jwtTokenProvider.generateAccessToken(email);
                    Cookie accessTokenCookie = new Cookie("accessToken", newAccessToken);
                    accessTokenCookie.setHttpOnly(true);
                    accessTokenCookie.setPath("/");
                    accessTokenCookie.setMaxAge(60 * 60);
                    response.addCookie(accessTokenCookie);

                    setAuthentication(newAccessToken, request);
                }
            }
        } catch (Exception ignore) {
            // 토큰 문제는 여기서 '무시'하고 통과시킴.
            // 보호된 API에서만 최종적으로 401/403이 떨어지게.
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String token, HttpServletRequest request) {
        String email = jwtTokenProvider.getEmailFromToken(token);
        User user = userRepository.findByUsEmail(email).orElse(null);

        if (user != null) {
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    private String getTokenFromCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(cookie -> name.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
