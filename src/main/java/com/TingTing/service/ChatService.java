package com.TingTing.service;

import com.TingTing.dto.*;
import com.TingTing.entity.ChatLog;
import com.TingTing.entity.ChatSession;
import com.TingTing.entity.Conditions;
import com.TingTing.entity.User;
import com.TingTing.gpt.GptClient;
import com.TingTing.gpt.GptMessage;
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

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    public ChatMessageResponseDto sendMessage(ChatMessageRequestDto requestDto) {
        // 1. 세션 조회
        ChatSession session = chatSessionRepository.findById(requestDto.getSessionId())
                .orElseThrow(() -> new IllegalArgumentException("채팅 세션이 존재하지 않습니다."));

        // 2. 기존 로그를 GPT 메시지 형식으로 변환
        List<GptMessage> messageList = session.getChatLogs().stream()
                .sorted(Comparator.comparing(ChatLog::getCreatedAt))
                .map(log -> new GptMessage(log.getChatRole().toLowerCase(), log.getChatMessage()))
                .collect(Collectors.toList());

        // 3. 유저 메시지 로그 저장 및 메시지 리스트에 추가
        ChatLog userLog = ChatLogMapper.toEntity(session, requestDto.getMessage(), "USER");
        chatLogRepository.save(userLog);
        messageList.add(new GptMessage("user", requestDto.getMessage()));

        // 4. GPT 응답 받아오기
        String reply = gptClient.getReply(messageList);
        System.out.println(reply);

        // 5. AI 응답 로그 저장
        ChatLog aiLog = ChatLogMapper.toEntity(session, reply, "AI");
        chatLogRepository.save(aiLog);

        // 6. 응답 DTO 반환
        return new ChatMessageResponseDto(session.getSessionId(), reply, "AI");
    }



}


