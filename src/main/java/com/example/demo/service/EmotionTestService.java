package com.example.demo.service;

import com.example.demo.dto.TestAnswerRequest;
import com.example.demo.dto.TestAiResultResponse;
import com.example.demo.dto.TestAiSubmitRequest;
import com.example.demo.dto.TestQuestionResponse;
import com.example.demo.entity.EmotionTest;
import com.example.demo.entity.EmotionTestResult;
import com.example.demo.repository.EmotionTestRepository;
import com.example.demo.repository.EmotionTestResultRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmotionTestService {

    private final EmotionTestRepository testRepository;
    private final EmotionTestResultRepository resultRepository;
    private final AiService aiService;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void initTests() {
        if (testRepository.count() == 0) {
            seed("emotional-stability", "감정 안정성 테스트", "최근 감정 기복을 확인합니다.");
            seed("stress-index", "스트레스 지수 테스트", "스트레스 수준을 측정합니다.");
            seed("resilience", "회복탄력성 테스트", "회복력과 적응력을 봅니다.");
            seed("energy", "에너지 레벨 테스트", "에너지/피로도를 체크합니다.");
            seed("cognition", "인지 기능 테스트", "집중력과 기억력을 가볍게 점검합니다.");
        }
    }

    private void seed(String code, String title, String desc) {
        EmotionTest t = new EmotionTest();
        t.setCode(code);
        t.setTitle(title);
        t.setDescription(desc);
        testRepository.save(t);
    }

    public List<EmotionTest> list() {
        return testRepository.findAll();
    }

    public TestQuestionResponse generateQuestions(String testCode, int count) {
        EmotionTest test = testRepository.findByCode(testCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "test not found"));
        String prompt = "당신은 심리 검사 출제자입니다. 테스트 이름: " + test.getTitle() +
                "\n" + test.getDescription() + "\n" +
                "Likert 5점 척도로 답할 수 있는 진술형 질문만 생성하세요. (선택지: 매우 그렇다, 대체로 그렇다, 보통이다, 대체로 아니다, 아니다)\n" +
                count + "개의 간결한 질문을 JSON 배열 문자열로만 반환하세요. 예시: [\"질문1\",\"질문2\"]. 다른 말 붙이지 마세요.";
        String content = aiService.chat(prompt);
        try {
            List<String> questions = objectMapper.readValue(content, objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
            return new TestQuestionResponse(questions);
        } catch (Exception e) {
            // 파싱 실패 시 기본 질문 생성
            return new TestQuestionResponse(List.of(
                    "최근 감정 기복이 어느 정도인가요?",
                    "스트레스를 느낄 때 얼마나 빨리 회복하나요?",
                    "밤에 잠들기까지 얼마나 걸리나요?",
                    "하루 에너지 레벨은 어떤가요?",
                    "최근 일상에서 집중이 잘 되나요?"
            ));
        }
    }

    public EmotionTestResult submit(TestAnswerRequest request) {
        EmotionTest test = testRepository.findByCode(request.getTestCode())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "test not found"));
        EmotionTestResult result = new EmotionTestResult();
        result.setTest(test);
        result.setScore(request.getScore());
        result.setSummary(request.getSummary());
        return resultRepository.save(result);
    }

    public TestAiResultResponse submitAi(TestAiSubmitRequest request) {
        EmotionTest test = testRepository.findByCode(request.getTestCode())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "test not found"));
        String answers = String.join("\n", request.getAnswers() != null ? request.getAnswers() : List.of());
        String prompt = "심리 테스트 결과를 요약해 주세요. 테스트: " + test.getTitle() + "\n" +
                "답변 목록:\n" + answers + "\n" +
                (request.getNotes() != null ? "추가 메모: " + request.getNotes() + "\n" : "") +
                "JSON만 반환하세요. 예시: {\"score\":80,\"summary\":\"...\",\"advice\":\"...\"} 다른 말 금지";
        String content = aiService.chat(prompt);
        Integer score = null; String summary = ""; String advice = "";
        try {
            Map<String, Object> map = objectMapper.readValue(content, Map.class);
            if (map.get("score") != null) score = Integer.parseInt(map.get("score").toString());
            if (map.get("summary") != null) summary = map.get("summary").toString();
            if (map.get("advice") != null) advice = map.get("advice").toString();
        } catch (Exception e) {
            summary = content;
        }

        EmotionTestResult result = new EmotionTestResult();
        result.setTest(test);
        result.setScore(score);
        result.setSummary(summary);
        result.setAiAdvice(advice);
        result.setAiScore(score);
        resultRepository.save(result);

        return new TestAiResultResponse(score, summary, advice);
    }

    public List<EmotionTestResult> history(String testCode) {
        EmotionTest test = testRepository.findByCode(testCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "test not found"));
        return resultRepository.findAllByTest_IdOrderByCreatedAtDesc(test.getId());
    }
}
