package com.example.llm.controller;

import com.example.llm.dto.response.DashboardResponse;
import com.example.llm.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "대시보드", description = "대시보드 데이터 조회 API")
public class DashboardController {
    
    private final DashboardService dashboardService;
    
    @GetMapping
    @Operation(summary = "대시보드 데이터 조회", description = "전체 대시보드 통계 데이터를 조회합니다")
    public ResponseEntity<DashboardResponse> getDashboard() {
        DashboardResponse response = dashboardService.getDashboard();
        return ResponseEntity.ok(response);
    }
}