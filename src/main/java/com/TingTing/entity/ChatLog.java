package com.TingTing.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_log")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ChatLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id" , nullable = false)
    private int logId;

    @Column(name = "session_id" , nullable = false)
    private int sessionId;

    @Column(name = "chat_message" , nullable = false , length = 5000)
    private String chatMessage;

    @Column(name = "created_at" , nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "chat_role" , nullable = false , length = 50)
    private String chatRole;
}
