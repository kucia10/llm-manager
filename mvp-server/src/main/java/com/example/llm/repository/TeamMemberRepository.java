package com.example.llm.repository;

import com.example.llm.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    
    List<TeamMember> findByTeamId(Long teamId);
    
    void deleteByTeamId(Long teamId);
}