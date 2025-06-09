# Security

 인증이 성공하면 응답의 일부분으로 JWT(JSON Web Token) 와 리프레시 토큰이라는 두 개의 토큰을 전달받고 JWT 는 액세스 토큰으로 사용
JWT 액세스 토큰은 인증을 요구하는 URL(Uniform Resource Locator)에 접근할 떄 사용하고 리프레시 토큰은 사용 중인 JWT 토큰이 만료됐을 떄 새 토큰을 요청하기 위해 사용.
이 때 리프레시 토큰이 유효하다면 새 토큰을 받을 수 있음.
<br />
 사용자에게는 관리자, 일반 사용자 등과 같은 역할(Role)이 부여되며 이 역할을 통해 특정 역학을 가진 사용자만 REST 엔트포인트에 접근하도록 권한을 부여.
CSRF(Cross-Site Request Forgery)와 CORS(Cross-Origin Resource Sharing) 에 대한 내용도 알고 있어야 됨.

# 스프링 시큐리티

- 보일러플레이트 코드(boilerplate code)를 매번 작성하지 않아도 엔터프라이즈 애플리캐ㅔ이션 레벨의 보안 기능을 쉽게 구현해주는 라이브러리로 구성된 프레임워크
- JWT와 유사한 방법으로 불투명(Opaque) 토큰도 지원
  - 불투명 토큰에서는 JWT 와 같은 방법으로 정보를 읽을 수 없고 토큰을 발급한 측만 정보를 읽을 수 있음.
- 토큰을 기반으로 인증하거나 권한을 부여하는 로직을 추가하려면 이 요청이 `DispatcherServlet`에 도달하기 전에 수행해야 함.
- 스프링 시큐리티 라이브러리는 요청이 `DispatcherServlet`에 도달하기 전에 먼저 처리되는 서블릿 `pre-filter` 를 필터 체인의 일부로 제공함.
  - `pre-filter` 는 요청이 실제 서블릿에 도달하기 전에 적용되는 서블릿 필터
  - 스프링 시큐리티의 경우 `DispatcherServlet` 에 요청이 도달하기 전에 동작함.
  - 이와 유사하게 `post-filter` 는 요청이 서블릿/컨트롤러에 의해 처리된 후에 적용됨.
- JWT 토큰 기반 인증을 구현할 때는 `spring-boot-starter-security` 또는 `spring-boot-starter-oauth2-resource-server` 를 사용하는 두 가지 방법이 있음.
  - `spring-boot-starter-security` 는 다음 라이브러리를 포함함.
    - spring-security-core
    - spring-security-config
    - spring-security-web
  - `spring-boot-starter-oauth2-resource-server` 는 앞에서 언급한 세가지와 다음 라이브러리를 제공함.
    - spring-security-oauth2-core
    - spring-security-oauth2-jose
    - spring-security-oauth2-resource-server

## 보안 필터 인증 순서

클라이언트가 HTTP 요청을 발생시키면 REST 컨트롤러에 도달하기 전에 아래 보안 필터를 모두 거침.
순서는 인증 결과에 따라 다를 수 있음.

 1. WebAsyncManagerIntegrationFilter
 2. SecurityContextPersistenceFilter
 3. HeaderWriterFilter
 4. CorsFilter
 5. CsrfFilter
 6. LogoutFilter
 7. BearerTokenAuthenticationFilter
 8. RequestCacheAwareFilter
 9. SecurityContextHolderAwareRequestFilter
10. AnonymousAuthenticationFilter
11. SessionManagementFilter
12. ExceptionTranslationFilter
13. FilterSecurityInterceptor

이 필터 체인은 향후 릴리즈에서 변경될 수 있음.
또한 `spring-boot-starter-security` 를 사용했거나 설정을 변경했다면 적용된 보안 필터 체인이 달라짐.
`SecurityFilterChain` 에서 사용 가능한 모든 필터는 [servlet-security-filters](https://docs.spring.io/spring-security/reference/servlet/architecture.html#servlet-security-filters) 에서 확인

# OAuth 2.0 리소스 서버를 사용한 인증 방법

  Spring Security OAuth 2.0 리소스 서버를 사용하면 `BearerTokenAuthenticationFilter` 를 사용해 인정과 권한 부여 로직을 구현하게 됨.
이 필터에는 베어러 토큰 인증 로직 포함.
그러나 여전히 토큰을 생성하는 REST 엔드포인트를 작성해야 함.

## OAuth 2.0 리소스 서버 인증 로직 처리 흐름

1. GET 방식으로 `/api/v1/addresses` 요청을 받음.
2. `BearerTokenAuthenticationFilter` 작동
3. 만약 요청에 `Authorization` 헤더가 없다면 
    1. `BearerTokenAuthenticationFilter` 가 베어러 토큰을 찾지 못하기 떄문에 인증 프로세스를 진행하지 않음.
    2. 이 경우 `BearerTokenAuthenticationFilter` 는 `FilterSecurityInterceptor` 에 호출에 대한 처리를 맡기고, `FilterSecurityInterceptor` 는 `AccessDeniedException` 예외를 발생시킴. (이 예외를 `ExceptionTranslationFilter` 가 처리)
    3. 이제 제어의 흐름이 `BearerTokenAuthenticationEntryPoint` 로 이동하고,  `BearerTokenAuthenticationEntryPoint` 는 `401 Unauthorized` 상태 코드와 함께 `Bearer` 라는 문자열 값이 포함된 `WWW-Authenticate` 헤더를 클라이언트로 전달함.
    4. 클라이언트는 `Bearer` 문장열이 포함된 `WWW-Authenticate` 헤더를 수신하는 경우에 유효한 `Bearer` 토큰이 포함된 `Authorization` 헤더를 사용해 재시도 해야 함.
    5. 이 단계에서는 클라이언트가 요청을 재생할 수 있기 때문에 보안상의 이유로 `requestCache` 설정은 `NullRequestCache` 로 설정
4. HTTP 요청이  `Authorization` 헤더를 포함하고 있다면
    1. `BearerTokenAuthenticationFilter` 는 HTTP 요청에서 `Authorization` 헤더를 추출하고 `Authorization` 헤더에서 토큰 추출
    2. 이 필터는 토큰값을 사용해 `BearertokenAuthenticationToken` 인스턴스 생성
    3. `BearertokenAuthenticationToken` 은 `Authentication` 인터페이스를 구현한 `AbstractAuthenticationToken` 클래스의 하위 클래스로 인증된 요청에 대한 토큰과 인증 대상에 대한 정보를 포함하고 있음.
5. HTTP 요청은 설정에 따라 `AuthenticationManager` 를 제공하는 `AuthenticationManagerResolver` 로 전달되고 `AuthenticationManager` 가 `BearerTokenAuthenticationToken` 토큰을 검증
6. 인증 과정이 설공하면
    1. `SecurityContext` 인스턴스에 `Authentication` 객체가 설정됨.
    2. 그런 다음 이 인스턴스는 `SecurityContextHolder.setContext()` 에 전달됨.
    3. 요청은 후속 처리를 위해 나머지 필터로 전달된 다음 `DispatcherServlet` 으로 라우팅 되고 마지막으로 `AddressController` 로 라우팅 됨.
7. 인증이 실패하면
    1. `SecurityContextHolder.clearContext()` 가 호출되어 컨텍스트 값을 정리함.
    2. 이 경우에는 `ExceptionTranslationFilter` 가 작동
    3. 제어 흐름이 `BearerTokenAuthenticationEntryPoint` 로 이동되고, `BearerTokenAuthenticationEntryPoint` 는 `401 ㄷUnauthorized` 상태 코드와 함께 `WWW-Authenticate` 헤더에 아래와 같은 적절한 에러 메시지를 포함하는 값을 설정하여 클라이언트쪽으로 응답함.

<pre>
Bearer error="invalid_token", 
error_description="An error occurred while attempting to decode the Jwt: Jwt expired at 2025-05-06T16:18:32Z",
error_uri="https://tools.ietf.org/html/rfc6750#section-3.1".
</pre>

# JWT

## JWT 구조

- 어떤 작업을 수행하거나 정보에 엑세스 하려면 권한이나 허가가 필요함.
- 이 **권한을 클레임(claim)** 이라고 함.
- 키는 클레임 이름을 표시하고 값에는 유효한 JSON 문자열 형태의 클레임 내용이 포함됨.
- 클레임은 JWT 에 대한 메타데이터일 수도 있음.
- JWT 는 일련의 클레임을 포함하는 인코딩된 문자열임.
- 이 클레임들은 JWS(JSON Web Signature) 로 디지털 서명되거나 JWE(JSON Web Encryption) 로 암호회 됨.
- JWT 는 통신을 하는 주체 간에 안전하게 클레임을 전송할 수 있도록 클레임 정보를 내부에 저장하고 있음.
- JWT 는 인코딩된 문자열로 aaa.bbb.ccc 와 같은 형태를 갖는데 각가의 점(.) 을 구분자로 하여 아래 세가지 부분으로 구성됨.
  - 헤더 (Header)
  - 페이로드 (Payload)
  - 시그니처 (Signature)
- https://jwt.io 와 같은 웹 사이트를 이용하면 JWT 의 콘텐츠 내용을 볼 수 있고 JWT 를 생성할 수도 있음.

### 헤더 (Header)

- **header 는 Base64URL 로 인코딩된 JSON 문자열로 구성됨.**
- 일반적으로 두개의 키-값 쌍을 포함함.

### 페이로드 (Payload)

- **payload 는 JWT 의 두번쨰 부분임.**
- **클레임을 포함하며 Base64URL 로 인코딩된 JSON 문자열임.**
- 클레임의 유형으로는 등록된(registered) 클레임, 공개(public) 클레임, 비공개(private) 클레임의 세가지 유형이 있음.
  - **Registered Claim**: 몇몇 클레임들은 IANA(Internat Assigned Numbers Authority) JSON 웹 토큰 클레임 레지스트리에 등록되어 있음. 따라서, 이 클레임들은 Registered Claim 으로 알려져 있음. 이 클레임들은 필수는 아니지만 사용하도록 권장됨.
    - 발급자(issure) 클레임(`iss` 를 키 값으로 함): 토큰 발급 주체를 나타냄.
    - 주제(subject) 클레임(`sub` 를 키 값으로 함): JWT의 주제를 나타내는 고유한 값.
    - 만료(expiration) 시간 클레임(`exp` 를 키 값으로 함): JWT의 만료 시간을 나타내는 숫자값으로 이 시간 이후로는 클레임이 거부됨.
    - 발급 시간(issued at) 클레임(`iat` 를 키 값으로 함): JWT 가 발행된 시간을 나타냄.
    - JWT ID 클레임(`jti` 를 키 값으로 함): JWT 의 고유 식별자를 나타냄.
    - 수신자(audience) 클레임(`aud` 를 키 값으로 함): JWT 가 의도한 수신자를 나타냄.
    - Not Before 클레임(`nbf` 를 키 값으로 함): 명시된 시간 이전에는 JWT 가 거부되어야 함을 나타냄.
  - **Public Claim**: JWT 발급자가 정의하며 등록된 클레임과 충돌하지 않아야 함. 따라서 IANA JWT 클레임 레지스트리에 등록하거나 이름이 중복되지 않도록 URI 로 정의해야 함.
  - **Private Claim**: 발급자와 수신자가 정의하여 사용해 커스텀 클레임으로 등록되거나 공개되지 않음.

### 서명 (Signature)

- **세 번째 부분이며 마찬가지로 Base64URL 로 인코딩된 문자열**
- 서명은 JWT 의 콘텐츠 보호를 위한 것으로 콘텐츠가 노출되어 눈에 보이더라도 토큰에 서명이 되어있는 콘텐트는 수정이 불가능함.
- 토큰에 서명하는 과정에는 개인 키, 공개 키와 함께 Base64로 인코딩된 헤더와 페이로드가 서명 알고리즘에 전달됨.
- 페이로드에 민감한 정보나 비밀 정보를 포함하려면 페이로드에 추가하기 전에 암호화하는 것이 좋음.
- 서명은 토큰을 수신했을 때 콘텐츠 내용이 수정되지 않았다는 것을 보장함.
- 공개 키, 개인키를 사용하면 발신자를 검증할 수 있으므로 보안 단계를 강화할 수 있음.
- JWT 와 JWE를 조함해 사용해도 됨.
  - 권장되는 방법은 먼저 JWE를 사용해 페이로드를 암호화한 다음 서명하는 것임. 


# 참고

- **bcrypt** :
  - 단방향 암호화를 위해 만들어진 해시 함수
  - sha256과 같은 기존 해시 함수가 암호화라는 본연의 목적이 아닌 빠르게 데이터를 검색하기 위한 자료구조로 설계됐기 때문에 가지고 있는 취약점을 보완
- **RSA**: 
  - RSA를 개발한 사람들인 Rivest, Shamir, Adleman 의 성에서 따옴
  - 디지털 서명과 키 설정에 대해서 각각 **연방 정보 처리 표준(Federal Information Processing Standards, FIPS)** 의 FIPS186 과 **특별 간행물(Special Publication, SP)** 의 SP800-56B 을 통해서 승인된 알고리즘.

