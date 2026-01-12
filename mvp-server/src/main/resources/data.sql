-- 초기 사용자 데이터 (테스트용)
INSERT INTO users (username, password, email, role, created_at) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAtVYQWy', 'admin@example.com', 'ADMIN', CURRENT_TIMESTAMP),
('user1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAtVYQWy', 'user1@example.com', 'USER', CURRENT_TIMESTAMP);

-- 초기 팀 데이터
INSERT INTO teams (name, quota, usage, created_at, updated_at) VALUES
('AI Research Team', 1000000, 250000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Data Science Team', 500000, 150000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Product Team', 300000, 50000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 초기 LLM 모델 데이터
INSERT INTO llm_models (name, provider, cost_per_token, api_key, is_active, created_at, updated_at) VALUES
('GPT-4', 'OpenAI', 0.00003, 'sk-test-key-1', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('GPT-3.5-Turbo', 'OpenAI', 0.000002, 'sk-test-key-2', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Claude-2', 'Anthropic', 0.00001, 'sk-test-key-3', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Claude-Instant', 'Anthropic', 0.0000008, 'sk-test-key-4', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 초기 팀 멤버 데이터
INSERT INTO team_members (team_id, user_id, role, created_at) VALUES
(1, 1, 'ADMIN', CURRENT_TIMESTAMP),
(1, 2, 'MEMBER', CURRENT_TIMESTAMP),
(2, 2, 'ADMIN', CURRENT_TIMESTAMP);

-- 초기 사용량 데이터 (테스트용)
INSERT INTO usage (team_id, model_id, tokens, cost, used_at) VALUES
(1, 1, 5000, 0.15, CURRENT_TIMESTAMP - INTERVAL '1' DAY),
(1, 2, 100000, 0.20, CURRENT_TIMESTAMP - INTERVAL '2' DAY),
(2, 1, 3000, 0.09, CURRENT_TIMESTAMP - INTERVAL '1' DAY),
(2, 3, 2000, 0.02, CURRENT_TIMESTAMP - INTERVAL '3' DAY);