package com.TingTing.service;

import com.TingTing.dto.UserDTO;
import com.TingTing.entity.User;
import com.TingTing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDTO getUserProfile(int usIdx) {
        User user = userRepository.findById(usIdx)
                .orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다."));

        return UserDTO.builder()
                .usIdx(user.getUsIdx())
                .usEmail(user.getUsEmail())
                .usPw(user.getUsPw())
                .usNickname(user.getUsNickname())
                .usProfileImg(user.getUsProfileImg())
                .createdAt(user.getCreatedAt())
                .build();
    }
    public void updateNickname(int usIdx, String nickname) {
        if (userRepository.existsByUsNickname(nickname)) {  // 중복 체크
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        User user = userRepository.findById(usIdx)
                .orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다."));

        user.changeNickname(nickname);
        userRepository.save(user);
    }
}
