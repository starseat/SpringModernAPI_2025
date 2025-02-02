# OAS

- OpenAPI 명세 (OpenAPI Specification, OAS)
    - [OAS v3.0.3 사용](https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.3.md)
- [예제 소스 내의 OAS 명세 - openapi.yaml](https://github.com/starseat/SpringModernAPI_2025/blob/03_oas/src/main/resources/api/openapi.yaml)
- openapi.yaml 내용을 복사하여 [Swagger Editor](https://editor.swagger.io) 의 편집기에 붙여 넣어 최적화된 사용자 인터페이스 API 확인
  - Swagger Editor 에서 작성 후 openapi.yaml 에 옮겨서 사용함.
  - 이 후 OAS 를 스프링 코드로 변환함.

## OAS 기본 구조

- openapi, info, externalDocs 는 API 의 메타데이터를 정의하는데 사용

### openapi (버전)

- 어떤 OA 를 사용중인지 표시
- OpenAPI 는 시맨틱 버전 관리([semver](https://semver.org)) 사용
  - `major:minor:patch` 형식 

### info

- API 에 대한 메타 데이터 포함
- 문서 생성에 사용되며 클라이언트가 사용할 수 있음.
- **title, version 만 필수**, 나머진 선택(optional) 필드
- info 필드
  - title: API 제목
  - version: API 버전을 문자혈 형식으로 노출
  - description:
    - api 세부정보를 설명하기 위해 사용
    - 이 부분에는 [Markdown](https://spec.commonmark.org) 사용
  - termOfService: 서비스 약관으로 연결되는 URL. (올바른 URL 형식 작성 필요)
  - contact:
    - api 제공자의 연락 정보
    - `email` attribute 는 담장자/조직 의 이메일 주소 기입
    - `name` attribute 는 담당자/조직 의 이름 기입
    - `URL` attribute 는 연락처 페이지에 대한 링크 제공
  - license:
    - 라이선스 정보
    - `name` attribute 는 MIT 와 같은 올바른 라이선스 이름을 나타내는 필수 필드
    - `url` 은 선택 사항이며 라이선스 문서에 대한 링크 제공

### externalDocs

- 노출된 API 의 확장 문서를 가리키는 선택적 필드
- `description`: 외부 문서의 요약을 정의하는 선택적(optional) 필드. Markdown 구문 사용
- `url`: 필수(mandatory) 항목. 외부 문서에 대한 링크

### servers

- API 를 호스팅하는 서버 목록을 가지는 선택적 항목
- 호스팅된 API 문서가 대화식이면 Swagger UI 에서 여기에 작성된 버서를 사용해서 API 를 직접 호출하고 응답 표시
- 이 부분이 제공되지 않으면 기본값으로 호스팅된 서버의 루트(/) 를 가리킴.
- `url` attribute 를 사용해 표시

### tags

- 루트 수준에서 정의된 tags 절은 tag 와 tag 메타데이터의 컬렉션 포함
- tags 는 리소스가 수행하는 오퍼레이션을 그룹화 하는데 사용
- **tags 메타데이터는 필수 필드인 name** 과 두 개의 *추가 선택적 속성인 description 과 externalDocs 를 포함*.

### paths

- end-point 정의
- URI 구성 및 HTTP 메소드 작성
- 각 메소드는 `tags, summary, description, operationId, parameters, requestBody, response` 7개의 필드를 가짐 

#### paths 의 method field

- tags
  - API 를 그룹화 할때 사용
  - Swagger Codegen 은 tags 를 사용하여 코드를 생성할 수도 있음.

- summary, description
  - OAS 의 메타데이터와 동일한 기능
  - API 의 작업 요약과 설명 
  - 동일한 스키마를 참조하는 description 필드에는 Markdown  사용 가능

- operationId
  - 오퍼레이션 이름
  - 예: addCartItemsByCustomerId
  - Swagger Codegen 에서 생성한 API 인터페이서의 메소드 이름으로 사용

- parameters
  - name 필드 앞에 - (하이픈) 이 있을경우 배열 요소 선언을 의미하며, 여러 개의 매개변수를 포함할 수 있고, 실제로 경로(path) 와 쿼리 매개변수 (query parameters) 의 조합인 배열로 선언됨.
  - path 매개변수(parameter) 의 경우 parameters 아래의 name 값이 중괄호({}) 안의 path 에 지정된 값과 동일한지 확인해야 함
  - parameter 필드는 API query, path, header, cookie 매개변수를 포함함.
  - description 은 정의된 매개변수를 설명
  - required 필드를 사용하면 필드의 필수/선택 여부 표시

- requestBody
  - 요청 페이로드 객체를 정의하는데 사용
  - description, content 필드 포함 (이 설명은 response 에 작성)

- response (필수 필드)
  - API 요청에 대한 응답 타입 정의
  - 기본 필드로 HTTP 상태 코드 포함
  - default 응답 또는 200 OK 와 같은 성공을 표시하는 HTTP 상태 코드가 될 수 있는 응답이 하나 이상 있어야 함.
  - 응답 타입 필드에는 description, headers, content 세가지 타입 필드 가 있음.
    - description: 응답 설명 
    - headers: 헤더와 그 값을 정의하는 데 사용
      ~~~yaml
      response:
        200:
          description: operation successful
          headers:
            X-RateLimit-Limit:
              schema:
                type: integer
      ~~~
    - content: 
      - 다양한 미디어 타입을 나타태는 contents type 정의
      - application/json, application/xml 과 같은 타입 등

### components

- OAS 는 다음의 6가지 기본 데이터 타입 지원 (모두 소문자)
    - string
    - number
    - integer
    - boolean
    - object
    - array

- 날짜(date), 시간(time), 소수점(float) 타입들은 string 타입에 format 을 사용하거나 object 타입 사용
- 예시
    - 소수점 1 = type: number / format: float
    - 소수점 2 = type: number / format: double
    - 정수 1 = type: integer / format: int32 (int 타입(부호 있는 32 bit 정수))
    - 정수 2 = type: integer / format: int64 (long 타입(부호 있는 64 bit 정수))
    - 날짜 = type: string / format: date (RFC 3339 에 따른 날짜, yyyy-MM-dd 형식)
    - 바이트 = type: string / format: byte (Base64 로 인코딩한 값 포함)
    - 바이너리 = type: string / format: binary (이진 데이터 포함, **파일 사용 가능**)

- $ref 는 참조 객체를 나타냄
  - [JSON 참조](https://datatracker.ietf.org/doc/html/draft-pbryan-zyp-json-ref-03) 기반. YAML 에서도 동일함.
  - `$ref`를 통해 동일 문서 또는 외부 문서의 객체 참조
  - 주로 API 정의가 여러 파일로 나누어져 있을때 사용