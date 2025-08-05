package com.TingTing.repository;

import com.TingTing.entity.ChatSession;
import com.TingTing.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Integer> {

    List<ChatSession> findAllByUsIdx(User user);
}
