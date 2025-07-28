package com.TingTing.mapper;

import com.TingTing.dto.ConditionRequestDto;
import com.TingTing.dto.ConditionResponseDto;
import com.TingTing.entity.Conditions;

public class ConditionMapper {

    // RequestDto → Entity
    public static Conditions toEntity(ConditionRequestDto dto) {
        return Conditions.builder()
                .aiName(dto.getAiName())
                .aiGender(dto.getAiGender())
                .aiAge(dto.getAiAge())
                .aiJob(dto.getAiJob())
                .aiPersonality(dto.getAiPersonality())
                .aiChatStyle(dto.getAiChatStyle())
                .aiInterests(dto.getAiInterests())
                .build();
    }

    // Entity → ResponseDto
    public static ConditionResponseDto toDto(Conditions entity) {
        return new ConditionResponseDto(entity.getConditionsId());
    }
}
