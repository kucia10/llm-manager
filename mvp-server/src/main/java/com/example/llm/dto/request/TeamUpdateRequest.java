package com.example.llm.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamUpdateRequest {
    
    @NotBlank(message = "팀 이름은 필수입니다")
    private String name;
    
    @NotNull(message = "할당량은 필수입니다")
    private Long quota;
}