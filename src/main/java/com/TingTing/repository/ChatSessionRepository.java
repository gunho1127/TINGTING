package com.TingTing.repository;

import com.TingTing.entity.ChatSession;
import com.TingTing.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Integer> {

    List<ChatSession> findAllByUsIdx(User user);

    // 로그인 유저가 가진 세션 총 개수
    @Query("select count(cs) from ChatSession cs where cs.usIdx.usIdx = :usIdx")
    long countByUserId(@Param("usIdx") int usIdx);
}
