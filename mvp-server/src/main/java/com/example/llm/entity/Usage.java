package com.example.llm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "usage")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", nullable = false)
    private LLMModel model;
    
    @Column(nullable = false)
    private Integer tokens;
    
    @Column(nullable = false)
    private Double cost;
    
    @Column(name = "used_at", nullable = false)
    private LocalDateTime usedAt;
    
    @PrePersist
    protected void onCreate() {
        if (usedAt == null) {
            usedAt = LocalDateTime.now();
        }
    }
}