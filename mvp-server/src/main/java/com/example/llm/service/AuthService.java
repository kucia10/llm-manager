package com.example.llm.service;

import com.example.llm.dto.request.LoginRequest;
import com.example.llm.dto.response.LoginResponse;
import com.example.llm.entity.User;
import com.example.llm.exception.ErrorCode;
import com.example.llm.exception.UnauthorizedException;
import com.example.llm.repository.UserRepository;
import com.example.llm.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final TokenUtil tokenUtil;
    private final PasswordEncoder passwordEncoder;
    
    public LoginResponse login(LoginRequest request) {
        // 개발용: 모든 로그인 요청을 허용 (Mock)
        // 실제 환경에서는 사용자 인증 로직이 필요
        
        // 테스트용 사용자 생성 또는 조회
        User user = userRepository.findByUsername(request.getUsername())
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .username(request.getUsername())
                            .password(passwordEncoder.encode(request.getPassword()))
                            .email(request.getUsername() + "@example.com")
                            .role("ADMIN")
                            .build();
                    return userRepository.save(newUser);
                });
        
        // 토큰 생성
        String token = tokenUtil.generateToken(user.getUsername(), user.getRole());
        
        return LoginResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }
    
    public LoginResponse getCurrentUser(String token) {
        validateToken(token);
        String username = getUsernameFromToken(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.UNAUTHORIZED));
        
        return LoginResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }
    
    public void logout(String token) {
        // JWT는 stateless하므로 별도의 로그아웃 처리 불필요
        // 프론트엔드에서 토큰 삭제로 처리
    }
    
    public void validateToken(String token) {
        if (!tokenUtil.validateToken(token)) {
            throw new UnauthorizedException(ErrorCode.INVALID_TOKEN);
        }
    }
    
    public String getUsernameFromToken(String token) {
        return tokenUtil.getUsernameFromToken(token);
    }
}