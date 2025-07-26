package com.TingTing.controller;

import com.TingTing.dto.*;
import com.TingTing.service.SignService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class SignController {

    private final SignService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignUpRequest dto) {
        authService.signUp(dto);
        return ResponseEntity.ok("회원가입 완료");
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> signin(@RequestBody SignInRequest dto) {
        return ResponseEntity.ok(authService.logIn(dto));
    }
}
