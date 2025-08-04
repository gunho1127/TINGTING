package com.TingTing.service.oauth;

import com.TingTing.service.RefreshTokenService;
import com.TingTing.util.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    private final long ACCESS_EXPIRE = 1000 * 60 * 60; // 1 hour
    private final long REFRESH_EXPIRE = 1000 * 60 * 60 * 24 * 7; // 7 days

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        System.out.println("✅ [OAuth2LoginSuccessHandler] 소셜 로그인 성공!");

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        String accessToken = jwtTokenProvider.generateAccessToken(email);
        String refreshToken = jwtTokenProvider.generateRefreshToken(email);

        refreshTokenService.saveOrUpdate(email, refreshToken, REFRESH_EXPIRE);

        Cookie ac = new Cookie("accessToken", accessToken);
        ac.setHttpOnly(true);
        ac.setPath("/");
        ac.setMaxAge((int) (ACCESS_EXPIRE / 1000));
        response.addCookie(ac);

        Cookie rc = new Cookie("refreshToken", refreshToken);
        rc.setHttpOnly(true);
        rc.setPath("/");
        rc.setMaxAge((int) (REFRESH_EXPIRE / 1000));
        response.addCookie(rc);

        response.sendRedirect("/login-success");
        System.out.println("🎯 [OAuth2LoginSuccessHandler] accessToken: " + accessToken);
        System.out.println("🎯 [OAuth2LoginSuccessHandler] refreshToken: " + refreshToken);
    }

}
