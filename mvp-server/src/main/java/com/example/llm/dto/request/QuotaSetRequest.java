package com.example.llm.dto.request;

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
public class QuotaSetRequest {
    
    @NotNull(message = "할당량은 필수입니다")
    @Positive(message = "할당량은 양수여야 합니다")
    private Long quota;
}