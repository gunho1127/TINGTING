package com.TingTing.service;

import com.TingTing.dto.UserDTO;
import com.TingTing.entity.User;
import com.TingTing.mapper.UserMapper;
import com.TingTing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDTO getUserProfile(int usIdx) {
        return userRepository.findById(usIdx)
                .map(UserMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void updateNickname(int usIdx, String nickname) {
        User user = userRepository.findById(usIdx)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.updateNickname(nickname);
        userRepository.save(user);
    }

    public void changePassword(int usIdx, String oldPassword, String newPassword) {
        User user = userRepository.findById(usIdx)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("기존 비밀번호가 일치하지 않습니다.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}