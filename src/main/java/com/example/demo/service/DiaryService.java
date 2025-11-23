package com.example.demo.service;

import com.example.demo.dto.AiResponse;
import com.example.demo.dto.DiaryRequest;
import com.example.demo.entity.Diary;
import com.example.demo.repository.DiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.dto.DailySummaryResponse;
import com.example.demo.entity.MomentEmotion;
import com.example.demo.repository.MomentEmotionRepository;
import com.example.demo.entity.EmotionTestResult;
import com.example.demo.repository.EmotionTestResultRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final MomentEmotionRepository emotionRepository;
    private final EmotionTestResultRepository testResultRepository;
    private final AiService aiService;

    // 일기 저장
    @Transactional
    public AiResponse writeDiary(DiaryRequest dto) {
        // 1. 일기 저장 (DTO -> Entity 변환)
        Diary diary = Diary.builder()
                .content(dto.getContent())
                .category(dto.getCategory())
                .keywords(dto.getKeywords())
                .build();
        diaryRepository.save(diary);

        // 2. 최근 심리 테스트 결과가 있으면 프롬프트에 포함
        String testContext = null;
        if (dto.getTestResultId() != null) {
            testContext = testResultRepository.findById(dto.getTestResultId())
                    .map(tr -> {
                        StringBuilder sb = new StringBuilder();
                        if (tr.getSummary() != null) sb.append(tr.getSummary()).append(" ");
                        if (tr.getAiAdvice() != null) sb.append("조언: ").append(tr.getAiAdvice());
                        return sb.toString();
                    })
                    .orElse(null);
        }

        // 3. AI에게 분석 요청 (동기 처리: AI 답장 올 때까지 기다림)
        AiResponse aiResponse = aiService.getReply(dto.getContent(), dto.getType(), testContext);

        diary.updateAiResponse(
                aiResponse.getReply(),
                aiResponse.getBook(),
                aiResponse.getMusic(),
                aiResponse.getEmotion()
        );

        return aiResponse;
    }

    // 일기 하나 조회
    @Transactional(readOnly = true) // 읽기 전용
    public Diary getDiary(Long id) {
        // DB에서 ID로 찾고, 없으면 에러 냄
        return diaryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 일기가 없습니다. ID=" + id));
    }

    // 하루치 요약 조회 (일기, AI 응답, 감정)
    @Transactional(readOnly = true)
    public DailySummaryResponse getDailySummary(LocalDate date) {
        // 1. 시간 범위 계산 (00:00:00 ~ 23:59:59)
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        // 2. 그날 쓴 일기들 찾기 (없으면 null 반환, 일기 리스트)
        List<Diary> diaries = diaryRepository.findAllByCreatedAtBetween(start, end);

        // 3. 그날 누른 감정들 찾기
        List<MomentEmotion> emotions = emotionRepository.findAllByCreatedAtBetween(start, end);

        // 4. 묶어서 리턴
        return new DailySummaryResponse(diaries, emotions);
    }

    // 일기 월별 조회 (캘린더)
    @Transactional(readOnly = true)
    public java.util.List<Diary> getMonthlyDiaries(int year, int month) {
        // 1. 해당 월의 시작 날짜 계산 (예: 2025-11-01 00:00:00)
        java.time.LocalDateTime start = java.time.LocalDateTime.of(year, month, 1, 0, 0, 0);

        // 2. 해당 월의 마지막 날짜 계산 (예: 2025-11-30 23:59:59)
        // (plusMonths(1)로 다음 달 1일로 가고, minusSeconds(1)로 1초 뺌)
        java.time.LocalDateTime end = start.plusMonths(1).minusSeconds(1);

        // 3. DB 조회
        return diaryRepository.findAllByCreatedAtBetween(start, end);
    }

    // 일기 전체 조회 (캘린더용)
    @Transactional(readOnly = true)
    public java.util.List<Diary> getAllDiaries() {
        return diaryRepository.findAll();
    }
}
