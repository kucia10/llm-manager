package com.example.llm.repository;

import com.example.llm.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    
    List<Team> findByNameContaining(String name);
    
    @Query("SELECT t FROM Team t WHERE t.quota > t.usage")
    List<Team> findTeamsWithAvailableQuota();
}