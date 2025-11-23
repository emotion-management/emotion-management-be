package com.example.demo.service;

import com.example.demo.dto.EmotionType;
import com.example.demo.dto.ReportResponse;
import com.example.demo.entity.Diary;
import com.example.demo.entity.MomentEmotion;
import com.example.demo.repository.DiaryRepository;
import com.example.demo.repository.MomentEmotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final DiaryRepository diaryRepository;
    private final MomentEmotionRepository emotionRepository;

    public ReportResponse buildReport(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "endDate must be on or after startDate");
        }
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        List<Diary> diaries = diaryRepository.findAllByCreatedAtBetween(start, end);
        List<MomentEmotion> emotions = emotionRepository.findAllByRecordedAtBetween(start, end);

        Map<EmotionType, Long> counts = emotions.stream()
                .collect(Collectors.groupingBy(MomentEmotion::getEmotionType, Collectors.counting()));

        EmotionType top = counts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        List<String> keywordHighlights = diaries.stream()
                .map(Diary::getKeywords)
                .filter(k -> k != null && !k.isBlank())
                .flatMap(k -> Arrays.stream(k.split(",")))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()))
                .entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(5)
                .map(Map.Entry::getKey)
                .toList();

        String summary = String.format("%d개의 일기와 %d개의 감정 기록을 분석했어요. 대표 감정: %s",
                diaries.size(), emotions.size(), top != null ? top.name() : "없음");

        return new ReportResponse(startDate, endDate, diaries, counts, top, summary, keywordHighlights);
    }
}
