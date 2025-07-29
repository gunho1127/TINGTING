package com.TingTing.service;

import com.TingTing.dto.ChatStartResponseDto;
import com.TingTing.dto.ConditionRequestDto;
import com.TingTing.dto.ConditionResponseDto;
import com.TingTing.entity.ChatLog;
import com.TingTing.entity.ChatSession;
import com.TingTing.entity.Conditions;
import com.TingTing.entity.User;
import com.TingTing.gpt.GptClient;
import com.TingTing.mapper.ChatLogMapper;
import com.TingTing.mapper.ConditionMapper;
import com.TingTing.mapper.ChatSessionMapper;
import com.TingTing.repository.ChatSessionRepository;
import com.TingTing.repository.ConditionsRepository;
import com.TingTing.repository.ChatLogRepository;
import com.TingTing.util.PromptBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {

    private final ConditionsRepository conditionsRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final ChatLogRepository chatLogRepository;
    private final GptClient gptClient;

    public ConditionResponseDto saveCondition(ConditionRequestDto dto) {
        Conditions condition = ConditionMapper.toEntity(dto);
        Conditions saved = conditionsRepository.save(condition);
        return ConditionMapper.toDto(saved);
    }

    public ChatStartResponseDto startSession(User user, int conditionId) {
        Conditions condition = conditionsRepository.findById(conditionId)
                .orElseThrow(() -> new IllegalArgumentException("조건을 찾을 수 없습니다."));

        // 채팅 세션 생성
        ChatSession session = ChatSessionMapper.toEntity(user, condition);
        chatSessionRepository.save(session);

        // 프롬프트 생성
        String prompt = PromptBuilder.buildFromCondition(condition, user);

        // 1. 프롬프트를 SYSTEM 역할로 ChatLog 저장
        ChatLog systemLog = ChatLogMapper.toEntity(session, prompt, "SYSTEM");
        chatLogRepository.save(systemLog);

        // 2. GPT API 호출
        String aiMessage = gptClient.getFirstMessage(prompt);

        // 3. AI 첫 메시지 로그 저장
        ChatLog aiLog = ChatLogMapper.toEntity(session, aiMessage, "AI");
        chatLogRepository.save(aiLog);

        // 4. 응답 반환
        return ChatSessionMapper.toResponse(session, aiMessage);
    }


}


