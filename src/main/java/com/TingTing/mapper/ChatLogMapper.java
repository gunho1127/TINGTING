package com.TingTing.mapper;

import com.TingTing.dto.ChatHistoryDto;
import com.TingTing.entity.ChatLog;
import com.TingTing.entity.ChatSession;

public class ChatLogMapper {

    public static ChatLog toEntity(ChatSession session, String message, String role) {
        return ChatLog.builder()
                .session(session)
                .chatMessage(message)
                .chatRole(role)
                .build();
    }

    //채팅 기록 조회
    public static ChatHistoryDto toDto(ChatLog log) {
        return ChatHistoryDto.builder()
                .message(log.getChatMessage())
                .role(log.getChatRole())
                .timestamp(log.getCreatedAt())
                .build();
    }
}