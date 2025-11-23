package com.example.demo.controller;

import com.example.demo.dto.DiaryRequest;
import com.example.demo.entity.Diary;
import com.example.demo.service.DiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diaries")
public class DiaryController {

    private final DiaryService diaryService;

    // POST 요청 처리
    @PostMapping
    public com.example.demo.dto.AiResponse createDiary(@RequestBody DiaryRequest request) {
        return diaryService.writeDiary(request);
    }

    // 개별 조회 (예: GET /api/diaries/1)
    @GetMapping("/{id}")
    public Diary getDiary(@PathVariable Long id) {
        return diaryService.getDiary(id);
    }

    // 하루 요약 조회 (모달 띄울 때 사용)
    @GetMapping("/summary")
    public com.example.demo.dto.DailySummaryResponse getDailySummary(
            @RequestParam java.time.LocalDate date // 2025-11-19 형식으로 받음
    ) {
        return diaryService.getDailySummary(date);
    }

    // 월별 조회 (예: GET /api/diaries/monthly?year=2025&month=11)
    @GetMapping("/monthly")
    public java.util.List<com.example.demo.entity.Diary> getMonthlyDiaries(
            @RequestParam int year,
            @RequestParam int month
    ) {
        return diaryService.getMonthlyDiaries(year, month);
    }

    // 전체 조회 (예: GET /api/diaries)
    @GetMapping
    public java.util.List<Diary> getAllDiaries() {
        return diaryService.getAllDiaries();
    }

    // 테스트용 GET 요청
    @GetMapping("/test")
    public String test() {
        return "The AI-ary 서버 정상 작동 중!";
    }
}