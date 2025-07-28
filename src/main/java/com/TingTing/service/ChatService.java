package com.TingTing.service;

import com.TingTing.dto.ConditionRequestDto;
import com.TingTing.dto.ConditionResponseDto;
import com.TingTing.entity.Conditions;
import com.TingTing.mapper.ConditionMapper;
import com.TingTing.repository.ConditionsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ConditionsRepository conditionsRepository;

    public ConditionResponseDto saveCondition(ConditionRequestDto dto) {
        Conditions condition = ConditionMapper.toEntity(dto);
        Conditions saved = conditionsRepository.save(condition);
        return ConditionMapper.toDto(saved);
    }
}


