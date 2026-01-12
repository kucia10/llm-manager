package com.example.llm.controller;

import com.example.llm.dto.request.ModelCreateRequest;
import com.example.llm.dto.response.ModelResponse;
import com.example.llm.entity.LLMModel;
import com.example.llm.repository.ModelRepository;
import com.example.llm.util.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("ModelController 통합 테스트")
class ModelControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private TokenUtil tokenUtil;

    private String authToken;

    @BeforeEach
    void setUp() {
        modelRepository.deleteAll();
        authToken = "Bearer " + tokenUtil.generateToken("testuser", "ADMIN");
    }

    @Test
    @DisplayName("모델 생성 - 성공")
    void createModel_Success() throws Exception {
        // Given
        ModelCreateRequest request = ModelCreateRequest.builder()
                .name("GPT-4")
                .provider("OpenAI")
                .costPerToken(0.0001)
                .apiKey("sk-test-key")
                .isActive(true)
                .build();

        // When & Then
        mockMvc.perform(post("/api/models")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("GPT-4"))
                .andExpect(jsonPath("$.provider").value("OpenAI"))
                .andExpect(jsonPath("$.costPerToken").value(0.0001))
                .andExpect(jsonPath("$.isActive").value(true));

        // Verify database
        LLMModel model = modelRepository.findAll().get(0);
        assertEquals("GPT-4", model.getName());
        assertEquals("OpenAI", model.getProvider());
    }

    @Test
    @DisplayName("모델 생성 - 이름이 비어있는 경우 400 에러")
    void createModel_EmptyName_Returns400() throws Exception {
        // Given
        ModelCreateRequest request = ModelCreateRequest.builder()
                .name("")
                .provider("OpenAI")
                .costPerToken(0.0001)
                .build();

        // When & Then
        mockMvc.perform(post("/api/models")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("모델 생성 - 제공자가 비어있는 경우 400 에러")
    void createModel_EmptyProvider_Returns400() throws Exception {
        // Given
        ModelCreateRequest request = ModelCreateRequest.builder()
                .name("GPT-4")
                .provider("")
                .costPerToken(0.0001)
                .build();

        // When & Then
        mockMvc.perform(post("/api/models")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("모델 생성 - 토큰당 비용이 음수인 경우 400 에러")
    void createModel_NegativeCost_Returns400() throws Exception {
        // Given
        ModelCreateRequest request = ModelCreateRequest.builder()
                .name("GPT-4")
                .provider("OpenAI")
                .costPerToken(-0.0001)
                .build();

        // When & Then
        mockMvc.perform(post("/api/models")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("전체 모델 목록 조회 - 성공")
    void getAllModels_Success() throws Exception {
        // Given
        LLMModel model1 = LLMModel.builder().name("GPT-4").provider("OpenAI")
                .costPerToken(0.0001).isActive(true).build();
        LLMModel model2 = LLMModel.builder().name("Claude-2").provider("Anthropic")
                .costPerToken(0.00008).isActive(true).build();
        modelRepository.save(model1);
        modelRepository.save(model2);

        // When & Then
        mockMvc.perform(get("/api/models")
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[1].name").exists());
    }

    @Test
    @DisplayName("전체 모델 목록 조회 - 빈 목록")
    void getAllModels_EmptyList() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/models")
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    @DisplayName("활성 모델 목록 조회 - 성공")
    void getActiveModels_Success() throws Exception {
        // Given
        LLMModel activeModel = LLMModel.builder().name("GPT-4").provider("OpenAI")
                .costPerToken(0.0001).isActive(true).build();
        LLMModel inactiveModel = LLMModel.builder().name("Old Model").provider("Provider")
                .costPerToken(0.00005).isActive(false).build();
        modelRepository.save(activeModel);
        modelRepository.save(inactiveModel);

        // When & Then
        mockMvc.perform(get("/api/models/active")
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("GPT-4"))
                .andExpect(jsonPath("$[0].isActive").value(true));
    }

    @Test
    @DisplayName("활성 모델 목록 조회 - 빈 목록")
    void getActiveModels_EmptyList() throws Exception {
        // Given
        LLMModel inactiveModel = LLMModel.builder().name("Old Model").provider("Provider")
                .costPerToken(0.00005).isActive(false).build();
        modelRepository.save(inactiveModel);

        // When & Then
        mockMvc.perform(get("/api/models/active")
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    @DisplayName("모델 ID로 조회 - 성공")
    void getModelById_Success() throws Exception {
        // Given
        LLMModel model = LLMModel.builder().name("GPT-4").provider("OpenAI")
                .costPerToken(0.0001).isActive(true).build();
        LLMModel savedModel = modelRepository.save(model);

        // When & Then
        mockMvc.perform(get("/api/models/" + savedModel.getId())
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedModel.getId()))
                .andExpect(jsonPath("$.name").value("GPT-4"))
                .andExpect(jsonPath("$.provider").value("OpenAI"))
                .andExpect(jsonPath("$.costPerToken").value(0.0001));
    }

    @Test
    @DisplayName("모델 ID로 조회 - 존재하지 않는 모델")
    void getModelById_NotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/models/999")
                        .header("Authorization", authToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("모델 수정 - 성공")
    void updateModel_Success() throws Exception {
        // Given
        LLMModel model = LLMModel.builder().name("Old Name").provider("Old Provider")
                .costPerToken(0.0001).isActive(true).build();
        LLMModel savedModel = modelRepository.save(model);

        ModelCreateRequest request = ModelCreateRequest.builder()
                .name("New Name")
                .provider("New Provider")
                .costPerToken(0.0002)
                .isActive(false)
                .build();

        // When & Then
        mockMvc.perform(put("/api/models/" + savedModel.getId())
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.provider").value("New Provider"))
                .andExpect(jsonPath("$.costPerToken").value(0.0002))
                .andExpect(jsonPath("$.isActive").value(false));

        // Verify database
        LLMModel updatedModel = modelRepository.findById(savedModel.getId()).orElse(null);
        assertNotNull(updatedModel);
        assertEquals("New Name", updatedModel.getName());
    }

    @Test
    @DisplayName("모델 수정 - 존재하지 않는 모델")
    void updateModel_NotFound() throws Exception {
        // Given
        ModelCreateRequest request = ModelCreateRequest.builder()
                .name("New Name")
                .provider("New Provider")
                .costPerToken(0.0002)
                .build();

        // When & Then
        mockMvc.perform(put("/api/models/999")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("모델 삭제 - 성공")
    void deleteModel_Success() throws Exception {
        // Given
        LLMModel model = LLMModel.builder().name("GPT-4").provider("OpenAI")
                .costPerToken(0.0001).isActive(true).build();
        LLMModel savedModel = modelRepository.save(model);

        // When & Then
        mockMvc.perform(delete("/api/models/" + savedModel.getId())
                        .header("Authorization", authToken))
                .andExpect(status().isOk());

        // Verify deletion
        assertFalse(modelRepository.existsById(savedModel.getId()));
    }

    @Test
    @DisplayName("모델 삭제 - 존재하지 않는 모델")
    void deleteModel_NotFound() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/models/999")
                        .header("Authorization", authToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("모델 활성/비활성 토글 - 활성 모델 비활성화")
    void toggleModel_ActiveToInactive() throws Exception {
        // Given
        LLMModel model = LLMModel.builder().name("GPT-4").provider("OpenAI")
                .costPerToken(0.0001).isActive(true).build();
        LLMModel savedModel = modelRepository.save(model);

        // When & Then
        mockMvc.perform(patch("/api/models/" + savedModel.getId() + "/toggle")
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive").value(false));

        // Verify database
        LLMModel updatedModel = modelRepository.findById(savedModel.getId()).orElse(null);
        assertNotNull(updatedModel);
        assertFalse(updatedModel.getIsActive());
    }

    @Test
    @DisplayName("모델 활성/비활성 토글 - 비활성 모델 활성화")
    void toggleModel_InactiveToActive() throws Exception {
        // Given
        LLMModel model = LLMModel.builder().name("GPT-4").provider("OpenAI")
                .costPerToken(0.0001).isActive(false).build();
        LLMModel savedModel = modelRepository.save(model);

        // When & Then
        mockMvc.perform(patch("/api/models/" + savedModel.getId() + "/toggle")
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive").value(true));

        // Verify database
        LLMModel updatedModel = modelRepository.findById(savedModel.getId()).orElse(null);
        assertNotNull(updatedModel);
        assertTrue(updatedModel.getIsActive());
    }

    @Test
    @DisplayName("모델 활성/비활성 토글 - 존재하지 않는 모델")
    void toggleModel_NotFound() throws Exception {
        // When & Then
        mockMvc.perform(patch("/api/models/999/toggle")
                        .header("Authorization", authToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("모델 ID가 유효하지 않은 경우 400 에러")
    void invalidModelId_Returns400() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/models/invalid")
                        .header("Authorization", authToken))
                .andExpect(status().isBadRequest());
    }
}