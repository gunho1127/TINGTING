package com.TingTing.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_analysis")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Chat_analysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analysis_id", nullable = false)
    private int analysisId;

    @Column(name = "session_id", nullable = false)
    private int sessionId;

    @Column(name = "compatibility_score")
    private int compatibilityScore;

    @Column(name = "emotion_flow" , columnDefinition = "TEXT")
    @Builder.Default
    private int emotionFlow = 0;

    @Column(name = "feedback_summary" , columnDefinition = "TEXT")
    @Builder.Default
    private int feedbackSummary = 0;

    @Column(name = "created_at" , nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}