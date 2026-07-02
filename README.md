# 라면픽 (ramen-picker)

조리 방식, 상황, 맛 취향, 매운 정도, 구매처 조건을 기반으로 라면을 추천하는 **조건 기반(룰) 라면 선택 서비스**입니다.
컵라면 · 봉지라면 · 뽀글이 모드를 명확히 구분하고, 추천 이유와 비슷한 라면까지 함께 제공합니다.

> 매운맛 테스트 서비스가 아닙니다. 매운 정도는 여러 추천 조건 중 하나일 뿐입니다.

---

## 기술 스택

| 구분 | 스택 |
| --- | --- |
| 백엔드 | Java 11, Spring Boot 2.7.18, Spring Data JPA, Gradle |
| DB | H2 (인메모리, 개발용) — 엔티티는 PostgreSQL 호환 설계 |
| 프론트 | React 18, Vite 5, React Router |
| 추천 | AI 미사용, 룰 기반 점수 계산 + 템플릿 이유 생성 |

> 현재 개발 환경 JDK가 11이라 Spring Boot 2.7 기준으로 구성했습니다.
> JDK 17+ 설치 시 `backend/build.gradle`의 버전을 올려 Spring Boot 3로 상향할 수 있습니다.

---

## 디렉터리 구조

```
ramen-picker/
├─ backend/                     # Spring Boot
│  ├─ src/main/java/com/example/ramenpicker/
│  │  ├─ ramen/                 # 라면 도메인 (controller/service/repository/dto/entity)
│  │  ├─ recommend/             # 추천 도메인 (controller/service/scorer/dto)
│  │  ├─ admin/                 # 관리자 API
│  │  ├─ common/                # enum, 컨버터, 예외처리, 메타 API
│  │  └─ config/                # CORS, seed 초기화
│  └─ src/main/resources/
│     ├─ application.yml
│     └─ seed-ramen.json        # 초기 라면 20종
└─ frontend/                    # React + Vite
   └─ src/
      ├─ pages/                 # Home / Recommend / Result / List / Detail / Admin
      ├─ components/            # 셀렉터, 카드, 폼 등
      ├─ context/               # 메타/결과 전역 상태
      └─ api.js                 # 백엔드 API 클라이언트
```

---

## 실행 방법

### 1. 백엔드 (포트 8095)

```bash
cd backend
./gradlew bootRun          # Windows: gradlew.bat bootRun
```

- 기동 시 `seed-ramen.json`의 라면 20종이 자동 적재됩니다.
- H2 콘솔: `http://localhost:8095/h2-console` (JDBC URL: `jdbc:h2:mem:ramenpicker`)

### 2. 프론트엔드 (포트 5173)

```bash
cd frontend
npm install
npm run dev
```

- 브라우저에서 `http://localhost:5173` 접속
- `/api` 요청은 Vite 프록시가 백엔드(8095)로 전달합니다.

---

## 추천 점수 계산 방식 (룰 기반)

| 조건 | 배점 |
| --- | --- |
| 조리 방식 일치 | +40 |
| 라면 타입 일치 | +25 |
| 상황 태그 일치 | +20 |
| 맛 태그 일치 | 태그당 +10 (최대 +30) |
| 매운 정도 근접 | 범위 내 +15 / 1단계 차이 +8 |
| 구매처 일치 | +10 |
| 조리 시간 조건 일치 | +5 |

- **적합도(%)** = 획득 점수 ÷ *요청에 실제로 지정된 조건의 만점 합* × 100
  (조건을 적게 걸어도 적합도가 과소평가되지 않도록 설계)
- **뽀글이 모드**: 봉지라면을 조리 방식 매칭으로 인정하고, 결과에 안전 안내 문구를 표시합니다.

### 매운 정도 매핑

| 선택값 | spicyLevel 범위 |
| --- | --- |
| 안 매운맛 | 1~2 |
| 살짝 매운맛 | 3~4 |
| 신라면급 | 5~6 |
| 불닭급 | 7~8 |
| 도전급 | 9~10 |
| 상관없음 | 점수 제외 |

---

## 주요 API

| 메서드 | 경로 | 설명 |
| --- | --- | --- |
| GET | `/api/meta` | 셀렉터용 메타(조리방식/타입/매운맛/태그/빠른추천) |
| GET | `/api/ramen` | 라면 목록/검색 (keyword·cookType·ramenType 등) |
| GET | `/api/ramen/{id}` | 라면 상세 |
| GET | `/api/ramen/{id}/similar` | 비슷한 라면 |
| POST | `/api/ramen/recommend` | 조건 기반 추천 |
| GET | `/api/ramen/quick-recommend?type=` | 빠른 추천 프리셋 |
| POST | `/api/admin/ramen` | 라면 등록 |
| PUT | `/api/admin/ramen/{id}` | 라면 수정 |
| DELETE | `/api/admin/ramen/{id}` | 라면 삭제 |

### 추천 요청 예시

```bash
curl -X POST http://localhost:8095/api/ramen/recommend \
  -H "Content-Type: application/json" \
  -d '{
    "cookType": "CUP",
    "ramenType": "SOUP",
    "situation": "해장",
    "flavorTags": ["얼큰", "깔끔"],
    "spicyPreference": "SHIN_RAMEN_LEVEL",
    "purchasePlace": "편의점",
    "limit": 5
  }'
```

빠른 추천 타입: `hangover`, `night_snack`, `convenience_cup`, `ppogeuli`, `mild`, `spicy_soup`, `stir_fried`, `heavy_meal`

---

## 구현 현황 (개발 우선순위 기준)

- [x] **1단계** — 메인 화면, 추천 조건 선택(단계형), 샘플 데이터 20종, 룰 기반 추천 API, 추천 결과 화면
- [x] **2단계** — 라면 상세, 비슷한 라면, 빠른 추천 버튼, 추천 이유 템플릿
- [x] **3단계** — 관리자 등록/수정/삭제, 검색, 별칭 검색, 결과 공유(링크/텍스트 복사)
- [ ] 4단계 — 사용자 평가, 추천 정확도 피드백, 인기 랭킹
- [ ] 5단계 — AI 보조(자연어 입력 변환, 추천 이유 개선)

---

## PostgreSQL 전환

운영 전환 시 `backend/build.gradle`에서 PostgreSQL 드라이버 주석을 해제하고,
`application.yml`의 `datasource`를 PostgreSQL 접속 정보로 교체하면 됩니다.
엔티티/DDL은 PostgreSQL과 호환되도록 작성되어 있습니다.
