package com.TingTing.service;

import com.TingTing.dto.UserDTO;
import com.TingTing.entity.User;
import com.TingTing.mapper.UserMapper;
import com.TingTing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

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
}