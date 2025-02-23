# SpringModernAPI_2025

[스프링 6와 스프링 부트 3로 배우는 모던 API 개발](https://product.kyobobook.co.kr/detail/S000214875784) 실습

- 홈페이지: [https://wikibook.co.kr/spring-api-dev/](https://wikibook.co.kr/spring-api-dev/)
- github: [https://github.com/wikibook/spring-api-dev](https://github.com/wikibook/spring-api-dev)
- 원서: [https://github.com/PacktPublishing/Modern-API-Development-with-Spring-6-and-Spring-Boot-3](https://github.com/PacktPublishing/Modern-API-Development-with-Spring-6-and-Spring-Boot-3)

![스프링 6와 스프링 부트 3로 배우는 모던 API 개발](https://contents.kyobobook.co.kr/sih/fit-in/458x0/pdt/9791158395384.jpg)

## OAS

- OpenAPI 명세 (OpenAPI Specification, OAS)
  - [OAS v3.0.3 사용](https://github.com/starseat/SpringModernAPI_2025/blob/main/03_OAS.md)
- [예제 소스 내의 OAS 명세](https://github.com/starseat/SpringModernAPI_2025/blob/03_oas/src/main/resources/api/openapi.yaml)
- [OAS 설명](https://github.com/starseat/SpringModernAPI_2025/blob/03_oas/03_OAS.md)

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

## Layer

- [Project Layer](https://github.com/starseat/SpringModernAPI_2025/blob/main/04_api_layer.md)

## 추가
- [YAML 문법 참조](https://yaml.org/spec/)