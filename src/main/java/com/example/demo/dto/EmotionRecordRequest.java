package com.example.demo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class EmotionRecordRequest {
    private EmotionType emotionType;
    private String reason;
    private LocalDateTime recordedAt; // 선택 입력, 없으면 서버에서 now()
}
