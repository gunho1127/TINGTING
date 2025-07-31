package com.TingTing.controller;

import com.TingTing.dto.ResponseDTO;
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

    private final SignService signService;

    @PostMapping("/verification/email")
    public ResponseEntity<ResponseDTO> sendVerificationEmail(@RequestParam String email) {
        if (signService.isEmailExist(email)) {
            return ResponseEntity.ok(new ResponseDTO(false, "이미 가입된 이메일입니다."));
        }

        signService.sendVerificationEmail(email);
        return ResponseEntity.ok(new ResponseDTO(true, "인증코드를 전송했습니다."));
    }

    @PostMapping("/verification/confirm")
    public ResponseEntity<ResponseDTO> verifyEmail(
            @RequestParam("email") String email,
            @RequestParam("code") String code) {
        ResponseDTO result = signService.checkVerificationCode(email, code);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/signup")
    public ResponseEntity<ResponseDTO> signup(@RequestBody SignUpRequest request) {
        if (signService.isEmailExist(request.getEmail())) {
            return ResponseEntity.ok(new ResponseDTO(false, "이미 가입된 이메일입니다."));
        }

        if (!signService.isEmailVerified(request.getEmail())) {
            return ResponseEntity.ok(new ResponseDTO(false, "이메일 인증을 먼저 완료해주세요."));
        }

        signService.signup(request);
        return ResponseEntity.ok(new ResponseDTO(true, "회원가입이 완료되었습니다."));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> signin(@RequestBody SignInRequest dto,
                                                HttpServletResponse response) {
        System.out.println(dto);
        return ResponseEntity.ok(signService.logIn(dto, response));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal User user,
                                       HttpServletResponse response) {
        signService.logOut(user, response);
        return ResponseEntity.ok().build();
    }



}