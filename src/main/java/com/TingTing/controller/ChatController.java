package com.TingTing.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/start")
    public ResponseEntity<ChatStartResponseDto> startChat(
            @RequestBody ChatStartRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        ChatStartResponseDto response = chatService.startSession(userId, requestDto.getConditionId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send")
    public ResponseEntity<ChatMessageResponseDto> sendMessage(
            @RequestBody ChatMessageRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        ChatMessageResponseDto response = chatService.sendMessage(userId, requestDto);
        return ResponseEntity.ok(response);
    }

}


