package com.TingTing.controller;

import com.TingTing.dto.ChatMessageRequestDto;
import com.TingTing.dto.ChatMessageResponseDto;
import com.TingTing.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/send") // 클라이언트에서 "/app/send"로 전송하면 처리됨
    public void receive(ChatMessageRequestDto request) {
        System.out.println("/send 요청 받음");
        // ChatService 사용해 GPT 호출 + DB 저장
        ChatMessageResponseDto response = chatService.sendMessage(request);

        // /topic/chat/{sessionId} 구독자에게 응답 push
        messagingTemplate.convertAndSend("/topic/chat/" + request.getSessionId(), response);
    }

    @MessageMapping("/test")
    public void test(String message) {
        System.out.println("✅ /test 요청 받음: " + message);
    }

}
