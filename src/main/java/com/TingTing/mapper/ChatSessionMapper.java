package com.TingTing.mapper;

import com.TingTing.dto.ChatSessionResponseDto;
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

    public static ChatSessionResponseDto toDto(ChatSession session) {
        String aiName = session.getConditionsId().getAiName();
        String aiPersonality = session.getConditionsId().getAiPersonality();

        // personality가 여러 개일 경우 첫 번째만 가져오도록 처리
        String firstTrait = " "; // 기본값 (예외 대비)
        if (aiPersonality != null && !aiPersonality.isBlank()) {
            firstTrait = aiPersonality.split(",")[0]; // "다정"
        }

        // 타이틀 예: "다정한 지민이와 소개팅"
        String title = firstTrait + "한 " + aiName + "님과의 설레는 첫 소개팅~";

        return new ChatSessionResponseDto(
                session.getSessionId(),
                title,
                session.getCreatedAt()
        );
    }
}
