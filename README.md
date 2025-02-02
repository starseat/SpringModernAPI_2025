# SpringModernAPI_2025

[스프링 6와 스프링 부트 3로 배우는 모던 API 개발](https://product.kyobobook.co.kr/detail/S000214875784) 실습

- 홈페이지: [https://wikibook.co.kr/spring-api-dev/](https://wikibook.co.kr/spring-api-dev/)
- github: [https://github.com/wikibook/spring-api-dev](https://github.com/wikibook/spring-api-dev)
- 원서: [https://github.com/PacktPublishing/Modern-API-Development-with-Spring-6-and-Spring-Boot-3](https://github.com/PacktPublishing/Modern-API-Development-with-Spring-6-and-Spring-Boot-3)

![스프링 6와 스프링 부트 3로 배우는 모던 API 개발](https://contents.kyobobook.co.kr/sih/fit-in/458x0/pdt/9791158395384.jpg)

## OAS

- OpenAPI 명세 (OpenAPI Specification, OAS)
  - [OAS v3.0.3 사용](https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.3.md)
- [예제 소스 내의 OAS 명세](https://github.com/starseat/SpringModernAPI_2025/blob/03_oas/src/main/resources/api/openapi.yaml)
- [OAS 설명](https://github.com/starseat/SpringModernAPI_2025/blob/03_oas/OAS.md)

## Swagger

### Swagger Editor

- [Swagger Editor](https://editor.swagger.io)
- Reset API 설계와 설명을 위해 사용
- 파일 작성시에는 OAS 3.0 버전이 설명되어 있는지 확인하고 사용
- 베타 버전은 [https://editor-next.swagger.io](https://editor-next.swagger.io) 에서 사용할 수 있음.

### Swagger Codegen

- [Swagger Codegen](https://github.com/swagger-api/swaggercodegen)
- 스프링 기반 API 인터페이스 생성
- Swagger Codegen 위에서 작동하는 코드를 생성하기 위해 [Gradle Plugin (gradle-swagger-generator-plugin)](https://github.com/int128/gradle-swagger-generator-plugin) 사용
- [OpenAPI 도구 Gradle Plugin - OpenAPI Generator](https://github.com/OpenAPITools/openapi-generator/tree/master/modules/openapi-generator-gradle-plugin) 를 마우스로 선택해도 됨.
  - 다만 불안정하므로 **Swagger Codegen 선호**

### Swagger UI

- [Swagger UI](https://swagger.io/swagger-ui)
- Rest API 문서 생성 용

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

## 추가
- [YAML 문법 참조](https://yaml.org/spec/)