package com.example.demo.controller;

import com.example.demo.dto.TestAnswerRequest;
import com.example.demo.dto.TestAiResultResponse;
import com.example.demo.dto.TestAiSubmitRequest;
import com.example.demo.dto.TestQuestionResponse;
import com.example.demo.entity.EmotionTest;
import com.example.demo.entity.EmotionTestResult;
import com.example.demo.service.EmotionTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tests")
public class EmotionTestController {

    private final EmotionTestService testService;

    @GetMapping
    public List<EmotionTest> list() {
        return testService.list();
    }

    @PostMapping("/generate-questions")
    public TestQuestionResponse generate(@RequestParam String testCode, @RequestParam(defaultValue = "5") int count) {
        return testService.generateQuestions(testCode, count);
    }

    @PostMapping("/submit")
    public EmotionTestResult submit(@RequestBody TestAnswerRequest request) {
        return testService.submit(request);
    }

    @PostMapping("/submit-ai")
    public TestAiResultResponse submitAi(@RequestBody TestAiSubmitRequest request) {
        return testService.submitAi(request);
    }

    @GetMapping("/history")
    public List<EmotionTestResult> history(@RequestParam String testCode) {
        return testService.history(testCode);
    }
}
