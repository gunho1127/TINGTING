package com.TingTing.mapper;

import com.TingTing.dto.ChatAnalysisResponseDto;
import com.TingTing.entity.ChatAnalysis;
import org.json.JSONObject;

public class ChatAnalysisMapper {

    public static ChatAnalysis toEntity(ChatAnalysisResponseDto dto, int sessionId) {
        return ChatAnalysis.builder()
                .sessionId(sessionId)
                .favorabilityScore(dto.getFavorabilityScore())
                .prosConsComment(dto.getProsConsComment())

                .conversationFlow1(dto.getConversationFlow1())
                .conversationFlow2(dto.getConversationFlow2())
                .conversationFlow3(dto.getConversationFlow3())

                .partnerTrait1(dto.getPartnerTrait1())
                .partnerTrait2(dto.getPartnerTrait2())
                .partnerTrait3(dto.getPartnerTrait3())

                .myStyle1(dto.getMyStyle1())
                .myStyle2(dto.getMyStyle2())
                .myStyle3(dto.getMyStyle3())

                .strength1(dto.getStrength1())
                .strength2(dto.getStrength2())
                .strength3(dto.getStrength3())

                .improvement1(dto.getImprovement1())
                .improvement2(dto.getImprovement2())
                .improvement3(dto.getImprovement3())

                .totalScore(dto.getTotalScore())
                .mannerScore(dto.getMannerScore())
                .mannerFeedback(dto.getMannerFeedback())
                .senseScore(dto.getSenseScore())
                .senseFeedback(dto.getSenseFeedback())
                .conversationScore(dto.getConversationScore())
                .conversationFeedback(dto.getConversationFeedback())
                .considerationScore(dto.getConsiderationScore())
                .considerationFeedback(dto.getConsiderationFeedback())
                .humorScore(dto.getHumorScore())
                .humorFeedback(dto.getHumorFeedback())
                .build();
    }

    public static ChatAnalysisResponseDto toDto(ChatAnalysis entity) {
        return new ChatAnalysisResponseDto(
                entity.getFavorabilityScore(),
                entity.getProsConsComment(),

                entity.getConversationFlow1(),
                entity.getConversationFlow2(),
                entity.getConversationFlow3(),

                entity.getPartnerTrait1(),
                entity.getPartnerTrait2(),
                entity.getPartnerTrait3(),

                entity.getMyStyle1(),
                entity.getMyStyle2(),
                entity.getMyStyle3(),

                entity.getStrength1(),
                entity.getStrength2(),
                entity.getStrength3(),

                entity.getImprovement1(),
                entity.getImprovement2(),
                entity.getImprovement3(),

                entity.getTotalScore(),
                entity.getMannerScore(),
                entity.getMannerFeedback(),
                entity.getSenseScore(),
                entity.getSenseFeedback(),
                entity.getConversationScore(),
                entity.getConversationFeedback(),
                entity.getConsiderationScore(),
                entity.getConsiderationFeedback(),
                entity.getHumorScore(),
                entity.getHumorFeedback()
        );
    }

    public static ChatAnalysisResponseDto toDto(JSONObject json) {
        return new ChatAnalysisResponseDto(
                json.getInt("상대가 느끼는 호감도 점수"),
                json.getString("상대가 생각한 장단점 한마디"),

                json.getString("대화 분석_대화 흐름1"),
                json.getString("대화 분석_대화 흐름2"),
                json.getString("대화 분석_대화 흐름3"),

                json.getString("대화 분석_상대의 성향 파악1"),
                json.getString("대화 분석_상대의 성향 파악2"),
                json.getString("대화 분석_상대의 성향 파악3"),

                json.getString("대화 분석_나의 대화 스타일1"),
                json.getString("대화 분석_나의 대화 스타일2"),
                json.getString("대화 분석_나의 대화 스타일3"),

                json.getString("피드백-장점1"),
                json.getString("피드백-장점2"),
                json.getString("피드백-장점3"),

                json.getString("피드백-보완할점1"),
                json.getString("피드백-보완할점2"),
                json.getString("피드백-보완할점3"),

                json.getInt("소개팅 종합 점수 총점"),
                json.getInt("매너 점수"),
                json.getString("매너 피드백"),
                json.getInt("센스 점수"),
                json.getString("센스 피드백"),
                json.getInt("대화 점수"),
                json.getString("대화 피드백"),
                json.getInt("배려 점수"),
                json.getString("배려 피드백"),
                json.getInt("유머 점수"),
                json.getString("유머 피드백")
        );
    }

}
