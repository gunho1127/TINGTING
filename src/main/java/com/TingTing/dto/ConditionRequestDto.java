package com.TingTing.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

// 요청 DTO
@Getter
@Setter
public class ConditionRequestDto {
    private String aiName;
    private String aiGender;
    private String aiAge;
    private String aiJob;
    private String aiChatStyle;     // "리드형,수다쟁이"
    private String aiPersonality;   // "다정,유머"
    private String aiInterests;     // "영화,음악"
}


