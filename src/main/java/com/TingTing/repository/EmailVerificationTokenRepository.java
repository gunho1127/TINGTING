package com.TingTing.repository;

import com.TingTing.entity.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

    // 해당 이메일의 가장 최신 토큰 조회 (인증 여부 확인, 코드 검증 시 사용)
    Optional<EmailVerificationToken> findTopByEmailOrderByCreatedAtDesc(String email);

    // 해당 이메일로 발송된 모든 인증코드 삭제 (재요청 시 이전 코드 무효화)
    void deleteByEmail(String email);
}