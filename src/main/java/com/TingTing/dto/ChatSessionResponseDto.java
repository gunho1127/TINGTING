package com.TingTing.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatSessionResponseDto {
    private int sessionId;             // 세션 고유 ID
    private String title;              // 대화 제목
    private LocalDateTime createdAt;   // 생성 날짜

    // ✅ 프론트에서 카드에 자동 렌더링되는 주요 정보들
    private String partnerGender;      // 성별 ("male", "female", "남", "여" 등 가능)
    private Integer partnerAge;        // 나이 (숫자 형태)
    private String partnerJob;         // 직업 (없으면 "-")
    private Integer messageCount;      // 메시지 수 (프론트는 0이면 "0개"로 표시)
}
