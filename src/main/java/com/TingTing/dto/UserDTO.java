package com.TingTing.dto; //(필요하다면, 내부 로직용)

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class UserDTO {
    private int usIdx;
    private String usEmail;
    private String usPw;
    private String usNickname;
    private String usProfileImg;
    private LocalDateTime createdAt;

}