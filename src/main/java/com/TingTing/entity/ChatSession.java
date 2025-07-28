package com.TingTing.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_session")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ChatSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sessin_id" , nullable = false)
    private int sessinId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "us_idx" , nullable = false)
    private User usIdx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conditios_id" , nullable = false)
    private Conditions conditiosId;

    @Column(name = "created_at" , nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}