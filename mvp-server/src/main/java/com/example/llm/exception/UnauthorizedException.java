package com.example.llm.exception;

public class UnauthorizedException extends BusinessException {
    
    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }
    
    public UnauthorizedException(String message) {
        super(ErrorCode.UNAUTHORIZED, message);
    }
}