package com.TingTing.mapper;

import com.TingTing.dto.ChatLogDTO;
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

    public static ChatLogDTO toDto(ChatLog log) {
        return new ChatLogDTO(
                log.getSession().getSessionId(),
                log.getLogId(),
                log.getChatMessage(),
                log.getChatRole(),
                log.getCreatedAt()
        );
    }
}
