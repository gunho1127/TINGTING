package com.TingTing.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_analysis")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analysis_id", nullable = false)
    private int analysisId;

    @Column(name = "session_id", nullable = false)
    private int sessionId;

    @Column(name = "favorability_score")
    private int favorabilityScore;

    @Column(name = "pros_cons_comment", columnDefinition = "TEXT")
    private String prosConsComment;

    @Column(name = "conversation_flow_1", columnDefinition = "TEXT")
    private String conversationFlow1;

    @Column(name = "conversation_flow_2", columnDefinition = "TEXT")
    private String conversationFlow2;

    @Column(name = "conversation_flow_3", columnDefinition = "TEXT")
    private String conversationFlow3;

    @Column(name = "partner_trait_1", columnDefinition = "TEXT")
    private String partnerTrait1;

    @Column(name = "partner_trait_2", columnDefinition = "TEXT")
    private String partnerTrait2;

    @Column(name = "partner_trait_3", columnDefinition = "TEXT")
    private String partnerTrait3;

    @Column(name = "my_style_1", columnDefinition = "TEXT")
    private String myStyle1;

    @Column(name = "my_style_2", columnDefinition = "TEXT")
    private String myStyle2;

    @Column(name = "my_style_3", columnDefinition = "TEXT")
    private String myStyle3;

    @Column(name = "strength_1", columnDefinition = "TEXT")
    private String strength1;

    @Column(name = "strength_2", columnDefinition = "TEXT")
    private String strength2;

    @Column(name = "strength_3", columnDefinition = "TEXT")
    private String strength3;

    @Column(name = "improvement_1", columnDefinition = "TEXT")
    private String improvement1;

    @Column(name = "improvement_2", columnDefinition = "TEXT")
    private String improvement2;

    @Column(name = "improvement_3", columnDefinition = "TEXT")
    private String improvement3;

    @Column(name = "total_score")
    private int totalScore;

    @Column(name = "manner_score")
    private int mannerScore;

    @Column(name = "manner_feedback", columnDefinition = "TEXT")
    private String mannerFeedback;

    @Column(name = "sense_score")
    private int senseScore;

    @Column(name = "sense_feedback", columnDefinition = "TEXT")
    private String senseFeedback;

    @Column(name = "conversation_score")
    private int conversationScore;

    @Column(name = "conversation_feedback", columnDefinition = "TEXT")
    private String conversationFeedback;

    @Column(name = "consideration_score")
    private int considerationScore;

    @Column(name = "consideration_feedback", columnDefinition = "TEXT")
    private String considerationFeedback;

    @Column(name = "humor_score")
    private int humorScore;

    @Column(name = "humor_feedback", columnDefinition = "TEXT")
    private String humorFeedback;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
