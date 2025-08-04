package com.TingTing.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatSessionResponseDto {
    private int sessionId;
    private String title;              // 아래에서 설명
    private LocalDateTime createdAt;
}
