# Emotion Management API Guide

프런트 개발자가 빠르게 연동할 수 있도록 정리한 공식 API 문서입니다.  
백엔드는 `http://localhost:8080` 기준으로 설명합니다.

---

# 1. 서버 실행 및 Swagger

### ▶ 실행
./gradlew bootRun

yaml
코드 복사

### ▶ Swagger UI
- http://localhost:8080/swagger-ui/index.html
- http://localhost:8080/v3/api-docs

---

# 2. 일기(Diary) API

## 2.1 일기 작성 + AI 분석
POST /api/diaries

css
코드 복사

#### Request Body
```json
{
  "content": "오늘 하루가 너무 힘들었어..."
}
Response
json
코드 복사
{
  "id": 1,
  "content": "...",
  "emotion": "SAD",
  "reply": "...",
  "book": "...",
  "music": "...",
  "createdAt": "2025-11-19T13:14:23"
}
2.2 일기 상세 조회
bash
코드 복사
GET /api/diaries/{id}
2.3 월별 일기 조회 (캘린더)
sql
코드 복사
GET /api/diaries/monthly?year=2025&month=11
Response
json
코드 복사
[
  {
    "id": 12,
    "emotion": "JOY",
    "day": 19,
    "summary": "기분 좋은 하루"
  }
]
3. 순간 감정(MomentEmotion) API
3.1 감정 저장
bash
코드 복사
POST /api/emotions
Request
json
코드 복사
{
  "type": "JOY"
}
Response
arduino
코드 복사
"감정 기록 완료!"
3.2 월별 감정 조회
sql
코드 복사
GET /api/emotions/monthly?year=2025&month=11
4. 감정 분석(AI) API (컨트롤러 추가 시 여기에 등록)
예시:

bash
코드 복사
POST /api/ai/emotion
Request
json
코드 복사
{
  "text": "오늘 너무 우울해..."
}
Response
json
코드 복사
{
  "emotion": "SAD",
  "advice": "오늘은 휴식이 필요해요."
}
5. 프런트 연동 패턴
ts
코드 복사
fetch("http://localhost:8080/api/diaries", {
  method: "POST",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify({ content: diaryText })
});
6. 데이터 흐름 요약
일기 작성 → AI 분석 → 저장

순간 감정(type only) 저장

월별 조회는 감정/일기를 캘린더용 JSON으로 반환

7. 추가 문서
(필요 시 더 작성 가능)

yaml
코드 복사

---

# ✅ **3) 스테이지 / 커밋 / 푸시**

```bash
git add .
git commit -m "add api guide docs"
git push origin docs-api
