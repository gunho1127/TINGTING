package com.TingTing.repository;

import com.TingTing.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    // 이메일로 유저 조회 (로그인, 중복확인 등에 사용)
    Optional<User> findByUsEmail(String usEmail);

    // 이메일 존재 여부 확인 (회원가입 시 중복 체크)
    boolean existsByUsEmail(String usEmail);

    // 닉네임으로 유저 조회 (닉네임 중복 체크 등)
    Optional<User> findByUsNickname(String usNickname);

    // 닉네임 존재 여부 확인
    boolean existsByUsNickname(String usNickname);
}