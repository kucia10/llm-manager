package com.example.llm.controller;

import com.example.llm.dto.request.QuotaSetRequest;
import com.example.llm.dto.request.TeamCreateRequest;
import com.example.llm.dto.request.TeamUpdateRequest;
import com.example.llm.dto.response.TeamResponse;
import com.example.llm.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
@Tag(name = "팀 관리", description = "팀 CRUD 및 할당량 설정 API")
public class TeamController {
    
    private final TeamService teamService;
    
    @PostMapping
    @Operation(summary = "팀 생성", description = "새로운 팀을 생성합니다")
    public ResponseEntity<TeamResponse> createTeam(@Valid @RequestBody TeamCreateRequest request) {
        TeamResponse response = teamService.createTeam(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @Operation(summary = "팀 목록 조회", description = "모든 팀 목록을 조회합니다")
    public ResponseEntity<List<TeamResponse>> getAllTeams() {
        List<TeamResponse> response = teamService.getAllTeams();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "팀 상세 조회", description = "특정 팀의 상세 정보를 조회합니다")
    public ResponseEntity<TeamResponse> getTeamById(@PathVariable Long id) {
        TeamResponse response = teamService.getTeamById(id);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "팀 수정", description = "팀 정보를 수정합니다")
    public ResponseEntity<TeamResponse> updateTeam(
            @PathVariable Long id,
            @Valid @RequestBody TeamUpdateRequest request) {
        TeamResponse response = teamService.updateTeam(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "팀 삭제", description = "팀을 삭제합니다")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.ok().build();
    }
    
    @PatchMapping("/{id}/quota")
    @Operation(summary = "할당량 설정", description = "팀의 할당량을 설정합니다")
    public ResponseEntity<TeamResponse> setQuota(
            @PathVariable Long id,
            @Valid @RequestBody QuotaSetRequest request) {
        TeamResponse response = teamService.setQuota(id, request);
        return ResponseEntity.ok(response);
    }
}