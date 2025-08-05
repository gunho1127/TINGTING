package com.TingTing.controller;

import com.TingTing.dto.ChangePasswordRequest;
import com.TingTing.dto.UpdateNicknameRequest;
import com.TingTing.dto.UserDTO;
import com.TingTing.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import com.TingTing.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ✅ 내 정보 조회 (JWT 기반)
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMyProfile(@AuthenticationPrincipal User user) {
        UserDTO dto = userService.getUserProfile(user.getUsIdx());
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/me/nickname")
    public ResponseEntity<Void> updateNickname(@AuthenticationPrincipal User user,
                                               @RequestBody UpdateNicknameRequest request) {
        userService.updateNickname(user.getUsIdx(), request.getNickname());
        return ResponseEntity.ok().build();
    }

    // ✅ 비밀번호 수정
    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal User user,
                                               @RequestBody ChangePasswordRequest request) {
        userService.changePassword(user.getUsIdx(), request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }

    // ❓ 특정 유저 정보 조회 (관리자용 또는 테스트용)
    @GetMapping("/{usIdx}")
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable("usIdx") int usIdx) {
        UserDTO user = userService.getUserProfile(usIdx);
        return ResponseEntity.ok(user);
    }
}