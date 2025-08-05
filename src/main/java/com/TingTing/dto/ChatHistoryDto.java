package com.TingTing.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatHistoryDto {
    private String message;         // chatMessage 그대로
    private String role;            // chatRole 그대로
    private LocalDateTime timestamp; // createdAt 그대로
}