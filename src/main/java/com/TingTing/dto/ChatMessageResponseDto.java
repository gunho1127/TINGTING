package com.TingTing.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponseDto {

    private int sessionId;     // 어떤 세션에 속한 메시지인지
    private String message;    // GPT가 생성한 응답 메시지
    private String role;       // "AI" 또는 "SYSTEM" 등 (옵션)
}