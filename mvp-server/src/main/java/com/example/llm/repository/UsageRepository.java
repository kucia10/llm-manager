package com.example.llm.repository;

import com.example.llm.entity.Usage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UsageRepository extends JpaRepository<Usage, Long> {
    
    List<Usage> findByTeamId(Long teamId);
    
    @Query("SELECT u FROM Usage u WHERE u.team.id = :teamId AND u.usedAt BETWEEN :startDate AND :endDate")
    List<Usage> findByTeamIdAndDateRange(@Param("teamId") Long teamId,
                                         @Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);
}