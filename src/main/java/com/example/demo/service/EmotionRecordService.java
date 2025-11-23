package com.example.demo.service;

import com.example.demo.dto.EmotionRecordRequest;
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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmotionRecordService {

    private final MomentEmotionRepository repository;

    public MomentEmotion record(EmotionRecordRequest request) {
        if (request.getEmotionType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "emotionType is required");
        }
        MomentEmotion saved = repository.save(
                MomentEmotion.builder()
                        .emotionType(request.getEmotionType())
                        .recordedAt(request.getRecordedAt())
                        .build()
        );
        return saved;
    }

    public EmotionSummaryResponse summary(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "endDate must be on or after startDate");
        }
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        List<MomentEmotion> emotions = repository.findAllByRecordedAtBetween(start, end);

        Map<EmotionType, Long> counts = emotions.stream()
                .collect(Collectors.groupingBy(MomentEmotion::getEmotionType, Collectors.counting()));

        EmotionType top = counts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        return new EmotionSummaryResponse(startDate, endDate, counts, emotions.size(), top);
    }

    public List<MomentEmotion> timeline(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);
        return repository.findAllByRecordedAtBetween(start, end).stream()
                .sorted(Comparator.comparing(MomentEmotion::getRecordedAt))
                .toList();
    }
}
