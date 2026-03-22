# 분산 트랜잭션 Monolithic to MSA 프로젝트

## 프로젝트 개요
이 프로젝트는 MSA 환경에서 분산 트랜잭션 처리 방식을 구현하고 학습하기 위한 프로젝트입니다.

- 서비스 간 API 호출 기반 트랜잭션 처리
- Redis를 활용한 분산 락
- 재시도 및 장애 처리 전략
- Saga / 보상 트랜잭션 패턴 적용

---

## 아키텍처

```
Client
  ↓
Order Service
  ├── Product Service (재고 차감)
  └── Point Service (포인트 차감)
```

- Order Service: 주문 생성 및 전체 트랜잭션 오케스트레이션 담당
- Product Service: 상품 재고 관리
- Point Service: 사용자 포인트 관리

---

## 기술 스택

- Language: Java 17
- Framework: Spring Boot 4.0.3
- Communication: REST API (RestClient)
- Cache / Lock: Redis
- DB: MySQL
- Infra: Docker

---

## 서비스 간 통신

예시 (Order Service → 다른 서비스 호출):

```java
RestClient.builder()
    .baseUrl("http://localhost:8080")
    .build();
```

- Product Service: http://localhost:8082
- Point Service: http://localhost:8081

---

## 트랜잭션 흐름

### 정상 흐름

1. 주문 생성 요청
2. 재고 차감 (Product Service)
3. 포인트 차감 (Point Service)
4. 모든 단계 성공 시 주문 완료

---

### 장애 처리 (보상 트랜잭션)

- 재고 차감은 성공했지만 포인트 차감 실패 시  
  → 재고 롤백 수행

---

## 문제와 해결

### 문제 1: 분산 트랜잭션 일관성

- 원인: 서비스별로 DB가 분리되어 있음
- 해결:
    - 재시도 로직 적용
    - 보상 트랜잭션 구현

---

### 문제 2: 동시성 문제

- 원인: 동시에 재고/포인트를 갱신하는 요청 발생
- 해결:
    - Redis 기반 분산 락 적용

---

## 실행 방법

### 1. 인프라 실행

```bash
docker-compose up -d
```

### 2. 서비스 실행

- order-service: 8080
- product-service: 8082
- point-service: 8081

---

## 학습 포인트

- MSA 환경에서 트랜잭션의 한계 이해
- 2PC, TCC, Saga 패턴 설계 및 적용 경험, 차이 확인
- Redis 분산 락 활용
- 서비스 간 결합도 관리