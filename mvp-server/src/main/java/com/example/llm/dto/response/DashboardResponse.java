package com.example.llm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    
    private Integer totalTeams;
    
    private Long totalQuota;
    
    private Long totalUsage;
    
    private Double totalCost;
    
    private Integer totalModels;
    
    private Integer activeModels;
    
    private List<TeamUsageSummary> teamUsageSummaries;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamUsageSummary {
        private Long teamId;
        private String teamName;
        private Long quota;
        private Long usage;
        private Double usagePercentage;
        private Integer modelCount;
    }
}