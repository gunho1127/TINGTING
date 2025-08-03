package com.TingTing.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "conditions")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conditions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conditions_id", nullable = false)
    private int conditionsId;

    @Column(name = "ai_name", nullable = false, length = 100)
    private String aiName;

    @Column(name = "ai_gender", nullable = false, length = 100)
    private String aiGender;

    @Column(name = "ai_age", nullable = false, length = 50)
    private String aiAge;

    @Column(name = "ai_job", length = 50)
    private String aiJob;

    @Column(name = "ai_chat_style", columnDefinition = "TEXT")
    private String aiChatStyle;

    @Column(name = "ai_personality", columnDefinition = "TEXT")
    private String aiPersonality;

    @Column(name = "ai_interests", columnDefinition = "TEXT")
    private String aiInterests;
}

