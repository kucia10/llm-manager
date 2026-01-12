package com.example.llm.controller;

import com.example.llm.dto.request.LoginRequest;
import com.example.llm.dto.response.LoginResponse;
import com.example.llm.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "인증", description = "인증 관련 API")
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "사용자 로그인 (Mock)")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "사용자 로그아웃")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        authService.logout(token);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/me")
    @Operation(summary = "현재 사용자 정보", description = "현재 로그인된 사용자 정보 조회")
    public ResponseEntity<LoginResponse> getCurrentUser(@RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        LoginResponse response = authService.getCurrentUser(token);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/validate")
    @Operation(summary = "토큰 검증", description = "JWT 토큰 유효성 검증")
    public ResponseEntity<Void> validateToken(@RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        authService.validateToken(token);
        return ResponseEntity.ok().build();
    }
}