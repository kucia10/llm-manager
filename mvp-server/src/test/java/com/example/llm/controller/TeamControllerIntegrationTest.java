package com.example.llm.controller;

import com.example.llm.dto.request.QuotaSetRequest;
import com.example.llm.dto.request.TeamCreateRequest;
import com.example.llm.dto.request.TeamUpdateRequest;
import com.example.llm.dto.response.TeamResponse;
import com.example.llm.entity.Team;
import com.example.llm.repository.TeamRepository;
import com.example.llm.util.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("TeamController 통합 테스트")
class TeamControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TokenUtil tokenUtil;

    private String authToken;

    @BeforeEach
    void setUp() {
        teamRepository.deleteAll();
        authToken = "Bearer " + tokenUtil.generateToken("testuser", "ADMIN");
    }

    @Test
    @DisplayName("팀 생성 - 성공")
    void createTeam_Success() throws Exception {
        // Given
        TeamCreateRequest request = TeamCreateRequest.builder()
                .name("AI Research Team")
                .quota(10000L)
                .build();

        // When & Then
        mockMvc.perform(post("/api/teams")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("AI Research Team"))
                .andExpect(jsonPath("$.quota").value(10000))
                .andExpect(jsonPath("$.usage").value(0));

        // Verify database
        Team team = teamRepository.findAll().get(0);
        assertEquals("AI Research Team", team.getName());
        assertEquals(10000L, team.getQuota());
    }

    @Test
    @DisplayName("팀 생성 - 이름이 비어있는 경우 400 에러")
    void createTeam_EmptyName_Returns400() throws Exception {
        // Given
        TeamCreateRequest request = TeamCreateRequest.builder()
                .name("")
                .quota(10000L)
                .build();

        // When & Then
        mockMvc.perform(post("/api/teams")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("팀 생성 - 할당량이 null인 경우 400 에러")
    void createTeam_NullQuota_Returns400() throws Exception {
        // Given
        TeamCreateRequest request = TeamCreateRequest.builder()
                .name("Test Team")
                .quota(null)
                .build();

        // When & Then
        mockMvc.perform(post("/api/teams")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("전체 팀 목록 조회 - 성공")
    void getAllTeams_Success() throws Exception {
        // Given
        Team team1 = Team.builder().name("Team 1").quota(10000L).usage(0L).build();
        Team team2 = Team.builder().name("Team 2").quota(20000L).usage(0L).build();
        teamRepository.save(team1);
        teamRepository.save(team2);

        // When & Then
        mockMvc.perform(get("/api/teams")
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[1].name").exists());
    }

    @Test
    @DisplayName("전체 팀 목록 조회 - 빈 목록")
    void getAllTeams_EmptyList() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/teams")
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    @DisplayName("팀 ID로 조회 - 성공")
    void getTeamById_Success() throws Exception {
        // Given
        Team team = Team.builder().name("Test Team").quota(10000L).usage(0L).build();
        Team savedTeam = teamRepository.save(team);

        // When & Then
        mockMvc.perform(get("/api/teams/" + savedTeam.getId())
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedTeam.getId()))
                .andExpect(jsonPath("$.name").value("Test Team"))
                .andExpect(jsonPath("$.quota").value(10000));
    }

    @Test
    @DisplayName("팀 ID로 조회 - 존재하지 않는 팀")
    void getTeamById_NotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/teams/999")
                        .header("Authorization", authToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("팀 수정 - 성공")
    void updateTeam_Success() throws Exception {
        // Given
        Team team = Team.builder().name("Old Name").quota(10000L).usage(0L).build();
        Team savedTeam = teamRepository.save(team);

        TeamUpdateRequest request = TeamUpdateRequest.builder()
                .name("New Name")
                .quota(20000L)
                .build();

        // When & Then
        mockMvc.perform(put("/api/teams/" + savedTeam.getId())
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.quota").value(20000));

        // Verify database
        Team updatedTeam = teamRepository.findById(savedTeam.getId()).orElse(null);
        assertNotNull(updatedTeam);
        assertEquals("New Name", updatedTeam.getName());
    }

    @Test
    @DisplayName("팀 수정 - 존재하지 않는 팀")
    void updateTeam_NotFound() throws Exception {
        // Given
        TeamUpdateRequest request = TeamUpdateRequest.builder()
                .name("New Name")
                .quota(20000L)
                .build();

        // When & Then
        mockMvc.perform(put("/api/teams/999")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("팀 삭제 - 성공")
    void deleteTeam_Success() throws Exception {
        // Given
        Team team = Team.builder().name("Test Team").quota(10000L).usage(0L).build();
        Team savedTeam = teamRepository.save(team);

        // When & Then
        mockMvc.perform(delete("/api/teams/" + savedTeam.getId())
                        .header("Authorization", authToken))
                .andExpect(status().isOk());

        // Verify deletion
        assertFalse(teamRepository.existsById(savedTeam.getId()));
    }

    @Test
    @DisplayName("팀 삭제 - 존재하지 않는 팀")
    void deleteTeam_NotFound() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/teams/999")
                        .header("Authorization", authToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("팀 할당량 설정 - 성공")
    void setQuota_Success() throws Exception {
        // Given
        Team team = Team.builder().name("Test Team").quota(10000L).usage(0L).build();
        Team savedTeam = teamRepository.save(team);

        QuotaSetRequest request = QuotaSetRequest.builder()
                .quota(50000L)
                .build();

        // When & Then
        mockMvc.perform(patch("/api/teams/" + savedTeam.getId() + "/quota")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quota").value(50000));

        // Verify database
        Team updatedTeam = teamRepository.findById(savedTeam.getId()).orElse(null);
        assertNotNull(updatedTeam);
        assertEquals(50000L, updatedTeam.getQuota());
    }

    @Test
    @DisplayName("팀 할당량 설정 - 존재하지 않는 팀")
    void setQuota_NotFound() throws Exception {
        // Given
        QuotaSetRequest request = QuotaSetRequest.builder()
                .quota(50000L)
                .build();

        // When & Then
        mockMvc.perform(patch("/api/teams/999/quota")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("팀 ID가 유효하지 않은 경우 400 에러")
    void invalidTeamId_Returns400() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/teams/invalid")
                        .header("Authorization", authToken))
                .andExpect(status().isBadRequest());
    }
}