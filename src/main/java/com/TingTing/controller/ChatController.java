package com.TingTing.controller;

import com.TingTing.dto.*;
import com.TingTing.entity.ChatLog;
import com.TingTing.entity.ChatSession;
import com.TingTing.entity.User;
import com.TingTing.mapper.ChatLogMapper;
import com.TingTing.mapper.ChatSessionMapper;
import com.TingTing.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/conditions")
    public ResponseEntity<ConditionResponseDto> createCondition(
            @RequestBody ConditionRequestDto requestDto,
            @AuthenticationPrincipal User user
    ) {
        int userId = user.getUsIdx();
        ConditionResponseDto response = chatService.saveCondition(requestDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/start")
    public ResponseEntity<ChatStartResponseDto> startChat(
            @RequestBody ChatStartRequestDto requestDto,
            @AuthenticationPrincipal User user
    ) {
        ChatStartResponseDto response = chatService.startSession(user, requestDto.getConditionId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/analysis")
    public ResponseEntity<ChatAnalysisResponseDto> analyzeChat(
            @RequestBody ChatAnalysisRequestDto requestDto,
            @AuthenticationPrincipal User user
    ) {
        ChatAnalysisResponseDto analysis = chatService.analyzeSession(user, requestDto.getSessionId());
        return ResponseEntity.ok(analysis);
    }

    // ✅ 1. 내 세션 목록 조회
    @GetMapping("/sessions")
    public ResponseEntity<List<ChatSessionResponseDto>> getMySessions(@AuthenticationPrincipal User user) {
        List<ChatSession> sessions = chatService.getChatSessions(user.getUsIdx());
        List<ChatSessionResponseDto> dtos = sessions.stream()
                .map(ChatSessionMapper::toDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    // ✅ 2. 특정 세션의 채팅 로그 조회
    @GetMapping("/sessions/{sessionId}/logs")
    public ResponseEntity<List<ChatHistoryDto>> getChatLogs(@AuthenticationPrincipal User user,
                                                            @PathVariable int sessionId) {
        List<ChatLog> logs = chatService.getChatLogs(sessionId, user.getUsIdx());
        List<ChatHistoryDto> dtos = logs.stream()
                .map(ChatLogMapper::toDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }
}
