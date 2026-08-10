# 일정 관리 시스템

관리자와 참가자가 일정을 등록하고 확인할 수 있도록 만든 Spring Boot 기반 일정 관리 프로젝트입니다.

관리자는 참가자와 일정을 관리할 수 있고,
참가자는 로그인 후 본인에게 등록된 일정을 달력과 상세 화면에서 확인할 수 있습니다.

---

## 기술 스택

### Backend
- Java
- Spring Boot
- Spring MVC
- Spring Data JPA
- Spring Security PasswordEncoder
- MySQL

### Frontend
- Thymeleaf
- HTML
- CSS
- JavaScript
- FullCalendar

### Build
- Gradle

---

## 주요 도메인

### Admin
관리자 계정 정보를 관리합니다.

### Member
참가자 정보를 관리합니다.

주요 정보:
- 참가자 번호
- 그룹 번호
- 로그인 아이디
- 비밀번호
- 이름
- 전화번호

### Schedule
참가자에게 등록되는 일정입니다.

주요 정보:
- 참가자
- 일정 날짜
- 코스
- 날씨
- 기온
- 습도

### ScheduleSpot
하나의 일정에 포함되는 세부 스팟 정보를 관리합니다.

주요 정보:
- 스팟 번호
- 시작 시간
- 방문 순서
- 연결된 일정

---

## 구현 완료 기능

### 공통
- 관리자 / 참가자 로그인 구분
- BCrypt를 이용한 비밀번호 암호화
- Session 기반 로그인 상태 관리
- 관리자 / 참가자 전용 Interceptor 적용

### 관리자

#### 참가자 관리
- 참가자 목록 조회
- 참가자 상세 조회

#### 일정 관리
- 일정 목록 조회
- 새 일정 등록
- 일정 상세 조회
- 일정 수정
- 일정 삭제
- 일정별 ScheduleSpot 등록
- 일정별 ScheduleSpot 수정
- 일정 삭제 시 연결된 ScheduleSpot 함께 삭제

### 참가자
- 참가자 로그인
- 본인 일정 조회
- FullCalendar 기반 월간 달력 표시
- DB 일정 FullCalendar에 출력
- 날짜 클릭 기능
- 일정이 있는 날짜 클릭 시 상세 API 호출
- 선택한 일정의 날짜 / 코스 / 그룹 / 날씨 표시
- ScheduleSpot 상세 목록 출력
- 시작 시간 HH:mm 형식 표시
- 상세 카드 자동 스크롤
- 일정이 없는 날짜 안내
- 다른 참가자의 일정 상세 API 접근 방지

---

## 일정 상세 조회 흐름

참가자 화면은 페이지 최초 로딩 시 본인의 일정 목록을 받아 FullCalendar에 표시합니다.

날짜를 클릭하면 해당 일정의 scheduleId를 이용해 백엔드 API에 상세정보를 요청합니다.

```text
날짜 클릭
    ↓
scheduleId 확인
    ↓
GET /member/api/schedules/{scheduleId}
    ↓
로그인 참가자 본인 일정인지 검증
    ↓
Schedule + ScheduleSpot 조회
    ↓
DTO를 JSON으로 반환
    ↓
JavaScript로 상세 카드 출력
```

---

## 일정 수정 방식

일정 수정은 JPA Dirty Checking을 이용합니다.

    Schedule 조회
    ScheduleSpot 조회
        ↓
    Entity의 update() 메서드 호출
        ↓
    @Transactional 종료
        ↓
    JPA 변경 감지
        ↓
    UPDATE SQL 실행

별도의 Repository save() 호출 없이,
영속 상태의 Entity 값을 변경하여 수정합니다.

---

## 일정 삭제 방식

ScheduleSpot이 Schedule을 참조하고 있기 때문에
자식 데이터를 먼저 삭제한 후 부모 Schedule을 삭제합니다.

    ScheduleSpot 삭제
        ↓
    Schedule 삭제

삭제 요청은 GET이 아닌 POST 요청으로 처리하며,
관리자 화면에서 삭제 전 confirm 창을 표시합니다.

---

## 현재 프로젝트 구조

    src/main/java/com/example/manage

    ├── config
    ├── controller
    ├── domain
    ├── dto
    ├── interceptor
    ├── repository
    └── service

화면은 다음과 같이 구분되어 있습니다.

    templates

    ├── admin
    │   ├── home.html
    │   ├── login.html
    │   ├── member-list.html
    │   ├── member-detail.html
    │   ├── schedule-list.html
    │   ├── schedule-form.html
    │   ├── schedule-edit.html
    │   └── schedule-detail.html
    │
    └── member
        ├── login.html
        └── home.html

---

## 다음 개발 예정

현재 핵심 일정 CRUD는 구현된 상태입니다.

다음 순서로 기능을 확장할 예정입니다.

1. 참가자 정보 수정
2. 서버 Validation 적용
3. 동일 참가자 / 동일 날짜 일정 중복 방지
4. ScheduleSpot 시작 시간 자동 계산
5. 관리자용 전체 일정 달력
6. 날씨 API 연동
7. EEG / PPG 등 측정 데이터 업로드 및 ScheduleSpot 연결
8. UI 및 예외 처리 최종 정리

---

## 향후 측정 데이터 구조

추후 참가자의 EEG, PPG 등의 측정 데이터를 일정별 스팟과 연결할 예정입니다.

예상 구조:

    Member
      ↓
    Schedule
      ↓
    ScheduleSpot
      ↓
    MeasurementSession
      ↓
    EEG / PPG / 기타 측정 데이터

이를 통해 다음과 같이 관리하는 것을 목표로 합니다.

    참가자
      ↓
    특정 날짜 일정
      ↓
    특정 Healing Spot
      ↓
    해당 위치에서 측정된 생체신호 데이터

---

## 실행 환경

- Java 17
- Spring Boot
- MySQL
- Gradle

애플리케이션 실행 후 localhost의 8080 포트로 접속합니다.

    http://localhost:8080