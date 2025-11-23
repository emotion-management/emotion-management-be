package com.example.demo.dto;

import com.example.demo.entity.Diary;
import com.example.demo.entity.MomentEmotion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class DailySummaryResponse {
    private List<Diary> diary; // 그날 쓴 일기 (내용 + AI 답장 + 추천 등 다 들어있음)
    private List<MomentEmotion> emotions; // 그날 꾹꾹 누른 감정들
}