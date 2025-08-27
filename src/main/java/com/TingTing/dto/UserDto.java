package com.TingTing.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class UserDto {
    private int usIdx;
    private String usEmail;
    private String usPw;
    private String usNickname;
    private String usGender;
    private String usJob;
    private String usAge;
    private String usProfileImg;
    private LocalDateTime createdAt;
    private String usProvider;
}