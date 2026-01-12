package com.example.llm.service;

import com.example.llm.dto.response.DashboardResponse;
import com.example.llm.entity.Team;
import com.example.llm.entity.Usage;
import com.example.llm.repository.ModelRepository;
import com.example.llm.repository.TeamRepository;
import com.example.llm.repository.UsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DashboardService {
    
    private final TeamRepository teamRepository;
    private final ModelRepository modelRepository;
    private final UsageRepository usageRepository;
    
    public DashboardResponse getDashboard() {
        List<Team> teams = teamRepository.findAll();
        
        // 기본 통계
        Integer totalTeams = teams.size();
        Long totalQuota = teams.stream().mapToLong(Team::getQuota).sum();
        Long totalUsage = teams.stream().mapToLong(Team::getUsage).sum();
        
        // 모델 통계
        Integer totalModels = (int) modelRepository.count();
        Integer activeModels = modelRepository.findByIsActiveTrue().size();
        
        // 비용 계산
        List<Usage> allUsages = usageRepository.findAll();
        Double totalCost = allUsages.stream().mapToDouble(Usage::getCost).sum();
        
        // 팀별 사용 요약
        List<DashboardResponse.TeamUsageSummary> teamUsageSummaries = teams.stream()
                .map(team -> {
                    Long teamId = team.getId();
                    List<Usage> teamUsages = usageRepository.findByTeamId(teamId);
                    
                    return DashboardResponse.TeamUsageSummary.builder()
                            .teamId(teamId)
                            .teamName(team.getName())
                            .quota(team.getQuota())
                            .usage(team.getUsage())
                            .usagePercentage(team.getQuota() > 0 
                                    ? (team.getUsage() * 100.0 / team.getQuota()) 
                                    : 0.0)
                            .modelCount(teamUsages.size())
                            .build();
                })
                .collect(Collectors.toList());
        
        return DashboardResponse.builder()
                .totalTeams(totalTeams)
                .totalQuota(totalQuota)
                .totalUsage(totalUsage)
                .totalCost(totalCost)
                .totalModels(totalModels)
                .activeModels(activeModels)
                .teamUsageSummaries(teamUsageSummaries)
                .build();
    }
}