package com.TingTing.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailVerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;               // 대상 이메일
    private String code;                // 인증 코드
    private boolean verified = false;   // 인증 여부

    private LocalDateTime createdAt;    // 생성 시각
    private LocalDateTime expiresAt;    // 만료 시각
    private LocalDateTime verifiedAt;   // 인증 성공 시각
}

