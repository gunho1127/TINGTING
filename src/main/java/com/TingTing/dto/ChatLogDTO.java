package com.TingTing.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatLogDTO {
    private int sessionId;
    private int logId;
    private String chatMessage;
    private String chatRole;
    private LocalDateTime createdAt;
}
