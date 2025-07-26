package com.TingTing.mapper;

import com.TingTing.dto.UserDTO;
import com.TingTing.entity.User;

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
}
