# VoiceAndText 🎙️📝

**다중 모달 감정 분석 플랫폼**

VoiceAndText는 사용자의 음성과 텍스트를 분석하여 정확한 감정 분류를 제공하는 AI 기반 플랫폼입니다. 음성 감정, 텍스트 감정, 최종 감정 분류 및 음성-텍스트 간 불일치 점수를 함께 제공합니다.

---

## 🌟 기능

- **음성 분석**: 업로드된 오디오 파일로부터 감정 특징 추출
- **텍스트 분석**: 음성 전사본 텍스트의 감정 분석
- **감정 분류**: 음성과 텍스트 분석 결과를 통합한 최종 감정 판단
- **불일치 탐지**: 음성과 텍스트 감정 간의 모순 정도를 점수로 제공
- **설명 요약**: 분석 결과에 대한 해석 요약 제공
- **OAuth 인증**: 카카오 로그인 통합
- **게스트 모드**: 회원가입 없이도 분석 요청 가능

---

## 🛠️ 기술 스택

| 계층 | 기술 |
|------|------|
| **JDK** | Java 17 |
| **프레임워크** | Spring Boot 3.5.12 |
| **인증** | Spring Security, JWT (JJWT) |
| **데이터** | Spring Data JPA, PostgreSQL |
| **캐시** | Redis |
| **파일 저장소** | AWS S3 |
| **API 문서** | SpringDoc OpenAPI (Swagger) |
| **빌드 도구** | Gradle |
| **컨테이너** | Docker, Docker Compose |

---

## 📂 프로젝트 구조

```
voiceandtext/
├── src/main/java/com/quadcore/voiceandtext/
│   ├── common/              # 공통 유틸리티, 예외, 응답 포맷
│   ├── domain/              # 도메인 엔티티 (User, AnalysisRequest, AudioFile, etc.)
│   ├── application/         # 사용 사례, 애플리케이션 서비스
│   ├── infrastructure/      # 영속성, 캐시, 외부 서비스 연동, 보안 설정
│   └── presentation/        # 컨트롤러, DTO (Request/Response)
├── src/main/resources/
│   ├── application.yaml     # 기본 설정
│   ├── application-local.yml    # 로컬 개발 설정
│   └── application-prod.yml     # 프로덕션 설정
├── build.gradle             # Gradle 빌드 설정
├── Dockerfile               # 컨테이너 이미지 정의
├── docker-compose.prod.yml  # 프로덕션 환경 구성
└── README.md               # 이 파일

```

---

## 🏗️ 아키텍처

이 프로젝트는 **Clean Architecture** 원칙을 따릅니다.

### 계층 구조

**Presentation Layer** (프레젠테이션)
- REST API 컨트롤러
- Request/Response DTO
- HTTP 요청 처리

**Application Layer** (애플리케이션)
- 비즈니스 로직 오케스트레이션
- 서비스 계층 (UseCase)
- 트랜잭션 관리

**Domain Layer** (도메인)
- 도메인 엔티티 (User, AnalysisRequest, etc.)
- 도메인 규칙
- 비즈니스 로직 (도메인 객체 내)

**Infrastructure Layer** (인프라)
- 데이터베이스 영속성 (JPA Repository)
- 외부 API 연동 (OAuth, AWS S3)
- 캐시 관리 (Redis)
- 보안 설정

**Common Layer** (공통)
- 예외 처리
- 응답 포맷 표준화
- 유틸리티 함수

### 핵심 도메인 모델

```
User 1:N AnalysisRequest
AnalysisRequest 1:1 AudioFile
AnalysisRequest 1:1 Transcription
AnalysisRequest 1:1 AnalysisResult
```

**AnalysisRequest**는 시스템의 핵심 개념으로, 사용자(회원 또는 게스트)의 분석 요청을 나타냅니다.

---

## 📚 주요 엔드포인트

### 인증
- `POST /auth/signup` - 회원가입
- `POST /auth/login` - 로그인
- `POST /oauth/kakao/login` - 카카오 OAuth 로그인
- `POST /auth/refresh` - 토큰 갱신

### 분석
- `POST /analysis/requests` - 분석 요청 생성
- `GET /analysis/requests/{id}` - 분석 요청 조회
- `GET /analysis/requests` - 사용자의 분석 요청 목록 조회
- `DELETE /analysis/requests/{id}` - 분석 요청 삭제

### 관리자
- `GET /admin/users` - 사용자 목록 조회
- `GET /admin/analysis` - 분석 통계 조회


---

## 🔐 보안 기능

- **JWT 기반 인증**: 토큰 기반의 Stateless 인증
- **Spring Security**: 엔드포인트 보호
- **CORS 설정**: 안전한 크로스 도메인 요청
- **OAuth 2.0**: 소셜 로그인 (카카오)
- **게스트 모드**: 인증 없는 분석 요청 지원

---

## 📝 개발 가이드

### 코드 스타일
- Java: Google Java Style Guide
- 명명 규칙: camelCase (변수/메서드), PascalCase (클래스)
- 들여쓰기: 4 spaces

### Git 커밋 컨벤션
```
feat: 새로운 기능
fix: 버그 수정
docs: 문서 수정
style: 코드 스타일 변경 (기능 변화 없음)
refactor: 코드 리팩토링
test: 테스트 추가 또는 수정
chore: 빌드, 의존성 등
```

### 브랜치 전략
- `main`: 프로덕션 코드 (최안정)
- `dev`: 개발 브랜치
- `feat/*`: 기능 개발
- `fix/*`: 버그 수정
- `refactor/*`: 리팩토링

---

## 📜 라이선스

이 프로젝트는 [MIT 라이선스](LICENSE)를 따릅니다.

---

## 👥 기여

버그 리포트, 기능 제안, Pull Request를 환영합니다!

1. 저장소를 Fork합니다
2. 기능 브랜치를 생성합니다 (`git checkout -b feature/AmazingFeature`)
3. 변경사항을 커밋합니다 (`git commit -m 'Add AmazingFeature'`)
4. 브랜치에 Push합니다 (`git push origin feature/AmazingFeature`)
5. Pull Request를 생성합니다

---

## 📧 연락처

프로젝트에 대한 질문이나 피드백이 있으면:

- **Email**: lilloo04@naver.com
- **Issues**: GitHub Issues 탭에서 이슈 생성
- **Discussions**: GitHub Discussions 탭에서 토론

---

**마지막 업데이트**: 2026년 6월
