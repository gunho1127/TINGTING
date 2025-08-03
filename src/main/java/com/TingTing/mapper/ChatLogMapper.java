package com.TingTing.mapper;

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
}
