# Layer

## Service Design

- 프레젠테이션 레이어, 응용 프로그램 레이어, 도메인 레이어, 인프라 레이어 로 구성된 멀티레이어 아키텍처로 구현
- 멀티레이어 아키텍처는 **DDD(Domain-Driven Design)** 으로 알려진 아키텍처 스타일의 기본 빌딩 블록
- 개발 방법론 중 상향식 접근 방식(bottom-to-top approach) 사용

### 프레젠테이션 레이어

- 사용자 인터페이스(UI) 담당

### 애플리케이션 레이어

- 애플리케이션 로직을 포함하고 애플리케이션의 전체 흐름을 유지하고 조정함.
- 비즈니스 로직이 아닌 *애플리케이션 로직만 포함*
- RESTful 웹 서비스, 비동기 API, gRPC API, GraphQL API 는 이 레이어의 일부임.

### 도메인 레이어

- 비즈니스 로직과 도메인 정보를 포함
- 주문(Order), 제품(Product) 등과 같은 비즈니스 객체의 상태 포함
- 도메인 레이어는 Service 와 Repository 로 구성

### 인프라 레이어

- 도메인 레이어의 객체를 읽고 유지하는 역할
- 다른 모든 레이어의 대한 지원 담당
- Database, Message Broker, File System 등과의 상호 작용을 위한 통신 담당 역할
- Spring Boot 는 인프라 레이어로 작동하며 Database, Message Broker 등과 같은 외부 및 내부 시스템과의 통신 및 상호 작용 지원함.
