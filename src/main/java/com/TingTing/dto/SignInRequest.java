package com.TingTing.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignInRequest {
    // 이메일 로그인용
    private String email;
    private String password;
}
