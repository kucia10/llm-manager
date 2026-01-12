package com.example.llm.controller;

import com.example.llm.dto.request.LoginRequest;
import com.example.llm.dto.response.LoginResponse;
import com.example.llm.entity.User;
import com.example.llm.repository.UserRepository;
import com.example.llm.util.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("AuthController 통합 테스트")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("로그인 - 신규 사용자인 경우 사용자 생성 및 토큰 반환")
    void login_NewUser_ReturnsToken() throws Exception {
        // Given
        LoginRequest request = LoginRequest.builder()
                .username("newuser")
                .password("password123")
                .build();

        // When & Then
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andReturn();

        // Verify user was created in database
        User user = userRepository.findByUsername("newuser").orElse(null);
        assertNotNull(user);
        assertEquals("newuser", user.getUsername());
    }

    @Test
    @DisplayName("로그인 - 기존 사용자인 경우 기존 사용자 정보로 토큰 반환")
    void login_ExistingUser_ReturnsToken() throws Exception {
        // Given
        User existingUser = User.builder()
                .username("existinguser")
                .password(passwordEncoder.encode("password123"))
                .email("existinguser@example.com")
                .role("USER")
                .build();
        userRepository.save(existingUser);

        LoginRequest request = LoginRequest.builder()
                .username("existinguser")
                .password("password123")
                .build();

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("existinguser"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @DisplayName("로그인 - 아이디가 비어있는 경우 400 에러")
    void login_EmptyUsername_Returns400() throws Exception {
        // Given
        LoginRequest request = LoginRequest.builder()
                .username("")
                .password("password123")
                .build();

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인 - 비밀번호가 비어있는 경우 400 에러")
    void login_EmptyPassword_Returns400() throws Exception {
        // Given
        LoginRequest request = LoginRequest.builder()
                .username("testuser")
                .password("")
                .build();

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그아웃 - 정상 처리")
    void logout_ValidToken_Returns200() throws Exception {
        // Given
        String token = tokenUtil.generateToken("testuser", "ADMIN");

        // When & Then
        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("현재 사용자 정보 조회 - 유효한 토큰인 경우 사용자 정보 반환")
    void getCurrentUser_ValidToken_ReturnsUser() throws Exception {
        // Given
        User user = User.builder()
                .username("testuser")
                .password(passwordEncoder.encode("password"))
                .email("testuser@example.com")
                .role("ADMIN")
                .build();
        userRepository.save(user);

        String token = tokenUtil.generateToken("testuser", "ADMIN");

        // When & Then
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @DisplayName("토큰 검증 - 유효한 토큰인 경우 200 반환")
    void validateToken_ValidToken_Returns200() throws Exception {
        // Given
        String token = tokenUtil.generateToken("testuser", "ADMIN");

        // When & Then
        mockMvc.perform(get("/api/auth/validate")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("토큰 검증 - 유효하지 않은 토큰인 경우 401 반환")
    void validateToken_InvalidToken_Returns401() throws Exception {
        // Given
        String invalidToken = "invalid.token.here";

        // When & Then
        mockMvc.perform(get("/api/auth/validate")
                        .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("현재 사용자 정보 조회 - 존재하지 않는 사용자인 경우 401 반환")
    void getCurrentUser_UserNotFound_Returns401() throws Exception {
        // Given
        String token = tokenUtil.generateToken("nonexistent", "ADMIN");

        // When & Then
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("로그인 - JSON 형식이 아닌 경우 400 에러")
    void login_InvalidJson_Returns400() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isBadRequest());
    }
}