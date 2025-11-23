package com.example.demo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TestAnswerRequest {
    private String testCode;
    private Integer score; // 간단 점수; 실제 구현 시 답변 목록 처리
    private String summary; // 결과 요약 텍스트
}
