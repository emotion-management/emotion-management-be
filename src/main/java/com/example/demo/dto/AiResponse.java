package com.example.demo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@NoArgsConstructor
@ToString
public class AiResponse {
    private String emotionType;    // 분석된 감정 (예: HAPPY/SAD 등)
    private String emotionAnalysis; // 감정에 대한 분석 설명
    private String reply;      // 위로의 말
    private String book;       // 추천 도서 제목
    private String music;      // 추천 음악 제목
    private String emotion;    // 감정 이모지
}
