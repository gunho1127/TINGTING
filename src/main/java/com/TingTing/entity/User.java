package com.TingTing.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "uesr")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "us_idx", nullable = false)
    private int usIdx;

    @Column(name = "us_profile_img", columnDefinition = "TEXT")
    @Builder.Default
    private String usProfileImg = "profile.png";

    @Column(name = "us_email", length = 50 , nullable = false , unique = true)
    private String usEmail;

    @Column(name = "us_pw" , length = 10, nullable = false)
    private String usPw;

    @Column(name = "us_nickname" , length = 50 , nullable = false , unique = true)
    private String usNickname;

    @Column(name = "created_at" , nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    // 권한 고정 (필요시 Role 컬럼 추가하여 동적 처리 가능)
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }
}
