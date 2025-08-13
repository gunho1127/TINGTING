package com.TingTing.mapper;

import com.TingTing.dto.SignUpRequestDto;
import com.TingTing.dto.UserDTO;
import com.TingTing.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserMapper {
    public static UserDTO toDTO(User user) {
        if (user == null) return null;
        return UserDTO.builder()
                .usIdx(user.getUsIdx())
                .usEmail(user.getUsEmail())
                .usPw(user.getUsPw())
                .usNickname(user.getUsNickname())
                .usProfileImg(user.getUsProfileImg())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public static User toEntity(SignUpRequestDto request, PasswordEncoder passwordEncoder) {
        return User.builder()
                .usEmail(request.getEmail())
                .usPw(passwordEncoder.encode(request.getPassword()))
                .usNickname(request.getNickname())
                .usGender(request.getGender())
                .usAge(request.getAge())         // ✅ 그대로 사용
                .usJob(request.getJob())
                .build();
    }
}