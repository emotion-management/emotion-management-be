package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Diary {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK

    @Column(columnDefinition = "TEXT") // 내 일기
    private String content;

    @Column(columnDefinition = "TEXT") // AI 답장
    private String reply;      // AI의 답장 내용
    private String book;       // 추천 책
    private String music;      // 추천 음악
    private String emotion;    // 감정 이모지

    // UI 아이콘 매핑/검색용 메타데이터
    private String category;   // 예: FRIEND, STUDY, MUSIC, EMOTION, OTHER 등
    private String keywords;   // 콤마 구분 키워드 문자열

    private LocalDateTime createdAt;

    @Builder
    public Diary(String content, String category, String keywords) {
        this.content = content;
        this.category = category;
        this.keywords = keywords;
        this.createdAt = LocalDateTime.now();
    }

    public void updateAiResponse(String reply, String book, String music, String emotion) {
        this.reply = reply;
        this.book = book;
        this.music = music;
        this.emotion = emotion;
    }
}
