package com.TingTing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MyDatingStatsDto {
    private int totalConversations; // 총 대화 수
    private int avgFavorability;    // 평균 호감도 (반올림)
    private int avgTotalScore;      // 평균 총점 (반올림)
}
