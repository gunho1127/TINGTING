package com.TingTing.repository;

import com.TingTing.entity.ChatAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatAnalysisRepository extends JpaRepository<ChatAnalysis, Integer> {

    // 세션 ID를 기반으로 분석 결과 조회
    Optional<ChatAnalysis> findBySessionId(int sessionId);

    // 필요 시 세션 ID로 분석 삭제
    void deleteBySessionId(int sessionId);

    @Query("""
       select avg(ca.favorabilityScore)
       from ChatAnalysis ca
       where ca.chatSession.usIdx.usIdx = :usIdx
       """)
    Double avgFavorabilityByUser(@Param("usIdx") int usIdx);

    @Query("""
       select avg(ca.totalScore)
       from ChatAnalysis ca
       where ca.chatSession.usIdx.usIdx = :usIdx
       """)
    Double avgTotalScoreByUser(@Param("usIdx") int usIdx);
}
