package com.example.llm.service;

import com.example.llm.dto.request.LoginRequest;
import com.example.llm.dto.response.LoginResponse;
import com.example.llm.entity.User;
import com.example.llm.exception.ErrorCode;
import com.example.llm.exception.UnauthorizedException;
import com.example.llm.repository.UserRepository;
import com.example.llm.util.TokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 단위 테스트")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenUtil tokenUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("encodedPassword")
                .email("testuser@example.com")
                .role("ADMIN")
                .build();

        loginRequest = LoginRequest.builder()
                .username("testuser")
                .password("password123")
                .build();
    }

    @Test
    @DisplayName("로그인 - 기존 사용자가 존재하는 경우 토큰 반환")
    void login_ExistingUser_ReturnsToken() {
        // Given
        when(userRepository.findByUsername(loginRequest.getUsername()))
                .thenReturn(Optional.of(testUser));
        when(tokenUtil.generateToken(testUser.getUsername(), testUser.getRole()))
                .thenReturn("test-token");

        // When
        LoginResponse response = authService.login(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals("test-token", response.getToken());
        assertEquals(testUser.getUsername(), response.getUsername());
        assertEquals(testUser.getRole(), response.getRole());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("로그인 - 신규 사용자인 경우 사용자 생성 및 토큰 반환")
    void login_NewUser_CreatesUserAndReturnsToken() {
        // Given
        when(userRepository.findByUsername(loginRequest.getUsername()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(loginRequest.getPassword()))
                .thenReturn("encodedPassword");
        when(userRepository.save(any(User.class)))
                .thenReturn(testUser);
        when(tokenUtil.generateToken(testUser.getUsername(), testUser.getRole()))
                .thenReturn("test-token");

        // When
        LoginResponse response = authService.login(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals("test-token", response.getToken());
        assertEquals("ADMIN", response.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("현재 사용자 정보 조회 - 유효한 토큰인 경우 사용자 정보 반환")
    void getCurrentUser_ValidToken_ReturnsUser() {
        // Given
        String token = "valid-token";
        when(tokenUtil.validateToken(token)).thenReturn(true);
        when(tokenUtil.getUsernameFromToken(token)).thenReturn("testuser");
        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(testUser));

        // When
        LoginResponse response = authService.getCurrentUser(token);

        // Then
        assertNotNull(response);
        assertEquals(testUser.getUsername(), response.getUsername());
        assertEquals(testUser.getRole(), response.getRole());
    }

    @Test
    @DisplayName("현재 사용자 정보 조회 - 유효하지 않은 토큰인 경우 예외 발생")
    void getCurrentUser_InvalidToken_ThrowsUnauthorizedException() {
        // Given
        String invalidToken = "invalid-token";
        when(tokenUtil.validateToken(invalidToken)).thenReturn(false);

        // When & Then
        assertThrows(UnauthorizedException.class, () -> 
                authService.getCurrentUser(invalidToken)
        );
    }

    @Test
    @DisplayName("현재 사용자 정보 조회 - 존재하지 않는 사용자인 경우 예외 발생")
    void getCurrentUser_UserNotFound_ThrowsUnauthorizedException() {
        // Given
        String token = "valid-token";
        when(tokenUtil.validateToken(token)).thenReturn(true);
        when(tokenUtil.getUsernameFromToken(token)).thenReturn("nonexistent");
        when(userRepository.findByUsername("nonexistent"))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(UnauthorizedException.class, () -> 
                authService.getCurrentUser(token)
        );
    }

    @Test
    @DisplayName("토큰 검증 - 유효한 토큰인 경우 정상 완료")
    void validateToken_ValidToken_NoException() {
        // Given
        String validToken = "valid-token";
        when(tokenUtil.validateToken(validToken)).thenReturn(true);

        // When & Then
        assertDoesNotThrow(() -> authService.validateToken(validToken));
    }

    @Test
    @DisplayName("토큰 검증 - 유효하지 않은 토큰인 경우 예외 발생")
    void validateToken_InvalidToken_ThrowsUnauthorizedException() {
        // Given
        String invalidToken = "invalid-token";
        when(tokenUtil.validateToken(invalidToken)).thenReturn(false);

        // When & Then
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, 
                () -> authService.validateToken(invalidToken)
        );
        assertEquals(ErrorCode.INVALID_TOKEN, exception.getErrorCode());
    }

    @Test
    @DisplayName("로그아웃 - 정상 처리 (JWT는 stateless)")
    void logout_ValidToken_NoException() {
        // Given
        String token = "valid-token";

        // When & Then
        assertDoesNotThrow(() -> authService.logout(token));
    }

    @Test
    @DisplayName("토큰에서 사용자명 추출")
    void getUsernameFromToken_ReturnsUsername() {
        // Given
        String token = "valid-token";
        String expectedUsername = "testuser";
        when(tokenUtil.getUsernameFromToken(token)).thenReturn(expectedUsername);

        // When
        String actualUsername = authService.getUsernameFromToken(token);

        // Then
        assertEquals(expectedUsername, actualUsername);
    }
}