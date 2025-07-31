package com.TingTing.controller;

import com.TingTing.dto.UserDTO;
import com.TingTing.dto.ChatLogDTO;
import com.TingTing.service.UserService;
import com.TingTing.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import com.TingTing.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ChatService chatService;

    // ✅ 내 정보 조회 (JWT 기반)
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMyProfile(@AuthenticationPrincipal User user) {
        UserDTO dto = userService.getUserProfile(user.getUsIdx());
        return ResponseEntity.ok(dto);
    }

    // ✅ 닉네임 수정
    @PutMapping("/me/nickname")
    public ResponseEntity<Void> updateNickname(@AuthenticationPrincipal User user,
                                               @RequestParam("nickname") String nickname) {
        userService.updateNickname(user.getUsIdx(), nickname);
        return ResponseEntity.ok().build();
    }

    // ✅ 비밀번호 변경
    @PutMapping("/me/password")
    public ResponseEntity<Void> updatePassword(@AuthenticationPrincipal User user,
                                               @RequestParam("currentPw") String currentPw,
                                               @RequestParam("newPw") String newPw) {
        userService.updatePassword(user.getUsIdx(), currentPw, newPw);
        return ResponseEntity.ok().build();
    }

    // ✅ 내 채팅 기록 조회
    @GetMapping("/me/chatlogs")
    public ResponseEntity<List<ChatLogDTO>> getMyChatLogs(@AuthenticationPrincipal User user) {
        List<ChatLogDTO> logs = chatService.getUserChatLogs(user);
        return ResponseEntity.ok(logs);
    }

    // ❓ 특정 유저 정보 조회 (관리자용 또는 테스트용)
    @GetMapping("/{usIdx}")
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable("usIdx") int usIdx) {
        UserDTO user = userService.getUserProfile(usIdx);
        return ResponseEntity.ok(user);
    }
}
