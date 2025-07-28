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

        sb.append("너는 ")
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

        sb.append("소개팅을 시작할건데 너의 대화 길이나 말투, 질문등은 조건대로 적용해서 바로 대화시작해줘");

        return sb.toString();
    }
}

