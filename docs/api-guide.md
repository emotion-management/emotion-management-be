프런트엔드 연동 가이드

프런트 개발자가 Swagger 문서와(있다면) JWT 인증 흐름을 이해하고 바로 연동할 수 있도록 정리한 문서입니다.
모든 예시는 로컬에서 백엔드가 http://localhost:8080 으로 실행된다는 가정입니다.

1. 백엔드/Swagger 실행 방법

사전 조건

src/main/resources/application.properties 의 DB 설정이 현재 실행 환경(H2/MySQL)에 맞는지 확인합니다.

현재 프로젝트는 기본적으로 H2 DB를 사용하며, 별도 MySQL 연동 시 URL/계정 정보를 변경해야 합니다.

실행

터미널에서 아래 실행:

./gradlew bootRun


서버가 성공적으로 뜨면 Tomcat 8080 포트가 열리고, 콘솔에 Started DemoApplication 로그가 표시됩니다.

Swagger UI 위치

브라우저에서 http://localhost:8080/swagger-ui/index.html 접속.

OpenAPI JSON은 http://localhost:8080/v3/api-docs 에서 확인 가능합니다.

✅ 보안 설정상 Swagger 관련 경로(/swagger-ui/**, /swagger-ui.html, /v3/api-docs/**)는 인증 없이 접근할 수 있도록 열려 있습니다.

2. (선택) JWT 인증 흐름 요약

⚠️ 현재 백엔드 기준: 로그인/회원가입 API가 아직 없거나(미구현) 해커톤 버전에서 제외될 수 있습니다.
만약 추후 JWT가 추가되면 이 섹션을 그대로 활성화해서 사용하면 됩니다.

회원가입 (POST /api/auth/register)

Body 예시:
```
{
  "email": "user@example.com",
  "password": "Passw0rd!",
  "nickname": "woojin"
}
```

성공 시 201 Created 와 메시지 반환.

로그인 (POST /api/auth/login)

Body:
```
{
  "email": "user@example.com",
  "password": "Passw0rd!"
}
```

성공 시:
```
{
  "token": "<JWT>",
  "nickname": "woojin",
  "email": "user@example.com"
}
```

토큰 만료 시간: 백엔드 설정값 기준(Refresh 토큰 없음 → 만료 시 재로그인 필요)

헤더 규칙

인증이 필요한 요청에는 반드시 아래 헤더 포함:

Authorization: Bearer <JWT>


미인증 허용 경로: /api/auth/**, Swagger 경로
그 외 /api/**는 인증 필요(추후 적용 시)

로그아웃 ```(POST /api/auth/logout)```

서버 측 상태 변화 없음.

프런트에서 토큰 삭제로 처리합니다.

내 정보 조회```GET /api/users/me)```

JWT 포함 호출 시 내 정보 반환.

3. Swagger UI에서 인증 입력하기 (JWT 적용 시)

Swagger 상단 Authorize 클릭

Bearer <JWT> 전체 문자열 입력

Authorize → Close

토큰 갱신 시 다시 Authorize

4. 핵심 기능 API
4.1 일기 작성 + AI 피드백 생성 (POST /api/diaries)

설명: 사용자가 “비밀편지(일기)”를 작성하면 AI가 감정을 분석하고 답장/도서/음악을 생성해 DB에 저장 후 반환합니다.

요청 Body 예시
```
{
  "content": "오늘 하루가 너무 힘들고 우울했어..."
}
```

응답 예시(필드명은 현재 DTO 기준)
```
{
  "id": 1,
  "content": "오늘 하루가 너무 힘들고 우울했어...",
  "reply": "정말 고생 많았어요...",
  "emotion": "SAD",
  "book": "위로가 되는 책 추천",
  "music": "감정에 맞는 음악 추천",
  "createdAt": "2025-11-21T21:10:00"
}
```

React(Axios) 사용 예시

```import api from "../api/axiosInstance";

const res = await api.post("/api/diaries", {
  content: diaryText,
});
```
4.2 일기 상세 조회 ```(GET /api/diaries/{id})```

설명: 특정 일기 1건 상세(답장/감정 포함) 조회

사용 예시

```curl "http://localhost:8080/api/diaries/1"```

4.3 월별 일기 조회 (캘린더 데이터) (GET /api/diaries/monthly?year=&month=)

설명: 캘린더에 표시할 월별 일기 리스트를 반환합니다.

사용 예시

```curl "http://localhost:8080/api/diaries/monthly?year=2025&month=11"```


응답 예시
```
[
  {
    "id": 12,
    "day": 19,
    "emotion": "JOY",
    "summary": "기분 좋았던 하루"
  }
]
```
4.4 순간 감정 기록 (“감정 꾹 누르기”)``` (POST /api/emotions)```

설명: 사용자가 순간적으로 느낀 감정을 버튼으로 기록합니다.
(UI: 😄 😢 😡 같은 감정 이모지 탭)

요청 Body 예시

현재 백엔드에서는 type 키로 받음.
```
{
  "type": "JOY"
}

```
응답 예시

감정 기록 완료!


React(Axios) 예시
```
await api.post("/api/emotions", { type: "JOY" });
```
4.5 월별 순간 감정 조회 ```(GET /api/emotions/monthly?year=&month=)```

설명: 해당 월에 기록된 순간 감정 리스트 반환 (감정 탭/통계용).

사용 예시
```
curl "http://localhost:8080/api/emotions/monthly?year=2025&month=11"
```
5. 프런트엔드 연동 시나리오 (React 기준)

최초 진입

JWT 적용 전: 모든 API는 일단 바로 호출 가능

JWT 적용 후: 로그인/회원가입 → 토큰 저장 → 보호 API 접근

일기 작성 흐름

사용자가 편지 작성 →``` POST /api/diaries```

응답으로 reply/emotion/book/music 수신

프런트는 즉시 화면에 “AI 답장 도착 UI”로 표시

캘린더 조회

월 이동 시``` GET /api/diaries/monthly```

day + emotion 필드를 이용해 달력 UI에 감정 이모지 표시

순간 감정 기록

이모지 버튼 탭 →``` POST /api/emotions```

성공 시 토스트/애니메이션으로 “기록 완료” UX 처리

(JWT 추가 시) API 호출 템플릿
```
const token = localStorage.getItem("jwt");
const res = await fetch("http://localhost:8080/api/diaries/monthly?year=2025&month=11", {
  headers: {
    "Content-Type": "application/json",
    Authorization: `Bearer ${token}`,
  },
});
```
6. 자주 묻는 질문
질문	답변
Swagger 호출 시 인증이 필요한가요?	Swagger 페이지는 인증 없이 열리며, JWT 적용 전에는 모든 API 호출이 가능합니다. JWT가 추가되면 Authorize가 필요합니다.
감정 기록 API가 404가 떠요	현재 순간 감정 API는 ```/api/emotions``` 입니다. ```(/api/moment/emotion 아님)```
순간 감정 저장 Body 키가 뭐죠?	```{ "type": "JOY" }``` 형태로 요청해야 합니다.
AI 답장은 어디서 오나요?```	POST /api/diaries ```응답의 reply, emotion, book, music 필드로 함께 내려옵니다.
월별 조회 기준이 뭐예요?	year, month 쿼리로 해당 달 1일~말일 데이터를 반환합니다.

필요한 내용이 더 있다면 이 문서에 섹션을 추가하거나, Swagger 문서에 Tag/Description 을 보강해 주세요.
