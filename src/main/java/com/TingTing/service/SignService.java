package com.TingTing.service;

import com.TingTing.dto.*;
import com.TingTing.entity.User;
import com.TingTing.repository.UserRepository;
import com.TingTing.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class SignService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public void signUp(SignUpRequest dto) {
        if (userRepository.existsByUsEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }
        if (userRepository.existsByUsNickname(dto.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        User user = User.builder()
                .usEmail(dto.getEmail())
                .usPw(passwordEncoder.encode(dto.getPassword()))
                .usNickname(dto.getNickname())
                .build();
        userRepository.save(user);
    }

    public TokenResponse logIn(SignInRequest dto) {
        User user = userRepository.findByUsEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("이메일이 존재하지 않습니다."));

        if (!passwordEncoder.matches(dto.getPassword(), user.getUsPw())) {
            throw new RuntimeException("비밀번호가 틀렸습니다.");
        }

        String token = jwtTokenProvider.createToken(user.getUsEmail());
        return new TokenResponse(token, user.getUsNickname());
    }

    public void verifyEmail(String token) {
        String email = jwtTokenProvider.getEmailFromToken(token);
        User user = userRepository.findByUsEmail(email).orElseThrow();
        // 이메일 인증 완료 로직 (필드가 없으므로 생략)
    }
}
