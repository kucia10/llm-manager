package com.example.llm.service;

import com.example.llm.dto.request.QuotaSetRequest;
import com.example.llm.dto.request.TeamCreateRequest;
import com.example.llm.dto.request.TeamUpdateRequest;
import com.example.llm.dto.response.TeamResponse;
import com.example.llm.entity.Team;
import com.example.llm.exception.ErrorCode;
import com.example.llm.exception.ResourceNotFoundException;
import com.example.llm.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TeamService {
    
    private final TeamRepository teamRepository;
    
    public TeamResponse createTeam(TeamCreateRequest request) {
        Team team = Team.builder()
                .name(request.getName())
                .quota(request.getQuota())
                .usage(0L)
                .build();
        Team savedTeam = teamRepository.save(team);
        return mapToTeamResponse(savedTeam);
    }
    
    @Transactional(readOnly = true)
    public List<TeamResponse> getAllTeams() {
        return teamRepository.findAll().stream()
                .map(this::mapToTeamResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public TeamResponse getTeamById(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.TEAM_NOT_FOUND));
        return mapToTeamResponse(team);
    }
    
    public TeamResponse updateTeam(Long id, TeamUpdateRequest request) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.TEAM_NOT_FOUND));
        
        team.setName(request.getName());
        team.setQuota(request.getQuota());
        
        return mapToTeamResponse(teamRepository.save(team));
    }
    
    public void deleteTeam(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.TEAM_NOT_FOUND));
        teamRepository.delete(team);
    }
    
    public TeamResponse setQuota(Long id, QuotaSetRequest request) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.TEAM_NOT_FOUND));
        
        team.setQuota(request.getQuota());
        
        return mapToTeamResponse(teamRepository.save(team));
    }
    
    private TeamResponse mapToTeamResponse(Team team) {
        return TeamResponse.builder()
                .id(team.getId())
                .name(team.getName())
                .quota(team.getQuota())
                .usage(team.getUsage())
                .createdAt(team.getCreatedAt())
                .updatedAt(team.getUpdatedAt())
                .build();
    }
}