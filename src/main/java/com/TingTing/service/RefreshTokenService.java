package com.TingTing.service;

import com.TingTing.entity.RefreshToken;
import com.TingTing.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public void saveOrUpdate(String email, String token, long expiresInMillis) {
        LocalDateTime expiry = LocalDateTime.now().plusSeconds(expiresInMillis / 1000);

        refreshTokenRepository.findByEmail(email)
                .ifPresentOrElse(
                        existing -> {
                            existing.updateToken(token, expiry);
                            refreshTokenRepository.save(existing);
                        },
                        () -> {
                            RefreshToken refreshToken = RefreshToken.builder()
                                    .email(email)
                                    .token(token)
                                    .expiryDate(expiry)
                                    .build();
                            refreshTokenRepository.save(refreshToken);
                        }
                );
    }

    public boolean isValid(String email, String token) {
        return refreshTokenRepository.findByEmail(email)
                .filter(rt -> rt.getToken().equals(token))
                .filter(rt -> rt.getExpiryDate().isAfter(LocalDateTime.now()))
                .isPresent();
    }

    public void delete(String email) {
        refreshTokenRepository.deleteByEmail(email);
    }
}
