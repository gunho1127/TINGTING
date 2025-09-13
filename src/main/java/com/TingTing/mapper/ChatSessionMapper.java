package com.TingTing.mapper;

import com.TingTing.dto.ChatSessionResponseDto;
import com.TingTing.dto.ChatStartResponseDto;
import com.TingTing.entity.ChatSession;
import com.TingTing.entity.Conditions;
import com.TingTing.entity.User;

import java.util.Optional;

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

    // 입력: ChatSession (이미 영속/트랜잭션 안에서 조회되어 있다고 가정)
    public static ChatSessionResponseDto toDto(ChatSession session) {
        Conditions cond = session.getConditionsId();   // ★ 연결된 Conditions 읽기

        // --- 타이틀 구성 ---
        String aiName = cond != null ? nullToBlank(cond.getAiName()) : "";
        String aiPersonality = cond != null ? nullToBlank(cond.getAiPersonality()) : "";
        String firstTrait = aiPersonality.isBlank() ? "" : aiPersonality.split(",")[0].trim();

        String title = (firstTrait.isBlank() ? "" : (firstTrait + "한 "))
                + (aiName.isBlank() ? "AI" : aiName)
                + "님과의 설레는 첫 소개팅~";

        // --- 카드 핵심 정보 ---
        String partnerGender = cond != null ? normalizeGender(cond.getAiGender()) : "-";
        Integer partnerAge   = cond != null ? parseAge(cond.getAiAge()) : null;   // null이면 프론트가 "-" 처리
        String partnerJob    = cond != null ? dashIfBlank(cond.getAiJob()) : "-";

        // 메시지 수 (간단 버전: 엔티티 컬렉션 크기 사용. 필요시 repo에서 count로 받으세요)
        int messageCount = Optional.ofNullable(session.getChatLogs())
                .map(list -> list.size())
                .orElse(0);

        return ChatSessionResponseDto.builder()
                .sessionId(session.getSessionId())
                .title(title)
                .createdAt(session.getCreatedAt())
                .partnerGender(partnerGender)
                .partnerAge(partnerAge)
                .partnerJob(partnerJob)
                .messageCount(messageCount)
                .build();
    }

    // ----------------- helpers -----------------
    private static String nullToBlank(String v) {
        return v == null ? "" : v.trim();
    }

    private static String dashIfBlank(String v) {
        return (v == null || v.isBlank()) ? "-" : v.trim();
    }

    // "male", "여", "FEMALE", "남자" → "남"/"여"/"-" 통일
    private static String normalizeGender(String v) {
        if (v == null) return "-";
        String s = v.trim().toLowerCase();
        if (s.startsWith("m") || s.equals("남") || s.contains("남자")) return "남";
        if (s.startsWith("f") || s.equals("여") || s.contains("여자")) return "여";
        return "-";
    }

    // "27", "27세", "만27", "나이:27" → 27
    private static Integer parseAge(String v) {
        if (v == null) return null;
        String digits = v.replaceAll("[^0-9]", "");
        return digits.isEmpty() ? null : Integer.valueOf(digits);
    }
}


