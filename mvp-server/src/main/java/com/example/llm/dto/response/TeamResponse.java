package com.example.llm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamResponse {
    
    private Long id;
    
    private String name;
    
    private Long quota;
    
    private Long usage;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}