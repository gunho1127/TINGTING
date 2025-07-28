package com.TingTing.controller;

import com.TingTing.dto.ChatStartRequestDto;
import com.TingTing.dto.ChatStartResponseDto;
import com.TingTing.dto.ConditionRequestDto;
import com.TingTing.dto.ConditionResponseDto;
import com.TingTing.entity.User;
import com.TingTing.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
//
//    @PostMapping("/send")
//    public ResponseEntity<ChatMessageResponseDto> sendMessage(
//            @RequestBody ChatMessageRequestDto requestDto,
//            @AuthenticationPrincipal UserDetailsImpl userDetails
//    ) {
//        Long userId = userDetails.getUser().getId();
//        ChatMessageResponseDto response = chatService.sendMessage(userId, requestDto);
//        return ResponseEntity.ok(response);
//    }

}


