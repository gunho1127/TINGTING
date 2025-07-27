package com.TingTing.controller;

import com.TingTing.dto.SignInRequest;
import com.TingTing.dto.SignUpRequest;
import com.TingTing.dto.TokenResponse;
import com.TingTing.entity.User;
import com.TingTing.service.SignService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<TokenResponse> signin(@RequestBody SignInRequest dto,
                                                HttpServletResponse response) {
        return ResponseEntity.ok(authService.logIn(dto, response));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal User user,
                                       HttpServletResponse response) {
        authService.logOut(user, response);
        return ResponseEntity.ok().build();
    }
}