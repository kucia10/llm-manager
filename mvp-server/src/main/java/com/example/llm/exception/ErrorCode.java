package com.example.llm.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    
    // Authentication errors
    INVALID_CREDENTIALS(401, "아이디 또는 비밀번호가 올바르지 않습니다"),
    UNAUTHORIZED(401, "인증이 필요합니다"),
    INVALID_TOKEN(401, "유효하지 않은 토큰입니다"),
    
    // Resource not found errors
    NOT_FOUND(404, "리소스를 찾을 수 없습니다"),
    USER_NOT_FOUND(404, "사용자를 찾을 수 없습니다"),
    TEAM_NOT_FOUND(404, "팀을 찾을 수 없습니다"),
    MODEL_NOT_FOUND(404, "모델을 찾을 수 없습니다"),
    
    // Business logic errors
    TEAM_NAME_DUPLICATE(400, "이미 존재하는 팀 이름입니다"),
    QUOTA_EXCEEDED(400, "할당량을 초과했습니다"),
    INVALID_INPUT(400, "잘못된 입력입니다"),
    
    // Server errors
    INTERNAL_SERVER_ERROR(500, "서버 오류가 발생했습니다");
    
    private final int status;
    private final String message;
}