package com.TingTing.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatStartResponseDto {
    private int sessionId;
    private String aiMessage;
}
