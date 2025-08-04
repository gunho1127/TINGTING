package com.TingTing.util;

import com.TingTing.entity.Conditions;
import com.TingTing.entity.User;

public class PromptBuilder {

    public static String buildFromCondition(Conditions condition, User user) {
        StringBuilder sb = new StringBuilder();

        sb.append("나는 지금 가상의 상대와 소개팅을 진행하려고 해, 너는 소개팅 상대방 역할로 나와 대화를 할거야,");

        sb.append("나는 ")
                .append(user.getUsAge()).append("살 ")
                .append(user.getUsGender()).append("이고 직업은 ")
                .append(user.getUsJob()).append("야. ");

        sb.append("너는 이름이 ")
                .append(condition.getAiName()).append(" 이고 ")
                .append(condition.getAiAge()).append("살 ")
                .append(condition.getAiGender()).append("이고 직업은 ")
                .append(condition.getAiJob()).append("야. ");

        if (condition.getAiPersonality() != null && !condition.getAiPersonality().isEmpty()) {
            sb.append("너의 성격은 ").append(condition.getAiPersonality()).append(" 이야. ");
        }

        if (condition.getAiChatStyle() != null && !condition.getAiChatStyle().isEmpty()) {
            sb.append("너의 말투는 ").append(condition.getAiChatStyle()).append(" 이고. ");
        }

        if (condition.getAiInterests() != null && !condition.getAiInterests().isEmpty()) {
            sb.append("너의 관심사는 ").append(condition.getAiInterests()).append(" 이야. ");
        }

        sb.append("이제 소개팅을 시작할건데 너의 대화 길이나 말투, 질문등은 조건대로 적용해서 대화 시작해줘, 한명씩 대화를 주고받는거야");

        return sb.toString();
    }

    public static String buildAnalysisPrompt() {
        String prompt = "너는 소개팅 분석 전문가야. 위 소개팅 대화를 읽고 아래 형식대로만 분석해줘.  \n" +
                "형식은 꼭 지켜야 해. 말투는 해요체 과거형, 점수는 냉정하게 평가.  \n" +
                "답변은 반드시 \"변수명 : 값\" 형식의 json \n" +
                "원하는 출력 형식은 아래와 같아:  \n" +
                "- 상대가 느끼는 호감도 점수(100점 만점, 정수)  \n" +
                "- 상대가 생각한 장단점 한마디(상대 말투로, 문자열)  \n" +
                "- 대화 분석_대화 흐름1~3(약 50자, 문자열)  \n" +
                "- 대화 분석_상대의 성향 파악1~3(약 50자, 문자열)  \n" +
                "- 대화 분석_나의 대화 스타일1~3(약 50자, 문자열)  \n" +
                "- 피드백-장점1~3(구체적인 50자 문장, 문자열)  \n" +
                "- 피드백-보완할점1~3 (구체적인 50자 문장, 문자열)  \n" +
                "- 소개팅 종합 점수 총점(100점 만점, 정수)  \n" +
                "- 매너 점수(20점 만점, 정수)  \n" +
                "- 매너 피드백(간단한 문장, 문자열)  \n" +
                "- 센스 점수(20점 만점, 정수)  \n" +
                "- 센스 피드백(간단한 문장, 문자열)  \n" +
                "- 대화 점수(20점 만점, 정수)  \n" +
                "- 대화 피드백(간단한 문장, 문자열)  \n" +
                "- 배려 점수(20점 만점, 정수)  \n" +
                "- 배려 피드백(간단한 문장, 문자열)\n" +
                "- 유머 점수(20점 만점, 정수)  \n" +
                "- 유머 피드백(간단한 문장, 문자열)";

        return prompt;
    }
}

