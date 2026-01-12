package com.example.llm.service;

import com.example.llm.dto.request.QuotaSetRequest;
import com.example.llm.dto.request.TeamCreateRequest;
import com.example.llm.dto.request.TeamUpdateRequest;
import com.example.llm.dto.response.TeamResponse;
import com.example.llm.entity.Team;
import com.example.llm.exception.ErrorCode;
import com.example.llm.exception.ResourceNotFoundException;
import com.example.llm.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TeamService 단위 테스트")
class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private TeamService teamService;

    private Team testTeam;
    private TeamCreateRequest createRequest;
    private TeamUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        testTeam = Team.builder()
                .id(1L)
                .name("Test Team")
                .quota(10000L)
                .usage(5000L)
                .createdAt(now)
                .updatedAt(now)
                .build();

        createRequest = TeamCreateRequest.builder()
                .name("New Team")
                .quota(20000L)
                .build();

        updateRequest = TeamUpdateRequest.builder()
                .name("Updated Team")
                .quota(30000L)
                .build();
    }

    @Test
    @DisplayName("팀 생성 - 성공")
    void createTeam_Success() {
        // Given
        Team savedTeam = Team.builder()
                .id(1L)
                .name(createRequest.getName())
                .quota(createRequest.getQuota())
                .usage(0L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        when(teamRepository.save(any(Team.class))).thenReturn(savedTeam);

        // When
        TeamResponse response = teamService.createTeam(createRequest);

        // Then
        assertNotNull(response);
        assertEquals(createRequest.getName(), response.getName());
        assertEquals(createRequest.getQuota(), response.getQuota());
        assertEquals(0L, response.getUsage());
        verify(teamRepository, times(1)).save(any(Team.class));
    }

    @Test
    @DisplayName("전체 팀 목록 조회 - 성공")
    void getAllTeams_Success() {
        // Given
        Team team2 = Team.builder()
                .id(2L)
                .name("Team 2")
                .quota(15000L)
                .usage(3000L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<Team> teams = Arrays.asList(testTeam, team2);
        when(teamRepository.findAll()).thenReturn(teams);

        // When
        List<TeamResponse> responses = teamService.getAllTeams();

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("Test Team", responses.get(0).getName());
        assertEquals("Team 2", responses.get(1).getName());
        verify(teamRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("전체 팀 목록 조회 - 빈 목록")
    void getAllTeams_EmptyList() {
        // Given
        when(teamRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<TeamResponse> responses = teamService.getAllTeams();

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(teamRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("팀 ID로 조회 - 성공")
    void getTeamById_Success() {
        // Given
        when(teamRepository.findById(1L)).thenReturn(Optional.of(testTeam));

        // When
        TeamResponse response = teamService.getTeamById(1L);

        // Then
        assertNotNull(response);
        assertEquals(testTeam.getId(), response.getId());
        assertEquals(testTeam.getName(), response.getName());
        assertEquals(testTeam.getQuota(), response.getQuota());
        verify(teamRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("팀 ID로 조회 - 존재하지 않는 팀")
    void getTeamById_NotFound() {
        // Given
        when(teamRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> teamService.getTeamById(999L)
        );
        assertEquals(ErrorCode.TEAM_NOT_FOUND, exception.getErrorCode());
        verify(teamRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("팀 수정 - 성공")
    void updateTeam_Success() {
        // Given
        when(teamRepository.findById(1L)).thenReturn(Optional.of(testTeam));
        when(teamRepository.save(any(Team.class))).thenReturn(testTeam);

        // When
        TeamResponse response = teamService.updateTeam(1L, updateRequest);

        // Then
        assertNotNull(response);
        verify(teamRepository, times(1)).findById(1L);
        verify(teamRepository, times(1)).save(any(Team.class));
    }

    @Test
    @DisplayName("팀 수정 - 존재하지 않는 팀")
    void updateTeam_NotFound() {
        // Given
        when(teamRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> teamService.updateTeam(999L, updateRequest)
        );
        assertEquals(ErrorCode.TEAM_NOT_FOUND, exception.getErrorCode());
        verify(teamRepository, times(1)).findById(999L);
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    @DisplayName("팀 삭제 - 성공")
    void deleteTeam_Success() {
        // Given
        when(teamRepository.findById(1L)).thenReturn(Optional.of(testTeam));
        doNothing().when(teamRepository).delete(any(Team.class));

        // When
        teamService.deleteTeam(1L);

        // Then
        verify(teamRepository, times(1)).findById(1L);
        verify(teamRepository, times(1)).delete(any(Team.class));
    }

    @Test
    @DisplayName("팀 삭제 - 존재하지 않는 팀")
    void deleteTeam_NotFound() {
        // Given
        when(teamRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> teamService.deleteTeam(999L)
        );
        assertEquals(ErrorCode.TEAM_NOT_FOUND, exception.getErrorCode());
        verify(teamRepository, times(1)).findById(999L);
        verify(teamRepository, never()).delete(any(Team.class));
    }

    @Test
    @DisplayName("팀 할당량 설정 - 성공")
    void setQuota_Success() {
        // Given
        QuotaSetRequest quotaSetRequest = QuotaSetRequest.builder()
                .quota(50000L)
                .build();

        when(teamRepository.findById(1L)).thenReturn(Optional.of(testTeam));
        when(teamRepository.save(any(Team.class))).thenReturn(testTeam);

        // When
        TeamResponse response = teamService.setQuota(1L, quotaSetRequest);

        // Then
        assertNotNull(response);
        verify(teamRepository, times(1)).findById(1L);
        verify(teamRepository, times(1)).save(any(Team.class));
    }

    @Test
    @DisplayName("팀 할당량 설정 - 존재하지 않는 팀")
    void setQuota_NotFound() {
        // Given
        QuotaSetRequest quotaSetRequest = QuotaSetRequest.builder()
                .quota(50000L)
                .build();

        when(teamRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> teamService.setQuota(999L, quotaSetRequest)
        );
        assertEquals(ErrorCode.TEAM_NOT_FOUND, exception.getErrorCode());
        verify(teamRepository, times(1)).findById(999L);
        verify(teamRepository, never()).save(any(Team.class));
    }
}