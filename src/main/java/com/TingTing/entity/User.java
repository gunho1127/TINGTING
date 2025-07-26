package com.TingTing.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    @Column(name = "us_pw" , length = 255, nullable = false)
    private String usPw;

    @Column(name = "us_nickname" , length = 50 , nullable = false , unique = true)
    private String usNickname;

    @Column(name = "created_at" , nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    // 닉네임 변경 메서드
    public void changeNickname(String newNickname) {
        this.usNickname = newNickname;
    }

}
