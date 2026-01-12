package com.example.llm.service;

import com.example.llm.dto.request.ModelCreateRequest;
import com.example.llm.dto.response.ModelResponse;
import com.example.llm.entity.LLMModel;
import com.example.llm.exception.ErrorCode;
import com.example.llm.exception.ResourceNotFoundException;
import com.example.llm.repository.ModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ModelService {
    
    private final ModelRepository modelRepository;
    
    public ModelResponse createModel(ModelCreateRequest request) {
        LLMModel model = LLMModel.builder()
                .name(request.getName())
                .provider(request.getProvider())
                .costPerToken(request.getCostPerToken())
                .apiKey(request.getApiKey())
                .isActive(true)
                .build();
        return mapToModelResponse(modelRepository.save(model));
    }
    
    @Transactional(readOnly = true)
    public List<ModelResponse> getAllModels() {
        return modelRepository.findAll().stream()
                .map(this::mapToModelResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ModelResponse> getActiveModels() {
        return modelRepository.findByIsActiveTrue().stream()
                .map(this::mapToModelResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public ModelResponse getModelById(Long id) {
        LLMModel model = modelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.MODEL_NOT_FOUND));
        return mapToModelResponse(model);
    }
    
    public ModelResponse updateModel(Long id, ModelCreateRequest request) {
        LLMModel model = modelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.MODEL_NOT_FOUND));
        
        model.setName(request.getName());
        model.setProvider(request.getProvider());
        model.setCostPerToken(request.getCostPerToken());
        if (request.getApiKey() != null) {
            model.setApiKey(request.getApiKey());
        }
        if (request.getIsActive() != null) {
            model.setIsActive(request.getIsActive());
        }
        
        return mapToModelResponse(modelRepository.save(model));
    }
    
    public void deleteModel(Long id) {
        LLMModel model = modelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.MODEL_NOT_FOUND));
        modelRepository.delete(model);
    }
    
    public ModelResponse toggleModel(Long id) {
        LLMModel model = modelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.MODEL_NOT_FOUND));
        
        model.setIsActive(!model.getIsActive());
        
        return mapToModelResponse(modelRepository.save(model));
    }
    
    private ModelResponse mapToModelResponse(LLMModel model) {
        return ModelResponse.builder()
                .id(model.getId())
                .name(model.getName())
                .provider(model.getProvider())
                .costPerToken(model.getCostPerToken())
                .isActive(model.getIsActive())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .build();
    }
}