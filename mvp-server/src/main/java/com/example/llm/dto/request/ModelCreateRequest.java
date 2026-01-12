package com.example.llm.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelCreateRequest {
    
    @NotBlank(message = "모델 이름은 필수입니다")
    private String name;
    
    @NotBlank(message = "제공자는 필수입니다")
    private String provider;
    
    @NotNull(message = "토큰당 비용은 필수입니다")
    @Positive(message = "토큰당 비용은 양수여야 합니다")
    private Double costPerToken;
    
    private String apiKey;
    
    private Boolean isActive;
}