package com.TingTing.controller;

import com.TingTing.dto.ResponseDTO;
import com.TingTing.dto.SignInRequest;
import com.TingTing.dto.SignUpRequest;
import com.TingTing.dto.TokenResponse;
import com.TingTing.entity.User;
import com.TingTing.repository.UserRepository;
import com.TingTing.service.RefreshTokenService;
import com.TingTing.service.SignService;
import com.TingTing.util.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class SignController {

    private final SignService signService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

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
        return ResponseEntity.ok(signService.logIn(dto, response));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal User user,
                                       @RequestHeader(value = "Authorization", required = false) String authHeader,
                                       HttpServletResponse response) {
        try {
            // 1. user 객체가 null인 경우 → Authorization 헤더에서 email 추출
            if (user == null) {
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }

                String token = authHeader.replace("Bearer ", "");
                String email = jwtTokenProvider.getEmailFromToken(token);

                user = userRepository.findByUsEmail(email)
                        .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
            }

            // 2. 로그아웃 처리
            signService.logOut(user, response);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/find-password")
    public ResponseEntity<String> findPassword(@RequestParam String email) {
        System.out.println("입력된 이메일: " + email);
        try {
            signService.sendTemporaryPassword(email);
            return ResponseEntity.ok("임시 비밀번호가 이메일로 전송되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("비밀번호 찾기 중 오류가 발생했습니다.");
        }
    }
}