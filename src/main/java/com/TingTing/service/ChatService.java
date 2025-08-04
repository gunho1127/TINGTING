package com.TingTing.service;

import com.TingTing.dto.*;
import com.TingTing.entity.*;
import com.TingTing.gpt.GptClient;
import com.TingTing.gpt.GptMessage;
import com.TingTing.mapper.ChatAnalysisMapper;
import com.TingTing.mapper.ChatLogMapper;
import com.TingTing.mapper.ConditionMapper;
import com.TingTing.mapper.ChatSessionMapper;
import com.TingTing.repository.ChatAnalysisRepository;
import com.TingTing.repository.ChatSessionRepository;
import com.TingTing.repository.ConditionsRepository;
import com.TingTing.repository.ChatLogRepository;
import com.TingTing.util.PromptBuilder;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
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
    private final ChatAnalysisRepository chatAnalysisRepository;

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

        // 5. AI 응답 로그 저장
        ChatLog aiLog = ChatLogMapper.toEntity(session, reply, "AI");
        chatLogRepository.save(aiLog);

        // 6. 응답 DTO 반환
        return new ChatMessageResponseDto(session.getSessionId(), reply, "AI");
    }

    public ChatAnalysisResponseDto analyzeSession(User user, int sessionId) {
        // 1. 세션 검증
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("세션을 찾을 수 없습니다."));

        if (session.getUsIdx().getUsIdx() != user.getUsIdx()) {
            throw new IllegalArgumentException("세션에 접근할 권한이 없습니다.");
        }

        // 2. 대화 로그 불러오기 및 GptMessage로 변환
        List<ChatLog> logs = chatLogRepository.findBySession_SessionId(sessionId);
        List<GptMessage> messages = logs.stream()
                .sorted(Comparator.comparing(ChatLog::getCreatedAt))
                .map(log -> new GptMessage(log.getChatRole().toLowerCase(), log.getChatMessage()))
                .collect(Collectors.toList());

        // 3. 프롬프트 삽입
        String prompt = PromptBuilder.buildAnalysisPrompt();
        messages.add(0, new GptMessage("system", prompt));

        // 4. GPT 응답 JSON 파싱
        String gptResponse = gptClient.getReply(messages);
        JSONObject json = new JSONObject(gptResponse);

        // 5. JSON → DTO
        ChatAnalysisResponseDto dto = ChatAnalysisMapper.toDto(json);

        // 6. DTO → Entity → 저장
        ChatAnalysis entity = ChatAnalysisMapper.toEntity(dto, sessionId);
        chatAnalysisRepository.save(entity);

        return dto;
    }






}


