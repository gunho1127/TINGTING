package com.TingTing.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest {
    private String email;
    private String password;
    private String nickname;
    private String age;
    private String gender;
    private String job;
}