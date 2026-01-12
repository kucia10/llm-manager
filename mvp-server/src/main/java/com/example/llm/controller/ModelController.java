package com.example.llm.controller;

import com.example.llm.dto.request.ModelCreateRequest;
import com.example.llm.dto.response.ModelResponse;
import com.example.llm.service.ModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/models")
@RequiredArgsConstructor
@Tag(name = "모델 관리", description = "LLM 모델 CRUD 및 활성화/비활성화 API")
public class ModelController {
    
    private final ModelService modelService;
    
    @PostMapping
    @Operation(summary = "모델 생성", description = "새로운 LLM 모델을 등록합니다")
    public ResponseEntity<ModelResponse> createModel(@Valid @RequestBody ModelCreateRequest request) {
        ModelResponse response = modelService.createModel(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @Operation(summary = "모델 목록 조회", description = "모든 모델 목록을 조회합니다")
    public ResponseEntity<List<ModelResponse>> getAllModels() {
        List<ModelResponse> response = modelService.getAllModels();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/active")
    @Operation(summary = "활성 모델 조회", description = "활성화된 모델 목록을 조회합니다")
    public ResponseEntity<List<ModelResponse>> getActiveModels() {
        List<ModelResponse> response = modelService.getActiveModels();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "모델 상세 조회", description = "특정 모델의 상세 정보를 조회합니다")
    public ResponseEntity<ModelResponse> getModelById(@PathVariable Long id) {
        ModelResponse response = modelService.getModelById(id);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "모델 수정", description = "모델 정보를 수정합니다")
    public ResponseEntity<ModelResponse> updateModel(
            @PathVariable Long id,
            @Valid @RequestBody ModelCreateRequest request) {
        ModelResponse response = modelService.updateModel(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "모델 삭제", description = "모델을 삭제합니다")
    public ResponseEntity<Void> deleteModel(@PathVariable Long id) {
        modelService.deleteModel(id);
        return ResponseEntity.ok().build();
    }
    
    @PatchMapping("/{id}/toggle")
    @Operation(summary = "모델 활성/비활성", description = "모델의 활성 상태를 토글합니다")
    public ResponseEntity<ModelResponse> toggleModel(@PathVariable Long id) {
        ModelResponse response = modelService.toggleModel(id);
        return ResponseEntity.ok(response);
    }
}