package com.TingTing.mapper;

import com.TingTing.dto.ChatStartResponseDto;
import com.TingTing.entity.ChatSession;
import com.TingTing.entity.Conditions;
import com.TingTing.entity.User;

public class ChatSessionMapper {

    public static ChatSession toEntity(User user, Conditions condition) {
        return ChatSession.builder()
                .usIdx(user)
                .conditionsId(condition)
                .build();
    }

    public static ChatStartResponseDto toResponse(ChatSession session, String aiMessage) {
        return new ChatStartResponseDto(session.getSessionId(), aiMessage);
    }
}
