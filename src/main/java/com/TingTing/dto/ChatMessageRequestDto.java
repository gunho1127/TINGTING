package com.TingTing.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageRequestDto {

    private int sessionId;       // 현재 채팅 세션 ID
    private String message;      // 유저가 입력한 메시지
}
