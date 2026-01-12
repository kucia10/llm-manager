package com.example.llm.repository;

import com.example.llm.entity.LLMModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelRepository extends JpaRepository<LLMModel, Long> {
    
    List<LLMModel> findByProvider(String provider);
    
    List<LLMModel> findByIsActiveTrue();
}