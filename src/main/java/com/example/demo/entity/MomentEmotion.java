package com.example.demo.entity;

import com.example.demo.dto.EmotionType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class MomentEmotion {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING) // DB에 숫자가 아니라 "HAPPY" 글자로 저장
    private EmotionType emotionType;

    @Column(columnDefinition = "TEXT")
    private String reason;

    private LocalDateTime recordedAt; // 실제 기록 시간 (캘린더 타임라인용)

    private LocalDateTime createdAt;

    @Builder
    public MomentEmotion(EmotionType emotionType, String reason, LocalDateTime recordedAt) {
        this.emotionType = emotionType;
        this.reason = reason;
        this.recordedAt = recordedAt != null ? recordedAt : LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
    }
}
