package com.example.llm.service;

import com.example.llm.dto.response.DashboardResponse;
import com.example.llm.entity.LLMModel;
import com.example.llm.entity.Team;
import com.example.llm.entity.Usage;
import com.example.llm.repository.ModelRepository;
import com.example.llm.repository.TeamRepository;
import com.example.llm.repository.UsageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DashboardService 단위 테스트")
class DashboardServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private ModelRepository modelRepository;

    @Mock
    private UsageRepository usageRepository;

    @InjectMocks
    private DashboardService dashboardService;

    private Team testTeam1;
    private Team testTeam2;
    private LLMModel testModel1;
    private LLMModel testModel2;
    private Usage usage1;
    private Usage usage2;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        
        testTeam1 = Team.builder()
                .id(1L)
                .name("AI Research Team")
                .quota(10000L)
                .usage(5000L)
                .createdAt(now)
                .updatedAt(now)
                .build();

        testTeam2 = Team.builder()
                .id(2L)
                .name("Data Science Team")
                .quota(20000L)
                .usage(10000L)
                .createdAt(now)
                .updatedAt(now)
                .build();

        testModel1 = LLMModel.builder()
                .id(1L)
                .name("GPT-4")
                .provider("OpenAI")
                .costPerToken(0.0001)
                .isActive(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        testModel2 = LLMModel.builder()
                .id(2L)
                .name("Claude-2")
                .provider("Anthropic")
                .costPerToken(0.00008)
                .isActive(false)
                .createdAt(now)
                .updatedAt(now)
                .build();

        usage1 = Usage.builder()
                .id(1L)
                .team(testTeam1)
                .model(testModel1)
                .tokens(1000)
                .cost(0.1)
                .usedAt(now)
                .build();

        usage2 = Usage.builder()
                .id(2L)
                .team(testTeam2)
                .model(testModel1)
                .tokens(2000)
                .cost(0.2)
                .usedAt(now)
                .build();
    }

    @Test
    @DisplayName("대시보드 데이터 조회 - 성공 (데이터 존재)")
    void getDashboard_Success() {
        // Given
        when(teamRepository.findAll()).thenReturn(Arrays.asList(testTeam1, testTeam2));
        when(modelRepository.count()).thenReturn(2L);
        when(modelRepository.findByIsActiveTrue()).thenReturn(Arrays.asList(testModel1));
        when(usageRepository.findAll()).thenReturn(Arrays.asList(usage1, usage2));
        when(usageRepository.findByTeamId(1L)).thenReturn(Arrays.asList(usage1));
        when(usageRepository.findByTeamId(2L)).thenReturn(Arrays.asList(usage2));

        // When
        DashboardResponse response = dashboardService.getDashboard();

        // Then
        assertNotNull(response);
        assertEquals(2, response.getTotalTeams());
        assertEquals(30000L, response.getTotalQuota());
        assertEquals(15000L, response.getTotalUsage());
        assertEquals(2, response.getTotalModels());
        assertEquals(1, response.getActiveModels());
        assertEquals(0.3, response.getTotalCost(), 0.001);
        assertNotNull(response.getTeamUsageSummaries());
        assertEquals(2, response.getTeamUsageSummaries().size());
    }

    @Test
    @DisplayName("대시보드 데이터 조회 - 빈 데이터")
    void getDashboard_EmptyData() {
        // Given
        when(teamRepository.findAll()).thenReturn(Collections.emptyList());
        when(modelRepository.count()).thenReturn(0L);
        when(modelRepository.findByIsActiveTrue()).thenReturn(Collections.emptyList());
        when(usageRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        DashboardResponse response = dashboardService.getDashboard();

        // Then
        assertNotNull(response);
        assertEquals(0, response.getTotalTeams());
        assertEquals(0L, response.getTotalQuota());
        assertEquals(0L, response.getTotalUsage());
        assertEquals(0, response.getTotalModels());
        assertEquals(0, response.getActiveModels());
        assertEquals(0.0, response.getTotalCost(), 0.001);
        assertTrue(response.getTeamUsageSummaries().isEmpty());
    }

    @Test
    @DisplayName("대시보드 데이터 조회 - 팀별 사용량 비율 계산")
    void getDashboard_CalculatesUsagePercentage() {
        // Given
        when(teamRepository.findAll()).thenReturn(Arrays.asList(testTeam1));
        when(modelRepository.count()).thenReturn(1L);
        when(modelRepository.findByIsActiveTrue()).thenReturn(Arrays.asList(testModel1));
        when(usageRepository.findAll()).thenReturn(Arrays.asList(usage1));
        when(usageRepository.findByTeamId(1L)).thenReturn(Arrays.asList(usage1));

        // When
        DashboardResponse response = dashboardService.getDashboard();

        // Then
        assertNotNull(response);
        assertEquals(1, response.getTeamUsageSummaries().size());
        
        DashboardResponse.TeamUsageSummary summary = response.getTeamUsageSummaries().get(0);
        assertEquals(1L, summary.getTeamId());
        assertEquals("AI Research Team", summary.getTeamName());
        assertEquals(10000L, summary.getQuota());
        assertEquals(5000L, summary.getUsage());
        assertEquals(50.0, summary.getUsagePercentage(), 0.001);
    }

    @Test
    @DisplayName("대시보드 데이터 조회 - 할당량이 0인 경우")
    void getDashboard_QuotaZero() {
        // Given
        testTeam1.setQuota(0L);
        when(teamRepository.findAll()).thenReturn(Arrays.asList(testTeam1));
        when(modelRepository.count()).thenReturn(1L);
        when(modelRepository.findByIsActiveTrue()).thenReturn(Arrays.asList(testModel1));
        when(usageRepository.findAll()).thenReturn(Arrays.asList(usage1));
        when(usageRepository.findByTeamId(1L)).thenReturn(Arrays.asList(usage1));

        // When
        DashboardResponse response = dashboardService.getDashboard();

        // Then
        assertNotNull(response);
        assertEquals(1, response.getTeamUsageSummaries().size());
        
        DashboardResponse.TeamUsageSummary summary = response.getTeamUsageSummaries().get(0);
        assertEquals(0L, summary.getQuota());
        assertEquals(5000L, summary.getUsage());
        assertEquals(0.0, summary.getUsagePercentage(), 0.001); // quota가 0이면 0%로 계산
    }

    @Test
    @DisplayName("대시보드 데이터 조회 - 총 비용 계산")
    void getDashboard_CalculatesTotalCost() {
        // Given
        Usage usage3 = Usage.builder()
                .id(3L)
                .team(testTeam1)
                .model(testModel2)
                .tokens(500)
                .cost(0.05)
                .usedAt(LocalDateTime.now())
                .build();

        when(teamRepository.findAll()).thenReturn(Arrays.asList(testTeam1));
        when(modelRepository.count()).thenReturn(2L);
        when(modelRepository.findByIsActiveTrue()).thenReturn(Arrays.asList(testModel1, testModel2));
        when(usageRepository.findAll()).thenReturn(Arrays.asList(usage1, usage3));
        when(usageRepository.findByTeamId(1L)).thenReturn(Arrays.asList(usage1, usage3));

        // When
        DashboardResponse response = dashboardService.getDashboard();

        // Then
        assertNotNull(response);
        assertEquals(0.15, response.getTotalCost(), 0.001); // 0.1 + 0.05
    }

    @Test
    @DisplayName("대시보드 데이터 조회 - 팀별 모델 사용 수 계산")
    void getDashboard_CalculatesModelCountPerTeam() {
        // Given
        when(teamRepository.findAll()).thenReturn(Arrays.asList(testTeam1, testTeam2));
        when(modelRepository.count()).thenReturn(2L);
        when(modelRepository.findByIsActiveTrue()).thenReturn(Arrays.asList(testModel1, testModel2));
        when(usageRepository.findAll()).thenReturn(Arrays.asList(usage1, usage2));
        when(usageRepository.findByTeamId(1L)).thenReturn(Arrays.asList(usage1));
        when(usageRepository.findByTeamId(2L)).thenReturn(Arrays.asList(usage2));

        // When
        DashboardResponse response = dashboardService.getDashboard();

        // Then
        assertNotNull(response);
        assertEquals(2, response.getTeamUsageSummaries().size());
        
        DashboardResponse.TeamUsageSummary summary1 = response.getTeamUsageSummaries().get(0);
        assertEquals(1, summary1.getModelCount());
        
        DashboardResponse.TeamUsageSummary summary2 = response.getTeamUsageSummaries().get(1);
        assertEquals(1, summary2.getModelCount());
    }
}