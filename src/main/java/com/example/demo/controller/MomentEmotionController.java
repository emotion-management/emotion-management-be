package com.example.demo.controller;

import com.example.demo.dto.EmotionType;
import com.example.demo.entity.MomentEmotion;
import com.example.demo.repository.MomentEmotionRepository;
import com.example.demo.dto.EmotionSummaryResponse;
import com.example.demo.dto.EmotionRecordRequest;
import com.example.demo.service.EmotionAnalysisService;
import com.example.demo.service.EmotionRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/emotions")
public class MomentEmotionController {

    private final MomentEmotionRepository repository;
    private final EmotionAnalysisService analysisService;
    private final EmotionRecordService recordService;

    // 1. 순간 감정 꾹 누르기 (저장)
    @PostMapping
    public String addEmotion(@RequestBody Map<String, String> request) {
        // {"type": "HAPPY"} 형태로 받음
        String typeStr = request.get("type");
        EmotionType type = EmotionType.valueOf(typeStr); // 문자열 -> Enum 변환

        repository.save(new MomentEmotion(type, null));
        return "감정 기록 완료!";
    }

    // 신규 감정 기록 (이유/시간 포함)
    @PostMapping("/record")
    public MomentEmotion record(@RequestBody EmotionRecordRequest request) {
        return recordService.record(request);
    }

    // 2. 월별 조회 (캘린더 감정 탭용)
    @GetMapping("/monthly")
    public List<MomentEmotion> getMonthlyEmotions(@RequestParam int year, @RequestParam int month) {
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0, 0);
        LocalDateTime end = start.plusMonths(1).minusSeconds(1);

        return repository.findAllByCreatedAtBetween(start, end);
    }

    // 3. 하루 감정 요약 (일별)
    @GetMapping("/summary/daily")
    public EmotionSummaryResponse getDailySummary(@RequestParam LocalDate date) {
        return analysisService.getDailySummary(date);
    }

    // 4. 기간별 감정 요약 (시작~끝 날짜) - 끝 날짜가 시작보다 빠르면 400
    @GetMapping("/summary/range")
    public EmotionSummaryResponse getRangeSummary(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ) {
        return analysisService.getRangeSummary(startDate, endDate);
    }

    // 타임라인 조회 (이유/시간 포함)
    @GetMapping("/timeline")
    public List<MomentEmotion> getTimeline(@RequestParam LocalDate date) {
        return recordService.timeline(date);
    }
}
