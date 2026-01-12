package com.example.llm.service;

import com.example.llm.dto.request.ModelCreateRequest;
import com.example.llm.dto.response.ModelResponse;
import com.example.llm.entity.LLMModel;
import com.example.llm.exception.ErrorCode;
import com.example.llm.exception.ResourceNotFoundException;
import com.example.llm.repository.ModelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ModelService 단위 테스트")
class ModelServiceTest {

    @Mock
    private ModelRepository modelRepository;

    @InjectMocks
    private ModelService modelService;

    private LLMModel testModel;
    private ModelCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        testModel = LLMModel.builder()
                .id(1L)
                .name("GPT-4")
                .provider("OpenAI")
                .costPerToken(0.0001)
                .apiKey("sk-test-key")
                .isActive(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        createRequest = ModelCreateRequest.builder()
                .name("GPT-3.5")
                .provider("OpenAI")
                .costPerToken(0.00005)
                .apiKey("sk-new-key")
                .isActive(true)
                .build();
    }

    @Test
    @DisplayName("모델 생성 - 성공")
    void createModel_Success() {
        // Given
        LLMModel savedModel = LLMModel.builder()
                .id(2L)
                .name(createRequest.getName())
                .provider(createRequest.getProvider())
                .costPerToken(createRequest.getCostPerToken())
                .apiKey(createRequest.getApiKey())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(modelRepository.save(any(LLMModel.class))).thenReturn(savedModel);

        // When
        ModelResponse response = modelService.createModel(createRequest);

        // Then
        assertNotNull(response);
        assertEquals(createRequest.getName(), response.getName());
        assertEquals(createRequest.getProvider(), response.getProvider());
        assertEquals(createRequest.getCostPerToken(), response.getCostPerToken());
        assertTrue(response.getIsActive());
        verify(modelRepository, times(1)).save(any(LLMModel.class));
    }

    @Test
    @DisplayName("전체 모델 목록 조회 - 성공")
    void getAllModels_Success() {
        // Given
        LLMModel model2 = LLMModel.builder()
                .id(2L)
                .name("Claude-2")
                .provider("Anthropic")
                .costPerToken(0.00008)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<LLMModel> models = Arrays.asList(testModel, model2);
        when(modelRepository.findAll()).thenReturn(models);

        // When
        List<ModelResponse> responses = modelService.getAllModels();

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("GPT-4", responses.get(0).getName());
        assertEquals("Claude-2", responses.get(1).getName());
        verify(modelRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("활성 모델 목록 조회 - 성공")
    void getActiveModels_Success() {
        // Given
        LLMModel inactiveModel = LLMModel.builder()
                .id(2L)
                .name("Old Model")
                .provider("Provider")
                .costPerToken(0.00005)
                .isActive(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<LLMModel> activeModels = Arrays.asList(testModel);
        when(modelRepository.findByIsActiveTrue()).thenReturn(activeModels);

        // When
        List<ModelResponse> responses = modelService.getActiveModels();

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertTrue(responses.get(0).getIsActive());
        verify(modelRepository, times(1)).findByIsActiveTrue();
    }

    @Test
    @DisplayName("활성 모델 목록 조회 - 빈 목록")
    void getActiveModels_EmptyList() {
        // Given
        when(modelRepository.findByIsActiveTrue()).thenReturn(Arrays.asList());

        // When
        List<ModelResponse> responses = modelService.getActiveModels();

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(modelRepository, times(1)).findByIsActiveTrue();
    }

    @Test
    @DisplayName("모델 ID로 조회 - 성공")
    void getModelById_Success() {
        // Given
        when(modelRepository.findById(1L)).thenReturn(Optional.of(testModel));

        // When
        ModelResponse response = modelService.getModelById(1L);

        // Then
        assertNotNull(response);
        assertEquals(testModel.getId(), response.getId());
        assertEquals(testModel.getName(), response.getName());
        assertEquals(testModel.getProvider(), response.getProvider());
        verify(modelRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("모델 ID로 조회 - 존재하지 않는 모델")
    void getModelById_NotFound() {
        // Given
        when(modelRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> modelService.getModelById(999L)
        );
        assertEquals(ErrorCode.MODEL_NOT_FOUND, exception.getErrorCode());
        verify(modelRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("모델 수정 - 성공")
    void updateModel_Success() {
        // Given
        when(modelRepository.findById(1L)).thenReturn(Optional.of(testModel));
        when(modelRepository.save(any(LLMModel.class))).thenReturn(testModel);

        // When
        ModelResponse response = modelService.updateModel(1L, createRequest);

        // Then
        assertNotNull(response);
        verify(modelRepository, times(1)).findById(1L);
        verify(modelRepository, times(1)).save(any(LLMModel.class));
    }

    @Test
    @DisplayName("모델 수정 - 존재하지 않는 모델")
    void updateModel_NotFound() {
        // Given
        when(modelRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> modelService.updateModel(999L, createRequest)
        );
        assertEquals(ErrorCode.MODEL_NOT_FOUND, exception.getErrorCode());
        verify(modelRepository, times(1)).findById(999L);
        verify(modelRepository, never()).save(any(LLMModel.class));
    }

    @Test
    @DisplayName("모델 삭제 - 성공")
    void deleteModel_Success() {
        // Given
        when(modelRepository.findById(1L)).thenReturn(Optional.of(testModel));
        doNothing().when(modelRepository).delete(any(LLMModel.class));

        // When
        modelService.deleteModel(1L);

        // Then
        verify(modelRepository, times(1)).findById(1L);
        verify(modelRepository, times(1)).delete(any(LLMModel.class));
    }

    @Test
    @DisplayName("모델 삭제 - 존재하지 않는 모델")
    void deleteModel_NotFound() {
        // Given
        when(modelRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> modelService.deleteModel(999L)
        );
        assertEquals(ErrorCode.MODEL_NOT_FOUND, exception.getErrorCode());
        verify(modelRepository, times(1)).findById(999L);
        verify(modelRepository, never()).delete(any(LLMModel.class));
    }

    @Test
    @DisplayName("모델 활성/비활성 토글 - 활성 모델 비활성화")
    void toggleModel_ActiveToInactive() {
        // Given
        when(modelRepository.findById(1L)).thenReturn(Optional.of(testModel));
        when(modelRepository.save(any(LLMModel.class))).thenReturn(testModel);

        // When
        ModelResponse response = modelService.toggleModel(1L);

        // Then
        assertNotNull(response);
        assertFalse(response.getIsActive());
        verify(modelRepository, times(1)).findById(1L);
        verify(modelRepository, times(1)).save(any(LLMModel.class));
    }

    @Test
    @DisplayName("모델 활성/비활성 토글 - 비활성 모델 활성화")
    void toggleModel_InactiveToActive() {
        // Given
        testModel.setIsActive(false);
        when(modelRepository.findById(1L)).thenReturn(Optional.of(testModel));
        when(modelRepository.save(any(LLMModel.class))).thenReturn(testModel);

        // When
        ModelResponse response = modelService.toggleModel(1L);

        // Then
        assertNotNull(response);
        assertTrue(response.getIsActive());
        verify(modelRepository, times(1)).findById(1L);
        verify(modelRepository, times(1)).save(any(LLMModel.class));
    }

    @Test
    @DisplayName("모델 활성/비활성 토글 - 존재하지 않는 모델")
    void toggleModel_NotFound() {
        // Given
        when(modelRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> modelService.toggleModel(999L)
        );
        assertEquals(ErrorCode.MODEL_NOT_FOUND, exception.getErrorCode());
        verify(modelRepository, times(1)).findById(999L);
        verify(modelRepository, never()).save(any(LLMModel.class));
    }
}