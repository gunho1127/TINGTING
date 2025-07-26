package com.TingTing.controller;

import com.TingTing.dto.UserDTO;
import com.TingTing.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ✅ 내 정보 조회 (JWT 기반)
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserDTO user = userService.getUserProfile(userDetails.getUser().getUsIdx());
        return ResponseEntity.ok(user);
    }

    // ✅ 닉네임 수정
    @PutMapping("/me/nickname")
    public ResponseEntity<Void> updateNickname(@AuthenticationPrincipal CustomUserDetails userDetails,
                                               @RequestParam("nickname") String nickname) {
        userService.updateNickname(userDetails.getUser().getUsIdx(), nickname);
        return ResponseEntity.ok().build();
    }

    // ❓ 특정 유저 정보 조회 (관리자용 또는 테스트용)
    @GetMapping("/{usIdx}")
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable("usIdx") int usIdx) {
        UserDTO user = userService.getUserProfile(usIdx);
        return ResponseEntity.ok(user);
    }
}
