package com.TingTing.repository;

import com.TingTing.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Integer> {
}
