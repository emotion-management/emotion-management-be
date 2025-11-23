프론트 연동용 API 가이드 (현재 모든 엔드포인트 permitAll, JSON 기반)
  Base URL: http://localhost:8080 (배포 시 도메인 교체)
  Headers: Content-Type: application/json

  1. 일기 / 감정 분석

  - POST /api/diaries
  - 
    요청: ```{ "content":"텍스트", "type":"COMFORT|REALITY", "category":"FRIEND|STUDY|MUSIC|EMOTION|OTHER", "keywords":"키워드1,키워드2", "testResultId":(선택, 심리테스트 결과 id) }```
    
    응답(AiResponse):
```
    {
      "emotionType": "HAPPY|SAD|ANGRY|ANXIOUS|TIRED",
      "emotionAnalysis": "감정 설명",
      "reply": "위로/조언",
      "book": "추천 도서",
      "music": "추천 음악",
      "emotion": "😊 같은 이모지"
    }
```
  - ```GET /api/diaries/monthly?year=2025&month=11```
    월별 일기 목록(아이콘 매핑용 category/keywords 포함)
  - ```GET /api/diaries/summary?date=YYYY-MM-DD```
    하루 일기/감정 요약

  2. 감정 기록

  - ```POST /api/emotions/record```
  - 
    요청: ```{ "emotionType":"HAPPY|SAD|ANGRY|ANXIOUS|TIRED", "reason":"텍스트", "recordedAt":"2025-11-23T10:30:00"(선택, 없으면 now) }```

    응답: MomentEmotion 객체
  - ```GET /api/emotions/timeline?date=YYYY-MM-DD```

     하루 타임라인(시간순 reason 포함)
  - ```GET /api/emotions/summary/daily?date=YYYY-MM-DD```

    응답:``` { startDate, endDate, counts:{emotion:count}, totalCount, topEmotion }```
  - ```GET /api/emotions/summary/range?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD```

     기간 감정 집계(위와 동일)
  - ```GET /api/emotions/monthly?year=YYYY&month=MM```

    월별 감정 기록 목록 (recordedAt 포함)

  3. 종합 리포트

  - ```GET /api/reports?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD```

    응답(ReportResponse): ```{ diaries:[...], emotionCounts:{}, topEmotion, summary, keywordHighlights:[...] }```

  4. 심리 테스트

  - ```GET /api/tests```

    기본 시드 5종 목록
  - ```POST /api/tests/generate-questions?testCode=...&count=5```

    응답: ```{ "questions": ["질문1", "질문2", ...] }```
  - ```POST /api/tests/submit-ai```

    요청: ```{ "testCode":"...", "answers":["Q1: 매우 그렇다", "Q2: 보통이다", ...], "notes":"선택" }```

     응답: ```{ "score":(nullable), "summary":"...", "advice":"..." }```
    (서버에 EmotionTestResult로 저장되며 aiAdvice/aiScore 포함)
  - ```GET /api/tests/history?testCode=...```
    테스트 결과 이력

  (참고) 수동 점수 제출: ```POST /api/tests/submit``` ```{ "testCode":"...", "score":80, "summary":"..." }```

  5. 인증/프로필 (데모용 평문, 토큰 없음)

  - ```POST /api/auth/signup```
   ``` { "email":"", "password":"", "nickname":"", "feedbackStyle":"COMFORT|REALITY", "notificationAm":true|false, "notificationTime":"HH:MM" }```
  - ```POST /api/auth/login``` ``` { "email":"", "password":"" }```
  - ```GET /api/profile?email=...```
  - ```PUT /api/profile?email=...``` (SignupRequest 형태로 변경 필드만 전달)

  응답 필드 메모

  - 일기/피드백: AiResponse에 emotionType/emotionAnalysis 추가됨.
  - 감정 요약: topEmotion 포함.
  - 일기 메타: Diary에 category/keywords 포함 → 프론트 아이콘 매핑 가능.
  - 테스트 결과: EmotionTestResult에 summary/aiAdvice/aiScore 저장.

  주의

  - 실제 서비스 시 비밀번호 해시/JWT 등 보안 적용 필요(현재 permitAll).
  - OpenAI 키를 application.properties에 설정해야 AI 호출 정상 동작.
  - DB는 MySQL emotion_diary, 접속 정보 맞춰야 함.
