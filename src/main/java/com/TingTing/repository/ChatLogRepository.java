package com.TingTing.repository;

import com.TingTing.entity.ChatLog;
import com.TingTing.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatLogRepository extends JpaRepository<ChatLog, Integer> {

    // 1. 세션 객체로 전체 로그 조회
    List<ChatLog> findBySession(ChatSession session);

    // 2. 세션 ID로 전체 로그 조회 (편리성 추가)
    List<ChatLog> findBySession_SessionId(int sessionId);

    // 3. 세션으로 로그 일괄 삭제
    void deleteAllBySession(ChatSession session);
}
