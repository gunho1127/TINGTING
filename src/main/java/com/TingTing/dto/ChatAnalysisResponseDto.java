package com.TingTing.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatAnalysisResponseDto {

    private int favorabilityScore;
    private String prosConsComment;

    private String conversationFlow1;
    private String conversationFlow2;
    private String conversationFlow3;

    private String partnerTrait1;
    private String partnerTrait2;
    private String partnerTrait3;

    private String myStyle1;
    private String myStyle2;
    private String myStyle3;

    private String strength1;
    private String strength2;
    private String strength3;

    private String improvement1;
    private String improvement2;
    private String improvement3;

    private int totalScore;
    private int mannerScore;
    private String mannerFeedback;
    private int senseScore;
    private String senseFeedback;
    private int conversationScore;
    private String conversationFeedback;
    private int considerationScore;
    private String considerationFeedback;
    private int humorScore;
    private String humorFeedback;
}
