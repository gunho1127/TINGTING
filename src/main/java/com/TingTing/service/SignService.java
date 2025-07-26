package com.TingTing.service;

import com.TingTing.dto.SignInRequest;
import com.TingTing.dto.SignUpRequest;
import com.TingTing.dto.TokenResponse;
import com.TingTing.entity.User;
import com.TingTing.repository.UserRepository;
import com.TingTing.util.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SignService {

    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider jwtTokenProvider;

    private final long ACCESS_EXPIRE = 1000 * 60 * 60; // 1h
    private final long REFRESH_EXPIRE = 1000 * 60 * 60 * 24 * 7; //7d

    public void signUp(SignUpRequest dto) {
        if (userRepository.existsByUsEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (userRepository.existsByUsNickname(dto.getNickname())) {
            throw new RuntimeException("Nickname already exists");
        }

        User user = User.builder()
                .usEmail(dto.getEmail())
                .usPw(dto.getPassword())
                .usNickname(dto.getNickname())
                .build();
        userRepository.save(user);
    }

    public TokenResponse logIn(SignInRequest dto, HttpServletResponse response) {
        User user = userRepository.findByUsEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!user.getUsPw().equals(dto.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user.getUsEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsEmail());

        refreshTokenService.saveOrUpdate(user.getUsEmail(), refreshToken, REFRESH_EXPIRE);

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

        return new TokenResponse(user.getUsNickname());
    }

    public void logOut(User user, HttpServletResponse response) {
        refreshTokenService.delete(user.getUsEmail());

        Cookie ac = new Cookie("accessToken", null);
        ac.setPath("/");
        ac.setMaxAge(0);
        response.addCookie(ac);

        Cookie rc = new Cookie("refreshToken", null);
        rc.setPath("/");
        rc.setMaxAge(0);
        response.addCookie(rc);
    }
}
