package com.TingTing.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "conditions")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Conditions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conditions_id" , nullable = false)
    private int conditionsId;

    @Column(name = "ai_gender" , nullable = false , length = 100)
    private String aiGender;

    @Column(name = "ai_age" , nullable = false , length = 50)
    private String aiAge;

    @Column(name = "ai_mbti" , length = 4)
    private String aiMbti;

    @Column(name = "ai_job" , length = 50)
    private String aiJob;

    @Column(name = "ai_interests" , columnDefinition = "TEXT")
    private String aiInterests;
}
