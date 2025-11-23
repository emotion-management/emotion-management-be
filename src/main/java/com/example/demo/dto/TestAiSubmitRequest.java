package com.example.demo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class TestAiSubmitRequest {
    private String testCode;
    private List<String> answers; // 사용자가 선택/서술한 답변 목록
    private String notes; // 선택 메모
}
