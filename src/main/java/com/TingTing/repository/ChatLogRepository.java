package com.TingTing.repository;

import com.TingTing.entity.ChatLog;
import com.TingTing.entity.ChatSession;
import com.TingTing.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatLogRepository extends JpaRepository<ChatLog, Integer> {

    // 특정 세션의 전체 로그 조회
    List<ChatLog> findBySession(ChatSession session);

    // 세션으로 로그 일괄 삭제 (필요 시)
    void deleteAllBySession(ChatSession session);
}