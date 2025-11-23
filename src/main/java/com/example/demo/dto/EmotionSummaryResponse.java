package com.example.demo.dto;

import java.time.LocalDate;
import java.util.Map;

public record EmotionSummaryResponse(
        LocalDate startDate,
        LocalDate endDate,
        Map<EmotionType, Long> counts,
        long totalCount,
        EmotionType topEmotion
) {}
