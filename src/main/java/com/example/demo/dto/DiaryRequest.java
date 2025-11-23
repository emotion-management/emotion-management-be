package com.example.demo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DiaryRequest {
    private String content; // 프론트엔드가 보낼 일기 내용
    private AdviceType type; // COMFORT 또는 REALITY

    // UI에서 아이콘 매핑이나 키워드 표시용 메타
    private String category; // 예: 친구/대화, 공부, 음악, 감정, 기타
    private String keywords; // 콤마 구분 키워드 문자열

    // 최근 심리 테스트 결과를 AI 프롬프트에 반영하기 위한 선택 입력
    private Long testResultId;
}
