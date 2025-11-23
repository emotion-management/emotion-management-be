package com.example.demo.service;

import com.example.demo.dto.AiResponse;
import com.example.demo.dto.ChatRequest;
import com.example.demo.dto.ChatResponse;
import com.example.demo.dto.AdviceType;
import com.fasterxml.jackson.databind.ObjectMapper; // JSON 변환기
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AiService {

    @Value("${openai.api-key}")
    private String openaiApiKey;

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    private final ObjectMapper objectMapper;

    public AiResponse getReply(String userDiary, AdviceType type) {
        return getReply(userDiary, type, null);
    }

    public AiResponse getReply(String userDiary, AdviceType type, String extraContext) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // 1. 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + openaiApiKey);
            headers.set("Content-Type", "application/json");

            String persona = "";

            if (type == AdviceType.REALITY) {
                // T 성향: 현실적인 조언
                persona = """
                    너는 냉철하고 현실적인 커리어 코치이자 인생 선배야.
                    빈말이나 감정적인 위로보다는, 사용자가 처한 상황을 객관적으로 분석하고
                    구체적인 해결책(Action Plan)들을 번호를 매겨서 제시해줘.
                    말투는 정중하지만 단호하게 해줘.
                    """;
            } else {
                // F 성향: 따뜻한 위로 (기본값)
                persona = """
                    너는 세상에서 가장 다정한 심리 상담사야.
                    사용자의 감정에 100% 공감해주고, '네 잘못이 아니야'라는 뉘앙스로 따뜻하게 위로해줘.
                    해결책보다는 감정을 어루만져주는 데 집중해줘.
                    """;
            }

            // JSON 강제 출력 지시 (이건 공통)
            String jsonInstruction = """

                반드시 아래 JSON 형식으로만 답변해. 다른 말은 붙이지 마.
                {
                    "emotionType": "감정 라벨 (HAPPY/SAD/ANGRY/ANXIOUS/TIRED 등)",
                    "emotionAnalysis": "감정 분석 설명",
                    "reply": "위로나 조언",
                    "book": "추천 도서 제목",
                    "music": "추천 음악 제목",
                    "emotion": "감정을 나타내는 이모지 1개"
                }
                """;

            String finalPrompt = persona + jsonInstruction + "\n\n일기 내용: " + userDiary;
            if (extraContext != null && !extraContext.isBlank()) {
                finalPrompt = finalPrompt + "\n\n최근 심리 테스트 결과 요약: " + extraContext;
            }

            // 3. 요청 객체 생성 (gpt-4o-mini 모델 사용)
            ChatRequest request = new ChatRequest("gpt-4o-mini", finalPrompt);

            // 4. API 호출
            HttpEntity<ChatRequest> entity = new HttpEntity<>(request, headers);
            ChatResponse response = restTemplate.postForObject(API_URL, entity, ChatResponse.class);

            // 5. 응답 파싱
            if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
                // 3. AI가 준 문자열 꺼내기
                String jsonString = response.getChoices().get(0).getMessage().getContent();

                // 혹시 AI가 ```json ... ``` 이런 마크다운을 붙일 경우 제거
                if (jsonString.startsWith("```json")) {
                    jsonString = jsonString.substring(7, jsonString.length() - 3);
                }

                // 4. 문자열 -> 자바 객체(AiResponse)로 변환 (Jackson Parsing)
                return objectMapper.readValue(jsonString, AiResponse.class);
            }
        } catch (Exception e) {
            e.printStackTrace(); // 에러나면 콘솔에 찍기
        }
        // 에러 시 빈 껍데기 리턴
        return new AiResponse();
    }

    /**
     * 일반 프롬프트용 단순 챗 호출 (첫 번째 choice content 반환)
     */
    public String chat(String prompt) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + openaiApiKey);
            headers.set("Content-Type", "application/json");

            ChatRequest request = new ChatRequest("gpt-4o-mini", prompt);
            HttpEntity<ChatRequest> entity = new HttpEntity<>(request, headers);
            ChatResponse response = restTemplate.postForObject(API_URL, entity, ChatResponse.class);
            if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
                return response.getChoices().get(0).getMessage().getContent();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
