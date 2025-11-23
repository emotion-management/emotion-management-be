package com.example.demo.service;

import com.example.demo.dto.EmotionSummaryResponse;
import com.example.demo.dto.EmotionType;
import com.example.demo.entity.MomentEmotion;
import com.example.demo.repository.MomentEmotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmotionAnalysisService {

    private final MomentEmotionRepository repository;

    public EmotionSummaryResponse getDailySummary(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);
        return buildSummary(date, date, start, end);
    }

    public EmotionSummaryResponse getRangeSummary(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "endDate must be on or after startDate");
        }
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        return buildSummary(startDate, endDate, start, end);
    }

    private EmotionSummaryResponse buildSummary(LocalDate startDate, LocalDate endDate,
                                                LocalDateTime start, LocalDateTime end) {
        List<MomentEmotion> emotions = repository.findAllByCreatedAtBetween(start, end);

        Map<EmotionType, Long> counts = emotions.stream()
                .collect(Collectors.groupingBy(MomentEmotion::getEmotionType, Collectors.counting()));

        EmotionType top = counts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        return new EmotionSummaryResponse(startDate, endDate, counts, emotions.size(), top);
    }
}
