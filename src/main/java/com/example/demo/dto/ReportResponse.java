package com.example.demo.dto;

import com.example.demo.entity.Diary;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record ReportResponse(
        LocalDate startDate,
        LocalDate endDate,
        List<Diary> diaries,
        Map<EmotionType, Long> emotionCounts,
        EmotionType topEmotion,
        String summary,
        List<String> keywordHighlights
) {}
