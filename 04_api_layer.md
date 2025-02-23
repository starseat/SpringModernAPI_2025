# Layer

## Service Design

- 프레젠테이션 레이어, 응용 프로그램 레이어, 도메인 레이어, 인프라 레이어 로 구성된 멀티레이어 아키텍처로 구현
- 멀티레이어 아키텍처는 **DDD(Domain-Driven Design)** 으로 알려진 아키텍처 스타일의 기본 빌딩 블록
- 개발 방법론 중 상향식 접근 방식(bottom-to-top approach) 사용

## 프레젠테이션 레이어

- 사용자 인터페이스(UI) 담당

## 애플리케이션 레이어

- 애플리케이션 로직을 포함하고 애플리케이션의 전체 흐름을 유지하고 조정함.
- 비즈니스 로직이 아닌 *애플리케이션 로직만 포함*
- RESTful 웹 서비스, 비동기 API, gRPC API, GraphQL API 는 이 레이어의 일부임.

## 도메인 레이어

- 비즈니스 로직과 도메인 정보를 포함
- 주문(Order), 제품(Product) 등과 같은 비즈니스 객체의 상태 포함
- 도메인 레이어는 Service 와 Repository 로 구성

## 인프라 레이어

- 도메인 레이어의 객체를 읽고 유지하는 역할
- 다른 모든 레이어의 대한 지원 담당
- Database, Message Broker, File System 등과의 상호 작용을 위한 통신 담당 역할
- Spring Boot 는 인프라 레이어로 작동하며 Database, Message Broker 등과 같은 외부 및 내부 시스템과의 통신 및 상호 작용 지원함.

# HATEOAS

- API 응답의 일부로 반환된 모든 모델에 링크 필드가 포함되어 있는지 확인해야 함.
- 수동 또는 자동 생성을 통해 링크(org.springframework.hateoas.Link 클래스) 를 모델과 연결하는 다양한 방법이 있음.
- 스프링 HATEOAS 의 링크와 속성은 `RFC 8288` 에 따라 구현됨.
- 하이퍼미디어로 사용자 정의 모델을 강화하기 위해 다음 클래스를 제공함. (모델에 기본적으로 추가하기 위한 링크와 메소드가 포함된 클래스임)
  - **RepresentationModel**: 모델/DTO 는 이 클래스를 확장하여 링크를 수집할 수 있음.
  - **EntityModel**: RepresentationModel 을 확장하고 그 안에 있는 도메인 객체(즉, 모델) content private 필드로 매핑함. 따라서 도메인 모델/DTO 및 링크를 포함함.
  - **CollectionModel**: RepresentationModel 도 확장함. 모든 컬렉션을 래핑하고 링크를 유지하고 관리하고 저장하는 방법 제공
  - **PageModel**: CollectionModel 을 확장하고 getNextLink() 및 getPreviousLink() 와 같은 페이지와 getTotalPages() 를 사용하여 페이지 메타데이터를 통해 반복하는 방법 제공

## HATEOAS 기본 방법

- 다음 코드와 같이 도메인 모델로 RepresentationModel 확장
  - 아래 예제 소스는 본 프로젝트를 build 후에 생성됨.
  
```java
@Schema(name = "Cart", description = "Shopping Cart of the user")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class Cart extends RepresentationModel<Cart>  implements Serializable {

  private static final long serialVersionUID = 1L;

  @JsonProperty("id")
  private String id;

  @JsonProperty("customerId")
  private String customerId;

  @JsonProperty("items")
  @Valid
  private List<Item> items = null;
  
  // ...
```

- RepresentationModel 을 확장하면 getLink(), hasLink(), add() 를 포함한 추가 메소드로 모델이 강화됨.
- 이러한 모든 모델은 Swagger Codegen 에서 생성 가능.
  - [config.json 참조](https://github.com/starseat/SpringModernAPI_2025/blob/main/src/main/resources/api/config.json)
  - `"hateoas": true` 로 하면 RepresentationModel 클래스를 확장하는 모델이 자동 생성됨.
<br>
- `config.json` 및 관련 처리 후 링크가 적절한 URL 로 매핑되도록 후 처리 필요.
  - `RepresentationModelAssemblerSupport` 추상 클래스 확장 필요
  - [CartRepresentationModelAssembler.java 참조](https://github.com/starseat/SpringModernAPI_2025/blob/main/src/main/com/packt/modern/api/hateoas/CartRepresentationModelAssembler.java)
    - toMapper() 메소드를 재정의(overriding) 하는 부분 확인
    - 생성자에서 super() 호출하여 Cart 모들과 함께 CartController 를 Rep 에 전달
    - 어블러는 이를 통해 앞에서 공유한 methodOn 메소드에 필요한 적절한 링크를 생성함. (이런 방법으로 링크 자동 생성)
<br>
- 이후 Controller 에 RepresentationModelAssembler 를 추가하여 하이퍼미디어 링크를 포함하도록 확장
- 예: [CartController.java](https://github.com/starseat/SpringModernAPI_2025/blob/main/src/main/java/com/packt/modern/api/controllers/CartsController.java)

```java
@RestController
public class CartsController implements CartApi {

  private static final Logger log = LoggerFactory.getLogger(CartsController.class);
  private final CartService service;
  private final CartRepresentationModelAssembler assembler;

  public CartsController(CartService service, CartRepresentationModelAssembler assembler) {
    this.service = service;
    this.assembler = assembler;
  }
  
  // ...

  @Override
  public ResponseEntity<Cart> getCartByCustomerId(String customerId) {
    return ok(assembler.toModel(service.getCartByCustomerId(customerId)));
  }
```

## HATEOAS 수동 링크 생성

- 다음과 같이 수동으로 자체 링크 생성 가능

```java
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

// ...
responseModel.setSelf(linkTo(methodOn(CartController.class)
.getItemsByUserId(userId, item)).withSelfRel())
```

- 위 예제 소스케서 responseModel 은 API 가 반환하는 모델 객체임.
- responseModel 에는 linkTo 와 methodOn 정적 메소드를 사용해 설정되는 _self 라는 필드가 있음.
- linkTo 와 methodOn 메소드는 스프링 HATEOAS 라이브러리가 제공하며 이를 통해 주어진 컨트롤러 메소드에 대한 자체 링크를 생성함.
<br>
- 모델에 링크를 추가하는 작업은 스프링 HATEOAS 의 RepresentationModelAssembler 인터페이스를 사용하여 자동으로 수행될 수도 있음.
- 이 인터페이스는 주로 주어진 Entity 를 Model 과 CollectionModel 로 변환하는 toModel(T model) 와 toCollectionModel(Iterable<? extents T> entities) 의 두가지 메소드가 있음.


# 추가 및 참조

- [Spring HATEOAS - Reference Documentation](https://docs.spring.io/spring-hateoas/docs/current/reference/html/)
- [[Spring] Hateoas(헤이티오스)란](https://peterica.tistory.com/402)