package com.example.llm.controller;

import com.example.llm.entity.LLMModel;
import com.example.llm.entity.Team;
import com.example.llm.entity.Usage;
import com.example.llm.repository.ModelRepository;
import com.example.llm.repository.TeamRepository;
import com.example.llm.repository.UsageRepository;
import com.example.llm.util.TokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("DashboardController 통합 테스트")
class DashboardControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private UsageRepository usageRepository;

    @Autowired
    private TokenUtil tokenUtil;

    private String authToken;

    @BeforeEach
    void setUp() {
        teamRepository.deleteAll();
        modelRepository.deleteAll();
        usageRepository.deleteAll();
        authToken = "Bearer " + tokenUtil.generateToken("testuser", "ADMIN");
    }

    @Test
    @DisplayName("대시보드 데이터 조회 - 성공 (데이터 존재)")
    void getDashboard_Success() throws Exception {
        // Given
        LocalDateTime now = LocalDateTime.now();
        
        Team team1 = Team.builder()
                .name("AI Research Team")
                .quota(10000L)
                .usage(5000L)
                .createdAt(now)
                .updatedAt(now)
                .build();
        
        Team team2 = Team.builder()
                .name("Data Science Team")
                .quota(20000L)
                .usage(10000L)
                .createdAt(now)
                .updatedAt(now)
                .build();
        
        teamRepository.save(team1);
        teamRepository.save(team2);
        
        LLMModel model1 = LLMModel.builder()
                .name("GPT-4")
                .provider("OpenAI")
                .costPerToken(0.0001)
                .isActive(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
        
        LLMModel model2 = LLMModel.builder()
                .name("Claude-2")
                .provider("Anthropic")
                .costPerToken(0.00008)
                .isActive(false)
                .createdAt(now)
                .updatedAt(now)
                .build();
        
        modelRepository.save(model1);
        modelRepository.save(model2);
        
        Usage usage1 = Usage.builder()
                .team(team1)
                .model(model1)
                .tokens(1000)
                .cost(0.1)
                .usedAt(now)
                .build();
        
        Usage usage2 = Usage.builder()
                .team(team2)
                .model(model1)
                .tokens(2000)
                .cost(0.2)
                .usedAt(now)
                .build();
        
        usageRepository.save(usage1);
        usageRepository.save(usage2);

        // When & Then
        mockMvc.perform(get("/api/dashboard")
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTeams").value(2))
                .andExpect(jsonPath("$.totalQuota").value(30000))
                .andExpect(jsonPath("$.totalUsage").value(15000))
                .andExpect(jsonPath("$.totalModels").value(2))
                .andExpect(jsonPath("$.activeModels").value(1))
                .andExpect(jsonPath("$.totalCost").value(0.3))
                .andExpect(jsonPath("$.teamUsageSummaries").isArray())
                .andExpect(jsonPath("$.teamUsageSummaries.length()").value(2));
    }

    @Test
    @DisplayName("대시보드 데이터 조회 - 빈 데이터")
    void getDashboard_EmptyData() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/dashboard")
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTeams").value(0))
                .andExpect(jsonPath("$.totalQuota").value(0))
                .andExpect(jsonPath("$.totalUsage").value(0))
                .andExpect(jsonPath("$.totalModels").value(0))
                .andExpect(jsonPath("$.activeModels").value(0))
                .andExpect(jsonPath("$.totalCost").value(0.0))
                .andExpect(jsonPath("$.teamUsageSummaries").isArray())
                .andExpect(jsonPath("$.teamUsageSummaries.length()").value(0));
    }

    @Test
    @DisplayName("대시보드 데이터 조회 - 팀만 존재하는 경우")
    void getDashboard_TeamsOnly() throws Exception {
        // Given
        LocalDateTime now = LocalDateTime.now();
        
        Team team = Team.builder()
                .name("AI Research Team")
                .quota(10000L)
                .usage(5000L)
                .createdAt(now)
                .updatedAt(now)
                .build();
        
        teamRepository.save(team);

        // When & Then
        mockMvc.perform(get("/api/dashboard")
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTeams").value(1))
                .andExpect(jsonPath("$.totalQuota").value(10000))
                .andExpect(jsonPath("$.totalUsage").value(5000))
                .andExpect(jsonPath("$.totalModels").value(0))
                .andExpect(jsonPath("$.activeModels").value(0))
                .andExpect(jsonPath("$.totalCost").value(0.0))
                .andExpect(jsonPath("$.teamUsageSummaries.length()").value(1))
                .andExpect(jsonPath("$.teamUsageSummaries[0].teamName").value("AI Research Team"))
                .andExpect(jsonPath("$.teamUsageSummaries[0].usagePercentage").value(50.0));
    }

    @Test
    @DisplayName("대시보드 데이터 조회 - 모델만 존재하는 경우")
    void getDashboard_ModelsOnly() throws Exception {
        // Given
        LocalDateTime now = LocalDateTime.now();
        
        LLMModel model1 = LLMModel.builder()
                .name("GPT-4")
                .provider("OpenAI")
                .costPerToken(0.0001)
                .isActive(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
        
        LLMModel model2 = LLMModel.builder()
                .name("Claude-2")
                .provider("Anthropic")
                .costPerToken(0.00008)
                .isActive(false)
                .createdAt(now)
                .updatedAt(now)
                .build();
        
        modelRepository.save(model1);
        modelRepository.save(model2);

        // When & Then
        mockMvc.perform(get("/api/dashboard")
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTeams").value(0))
                .andExpect(jsonPath("$.totalQuota").value(0))
                .andExpect(jsonPath("$.totalUsage").value(0))
                .andExpect(jsonPath("$.totalModels").value(2))
                .andExpect(jsonPath("$.activeModels").value(1))
                .andExpect(jsonPath("$.totalCost").value(0.0))
                .andExpect(jsonPath("$.teamUsageSummaries.length()").value(0));
    }

    @Test
    @DisplayName("대시보드 데이터 조회 - 할당량이 0인 팀")
    void getDashboard_QuotaZero() throws Exception {
        // Given
        LocalDateTime now = LocalDateTime.now();
        
        Team team = Team.builder()
                .name("AI Research Team")
                .quota(0L)
                .usage(5000L)
                .createdAt(now)
                .updatedAt(now)
                .build();
        
        teamRepository.save(team);

        // When & Then
        mockMvc.perform(get("/api/dashboard")
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamUsageSummaries.length()").value(1))
                .andExpect(jsonPath("$.teamUsageSummaries[0].quota").value(0))
                .andExpect(jsonPath("$.teamUsageSummaries[0].usage").value(5000))
                .andExpect(jsonPath("$.teamUsageSummaries[0].usagePercentage").value(0.0));
    }

    @Test
    @DisplayName("대시보드 데이터 조회 - 팀별 사용 요약 계산")
    void getDashboard_TeamUsageSummaries() throws Exception {
        // Given
        LocalDateTime now = LocalDateTime.now();
        
        Team team = Team.builder()
                .name("AI Research Team")
                .quota(10000L)
                .usage(5000L)
                .createdAt(now)
                .updatedAt(now)
                .build();
        
        teamRepository.save(team);
        
        LLMModel model = LLMModel.builder()
                .name("GPT-4")
                .provider("OpenAI")
                .costPerToken(0.0001)
                .isActive(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
        
        modelRepository.save(model);
        
        Usage usage = Usage.builder()
                .team(team)
                .model(model)
                .tokens(1000)
                .cost(0.1)
                .usedAt(now)
                .build();
        
        usageRepository.save(usage);

        // When & Then
        mockMvc.perform(get("/api/dashboard")
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamUsageSummaries[0].teamName").value("AI Research Team"))
                .andExpect(jsonPath("$.teamUsageSummaries[0].quota").value(10000))
                .andExpect(jsonPath("$.teamUsageSummaries[0].usage").value(5000))
                .andExpect(jsonPath("$.teamUsageSummaries[0].usagePercentage").value(50.0))
                .andExpect(jsonPath("$.teamUsageSummaries[0].modelCount").value(1));
    }

    @Test
    @DisplayName("대시보드 데이터 조회 - 인증 없이 요청")
    void getDashboard_NoAuth_Returns403() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isForbidden());
    }
}