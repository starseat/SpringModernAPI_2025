# async

[Project Reactor](https://projectreactor.io/) 를 기반으로 하는 **스프링 윀플럭스(Spring WebFlux)** 사용

## Project Reactor

- **리액티브 스트림(Reactive Streams)** 기반이고, 리액티브 스트림은 데이터 소스인 발행자(publisher) 가 구독자(subscriber) 에게 데이터를 푸시하는 **발행자-구독자 모델(publisher-subscriber model)** 임.
- Reactive API 는 이벤트 루프 설계를 기반으로 한 푸시 스타일 알림 사용
- 이외 Reactive Streams 는 세부적으로 map, flatMap, filter 와 같은 자바 스트림(Java Streams) 오퍼레이션도 지원
  - 내부적으로 Reactive Streams 은 푸시 스타일을 사용하는 반면, Java Streams 은 풀 모델(pull model) 에서 작동함.
  - 즉, 컬렉션(Collection) 과 같은 소스의 항목을 가져옴.
- 리액티브에서는 소스(발행자)는 데이터를 푸시할 뿐임.
<br>
- 리액티브 스트림에서 데이터 스트림은 비동기적이고 논-블로킹이며 백프레셔(back-pressure)를 지원
- 리액티브 스트림 명세에 따라 네 가지 기본 타입이 있음.
  - 발행자(Publisher)
  - 구독자(Subscriber)
  - 구독자(Subscriber)
  - 프로세서(Processor)

### 발행자(Publisher)

- 발행자는 한 명 이상의 구독자에게 데이터 스트림을 제공
- 구독자는 subscribe() 메소드를 사용하여 발행자를 구독
- 각 구독자는 발행자를 한 번만 구독할 수 있음.
- 가장 중요한 것은 발행자가 구독자의 요청에 따라 데이터를 푸시한다는 것임.
  - 리액티브 스트림은 게으르며(Lazy), 구독자가 있는 경우에만 요소를 푸시함.
<br>
- 정의 예시

```java
package org.reactivestreams;

// T : 발행자가 전송하는 요소의 타입
public interface Publisher<T> {
  public void subscribe(Subscriber<? super T> s); // Subscriber 는 아래 정의 예시인 인터페이스
}
```

### 구독자(Subscriber)

구독자는 발행자가 푸시한 데이터를 사용함.

#### 발행자와 구독자 사이의 통신 방법

##### 통신 방법 1

Subscriber 인터페이스가 Publisher.subscribe() 메소드에 전달되면 onSubscribe() 메소드를 트리거 한다.
여기에는 백프레셔, 즉 구독자가 발행자에게 요구하는 데이터의 양을 제어하는 Subscription 이라는 매개변수를 포함한다.

##### 통신 방법 2

 첫 번째 단계 후 Publisher 는 Subscription.request(long) 호출을 기다린다.
Publisher 는 Subscription.request() 호출이 이루어진 후에만 데이터를 Subscriber 에게 푸시한다.
이 메소드를 사용할 때는 Publisher 의 요소(element) 개수가 필요한다.
<br>
 일반적으로 발행자는 구독자가 데이터를 안전하게 처리할 수 있는지 여부와 상관없이 데이터를 구독자에게 푸시한다.
그러나 구독자는 자신이 안전하게 처리할 수 있는 데이터의 양을 알고 있다. 따라서 리액티브 스트림에서 Subscriber 는 Subscription 인스턴스를 사용하여 Publisher 에게 원하는 요소의 개수를 전달한다.
이 과정을 **백프레셔(back-pressure)** 또는 **흐름 제어(flow control)** 이라고 부른다.
<br>
 Publisher 가 Subscriber 에게 속도를 줄이도록 요청했지만 Subscriber 가 속도를 늦출 수 없게 되면 Publisher 는 실패(fail), 삭제(drop) 또는 버퍼링(buffer) 여부를 결정해야 한다.

##### 통신 방법 3

 *2단계*를 사용하여 요청이 이루어지면 Publisher 는 데이터 알림을 보내고 데이터를 소비하기 위해 onNext() 메소드가 사용된다.
Subscription.request() 에 의해 전달된 요구(demand) 에 따라 Publisher 가 알림을 푸시할 때 까지 트리거 된다.

##### 통신 방법 4

 마지막으로 onError() 또는 onCompletion() 이 끝내는 상태(terminal state) 로 트리거 된다.
이러한 호출 중 하나다 트리거 된 후에는 Subscription.request() 를 호출해도 알림이 전송되지 않는다.
그리고는 끝내는 메소드(terminal method) 가 호출된다.

- onError() 는 에러가 발생하는 순간 호출된다.
- 모든 요소는 푸시되면 onCompletion() 이 호출된다.
  
<br>

- 정의 예시

```java
package org.reactivestreams;

// T : 발행자가 전송하는 요소의 타입
public interface Subscriber<T> {
  public void onSubscribe(Subscription s);
  public void onNext(T t);
  public void onError(Throwable t);
  public void onComplete();
}
```

### 구독자(Subscriber)

- 구독은 발행자와 구독자 사이의 중재자(mediator) 임.
- **Subscription.subscriber()** 메소드를 호출하고 발행자에게 요구(demand)를 알리는 것은 구독자의 책임임.
- 구독자는 필요할 때 Subscription.subscriber() 메소드를 호출할 수 있음.
- cancel() 메소드는 발행자에게 데이터 알림 전송을 중지하고 리소스를 정리하도록 요청
<br>
- 정의 예시

```java
package org.reactivestreams;

public interface Subscription {
  public void request(long n);
  public void cancel();
}
```

### 프로세서(Processor)

- 프로세서는 발행자와 구독자 사이에서 다리 역할을 하며 프로세싱 단계(processing state)를 나타냄.
- 발행자와 구독자 모두에게 작동
- 각 인터페이스에 정의한 컨트랙트(contract)를 따름
  <br>
- 정의 예시

```java
package org.reactivestreams;

public interface Processor<T, R> extends Subscriber<T>, Publisher<R> {
}
```

<br>

- 사용 예시
  - Flux.just() 정적 메소드를 사용하여 Flux 생성
  - Flux 는 프로젝트 리액터(Project Reactor) 의 발행자 타입
  
```Java
Flux<Integer> fluxInt = Flux.just(1, 10, 100, 1000).log();
fluxInt.reduce(Integer::sum).subscribe(sum -> System.out.println("Sum is: %d", sum));
```

- 로그 예시

```log
[main] INFO reactor.Flux.Array.1 - | onSubscribe([Synchronous Fuseable] FluxArray.ArraySubscription)
[main] INFO reactor.Flux.Array.1 - | request(unbounded)
[main] INFO reactor.Flux.Array.1 - | onNext(1)
[main] INFO reactor.Flux.Array.1 - | onNext(10)
[main] INFO reactor.Flux.Array.1 - | onNext(100)
[main] INFO reactor.Flux.Array.1 - | onNext(1000)
[main] INFO reactor.Flux.Array.1 - | onComplete()
Sum is: 1111
```

## 스프링 웹플렉스(Spring WebFlux)

- 서블릿 3.0 컨테이너는 기반이 되는 이벤트 루프를 발전시켜 사용
  - 비동기 요청은 비동기적으로 처리되지만 읽기 및 쓰기 작업은 여전이 블로킹 입/출력 스트림 사용
- 서블릿 3.1 컨테이너는 비동기성을 지원하며 논-블로킹 I/O 스트림 API 제공
- 스프링 웹플럭스는 다음 기능과 아키타입(archetype) 지원
  - 이벤트 루프 동시성 모델
  - 애노테이션이 달린 컨트롤러와 함수형 엔드포인트
  - 리액티브 클라이언트
  - Tomcat, Undertow 및 Jetty 와 같은 Netty 및 서블릿 3.1 컨테이너 기반 웹 서버

### 리액티브 API

- 스프링 웹플럭스 API 는 리액티브 API 이며 발행자를 일반 입력으로 허용
- 웹플럭스는 입력받은 발행자를 리액터 코어 또는 RxJava 와 같은 리액티브 라이브러리에서 지원하는 타입에 맞게 조정
- 지원하는 리액티브 라이브러리 타입에 따라 입력을 처리하고 출력을 반환함.
- 웹플럭스 API 는 이를 통해 다른 리액티브 라이브러리와 상호 운용이 가능해짐.
<br>
- 스프링 웹플럭스는 기본 핵심 의존성으로 [리액터](https://projectreactor.io/) 를 사용
- 프로젝트 리액터는 리액티브 스트림 라이브러리를 제공함.
- 웹플럭스 발행자의 입력을 수락한 다음 리액터 타입에 적용하고, Mono 또는 Flux 출력으로 반환하는 방식으로 작동
<br>
- 리액티브 스트림의 발행자는 구독자가 요청한 데이터를 푸시할 때 하나 이상의(무한한) 요소를 푸시할 수 있음.
- 프로젝트 리액터는 더 나아가 **Mono** 와 **Flux** 라는 두 가지 발행자 구현을 제공
  - **Mono** 는 구독자에게 0 또는 1 반환
  - **Flux** 는 0~N 까지의 요소 반환
  - 둘 다 CorePublisher 인터페이스를 구현하는 추상 클래스임.
  - CorePublisher 인터페이스를 발행자를 확장함.
- **Mono, Flux** 예시

```java
// public Product findById(UUID id);
public Mono<Product> findById(UUID id);

// public List<Product> getAll();
public Flux<Product> getAll();
```

<br>

- 스트림은 소스가 재시작될 수 있는지 여부에 따라 핫(hot) 스트림 또는 콜드(cold) 스트림으로 분류될 수 있음.
  - Cold 발행자는 각각의 구독에 대해 새로운 데이터를 생성하는 구조
  - Hot 발행자는 구독자의 상태에 상관없이 데이터를 지속적으로 제공
- 콜드 스트림에 대한 구독자가 여려명 있고 핫 스트림의 여러 구독자에 대해 동일한 소스가 사용되는 경우 소스는 다시 시작됨.
- 프로젝트 리액터의 스트림은 기본적으로 콜드임.
- 따라서 스트림을 사용하면 다시 시작할 때 까지 재사용 할 수 없음.
  - 그러나 프로젝트 리액터에서 cache() 메소드를 사용하면 콜드 스트림을 핫 스트림으로 전환할 수 있음.
- 이 두가지 방법은 Mono 및 Flux 추상 클래스에서 모두 사용

####  콜드 스트림과 핫 스트림 예시

- 합계와 최대라는 두 개의 오퍼레이션 별도 수행
- 이 경우 구독자는 2개
- 프로젝트 리액터의 기본 스트림은 콜드 임을 생각하기

```java
Flux<Integer> fluxInt = Flux.just(1, 10, 100).log();
fluxInt.reduce(Integer::sum).subscribe(sum -> System.out.println("Sum is: %d", sum));
fluxInt.reduce(Integer::max).subscribe(max -> System.out.println("Maximum is: %d", max));
```

```log
[main] INFO reactor.Flux.Array.1 - | onSubscribe([Synchronous Fuseable] FluxArray.ArraySubscription)
[main] INFO reactor.Flux.Array.1 - | request(unbounded)
[main] INFO reactor.Flux.Array.1 - | onNext(1)
[main] INFO reactor.Flux.Array.1 - | onNext(10)
[main] INFO reactor.Flux.Array.1 - | onNext(100)
[main] INFO reactor.Flux.Array.1 - | onComplete()
Sum is: 111
[main] INFO reactor.Flux.Array.1 - | onSubscribe([Synchronous Fuseable] FluxArray.ArraySubscription)
[main] INFO reactor.Flux.Array.1 - | request(unbounded)
[main] INFO reactor.Flux.Array.1 - | onNext(1)
[main] INFO reactor.Flux.Array.1 - | onNext(10)
[main] INFO reactor.Flux.Array.1 - | onNext(100)
[main] INFO reactor.Flux.Array.1 - | onComplete()
Maximum is: 100
```

HTTP 요청 처럼 소스가 다른 곳에 있거나 소스를 다시 시작하고 싶지 않을 경우 **cache()** 를 사용하여 콜드 스트림을 핫 스트림으로 전환할 수 있음.
(Flux.just() 에 chche() 호출 추가)

```java
Flux<Integer> fluxInt = Flux.just(1, 10, 100).log().cache();
fluxInt.reduce(Integer::sum).subscribe(sum -> System.out.println("Sum is: %d", sum));
fluxInt.reduce(Integer::max).subscribe(max -> System.out.println("Maximum is: %d", max));
```

```log
[main] INFO reactor.Flux.Array.1 - | onSubscribe([Synchronous Fuseable] FluxArray.ArraySubscription)
[main] INFO reactor.Flux.Array.1 - | request(unbounded)
[main] INFO reactor.Flux.Array.1 - | onNext(1)
[main] INFO reactor.Flux.Array.1 - | onNext(10)
[main] INFO reactor.Flux.Array.1 - | onNext(100)
[main] INFO reactor.Flux.Array.1 - | onComplete()
Sum is: 111
Maximum is: 100
```

### 리액티브 코어

- 스프링으로 리액티브 웹 애플리케이션을 개발하기 위환 기초 제공
- 웹플럭스 애플리케이션 설정에는 webHandler(DisatcherHandler), WebFilter, WebExceptionHandler, HandlerMapping, HandlerAdapter, HandlerResultHandler 와 같은 bean 도 포함됨.
- 리액티브 코어에는 REST 서비스 구현을 위한 Tomcat, Jetty, Netty, Undertow 웹 서버에 대한 특정 HandlerAdapter 인스턴스가 있음.
- Mono/Flux 스트림은 웹플럭스 내부 클래스에 의해 구독되고 컨트롤러가 Mono/Flux 스트림을 보낼 때 이 클래스는 해당 내용을 HTTP 패킷으로 변환함.
- HTTP 프로토콜은 이벤트 스트림을 지원
  - 그러나 JSON 등의 다른 미디어 타입일 때는 스프링 웹플럭스는 Mono/Flux 스트림을 구독하고 onComplete() 또는 onError() 가 트리거될 때까지 기다림.
  - 그럼 다음 하나의 HTTP 응답에서 전체 요소 목록 또는 Mono 의 경우 단일 요소를 직렬화 함. (serialize)

#### HTTP 웹 요청을 처리하기 위한 지원

- 서버에 의한 웹 요청 처리
  - HttpHandler: reactor.core.publisher.Mono 패키지의 인터페이스는 다양한 HTTP 서버 API (예: Netty 또는 Tomcat) 위에 요청/응답 핸들러의 추상화 제공
```java
public interface HttpHandler {
  Mono<Void> handle(ServerHttpRequest request, ServerHttpResponse response);
}
 ```
  - [WebHandler](https://docs.spring.io/spring-framework/reference/web/webflux/dispatcher-handler.html): org.springframework.web.server 패키지의 인터페이스는 사용자 세션, 요청 및 세션 속성, 요청에 대한 로케일과 주체, 폼 데이터 등 지원
  
- WebClient 를 이요항 클라이언트의 웹 요청 호출 처리
- 요청 및 응답에 대해 서버 및 클라이언트 수준 모두에서 콘텐츠 직렬화 및 역직렬화를 하기 위한 코덱(Encoder, Decoder, HttpMessageWriter, HttpMessageReader, DataBuffer)

## DispatcherHandler

- 스프링 웹플럭스의 Front Controller 인 DispatcherHandler 는 스프링 MVC 프레임워크의 DispatcherServlet 임.
- 요청을 처리하기 위해 특별한 컴포넌트를 사용하는 알고리즘을 포함하여, 그 특별한 컴포넌트는 HandlerMapping(요청을 핸들러에 매핑), HandlerAdapter(요청에 매핑된 핸들러를 호출하는 DispatcherHandler 헬퍼) 및 HandlerResultHandler 와 같음.
- DispatcherHandler 컴포넌트는 webHandler 라는 bean 으로 식별됨.

### DispatcherHandler 요청 처리 순서

- 1. DispatcherHandler 가 웹 요청 받음.
- 2. DispatcherHandler 는 HandlerMapping 을 사용해 요청에 일치하는 핸들러를 찾고 첫번째 일치하는 것을 사용
- 3. 각 HandlerAdapter 를 사용해 요청을 처리하고 HandlerResult(처리 후 반환 값) 를 노출
  - 반환 값은 ResponseEntity, ServerResponse, @RestController 에서 반환된 값 또는 View Resolver 에서 됨.
- 4. 각 HandlerResultHandler 를 사용해 2단계에서 수신한 HandlerResult 타입을 기반으로 응답을 작성하거나 뷰를 렌더링
  - ResponseEntityResultHandler 는 ResponseEntity 에 사용
  - ServerResponseResultHandler 는 ServerResponse 에 사용
  - ResponseBodyResultHandler 는 @RestController 애노테이션이 달린 메소드 에 사용
  - ViewResolutionRersultHandler 는 View Resolver 가 반환한 값에 사용
- 5. 요청 완료

### 컨트롤러

- 스프링 재단은 애노테이션이 논-플로킹 방식이기 때문에 스프링 MVC 와 스프링 웹플럭스에 동일한 애노테이션을 유지함.

- REST Controller 예시

```java
@RestController
public class OrderController {
  @RequestMapping(value = "/api/v1/orders", method = RequestMEthod.POST)
  public ResponseENtity<Order> addOrder(@RequestBody NewOrder newOrder) {
    // ...
  }
  
  @RequestMapping(value = "/api/v1/orders/{id}", method = RequestMethod.GET)
  public ResponseEntity<Order> getOrderById(@PathVariable("id") String id) {
    // ...
  }
}
```

### 함수형 엔드포인트

- 리액티브 프로그래밍은 대개 함수형 프로그래밍 스타일을 따름.
- 스프링 웹플럭스는 동일한 리액티브 코어 기반을 사용하면서도 함수형 엔드포인터 (Functional endpoints) 를 사용하여 REST 엔드포인트를 정의하는 대체 방법을 허용함.

- 함수형 엔드포인트 예시
  - RouterFunctions.route() 빌더는 함수형 프로그래밍 스타일로 단일 명령문에서 모든 REST 경로를 작성할 수 있게 하는 메소드임.
  - 이 후 핸들러 클래스의 메소드 참조를 사용해 요청을 처리하는 부분은 애노테이션 기반 모델의 @RequestMapping 본문과 동일

```java
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

// ...

OrderRepository repository = // ...
OrderHandler handler = new OrderHandler(repository);
RouterFunction<ServerResponse> route = route()
        .GET("/v1/api/orders/{id}", accept(APPLICATION_JSON), handler::getOrderById)
        .POST("/v1/api/orders", handler::addOrder)
        .build();
public class OrderHandler {
  public Mono<ServerResponse> addOrder(ServerRequest req) {
    // ...
  }
  
  public Mono<ServerResponse> getOrderById(ServerRequest req) {
    // ...
  }
}

```

```java
public class OrderHandler {
  public Mono<ServerResponse> addOrder(ServerRequest req) {
    Mono<NewOrder> order = req.bodyToMono(NewOrder.class);
    return ok().build(repository.save(toEntity(order)));
  }
  
  public Mono<ServerResponse> getOrderById(ServerRequest req) {
    String orderId = req.pathVariable("id");
    return respository.getOrderById(UUID.fromString(orderId))
            .flatMap(order -> ok()
                    .contentType(APPLICATION_JSON).bodyValue(toModel(order)))
            .switchIfEmpty(ServerResponse.notFOund().build());
  }
}
```

- 요청 부분은 ServerRequest 매개변수 사용
  - addOrder() 메소드에서 Order 객체는 req.bodyToMono() 를 사용해 추출되며, 이 메소드는 요청 본문을 구문 분석해 Order 객체로 변환함.
  - getOrderById() 메소드는 서버 요청 객체에서 req.pathVariable("id") 을 호출하여 주어진 ID 로 식별된 Order 객체로 추출함.
- 응답 부분은 ResponseEntity 와 유사한 ServerResponse 객체를 사용함.
  - ok() 정적 메소드는 ResponseEntity 가 아닌 org.springframework.web.reactive.function.server.ServerResponse.ok 임.
  - 핸들러 메소드 구현 시에는 명령형 스타일이 아닌 함수형 스타일을 사용하며 리액티브 체인이 끊어지지 않도록 해야 함.
  - Repository 는 두 경우 모두 Mono 객체(발행자) 를 반환하고 이는 ServerResponse 내부에 래핑된 응답으로 반환됨.

### 컨트롤러에 대한 전역 예외 처리

- 스프링 웹플럭스에서는 에러를 처리하기 위해 `@ControllerAdvice` 가 아닌 `ErrorAttributes` 를 사용
  - 예제에서는 `ApiErrorAttrubite` 클래스 사용. (`DefaultErrorAttributes` 인터페이스 확정)
  - `DefaultErrorAttributes` 속성
    - timestamp: 에러가 캡쳐된 시간
    - status: 상태 코드
    - error: 에러 설명
    - exception: 루트 예외의 클래스 이름(설정된 경우)
    - message: 예외 메시지(설정된 경우)
    - errors: BindingResult 예외의 모든 ObjectErrors(설정된 경우)
    - trace: 예외 스택 추적(설정된 경우)
    - path: 예외가 발생한 경로
    - requestId: 현재 요청과 연결된 고유 ID

- `ApiErrorAttrubite` 를 사용하기 위해 `ApiErrorWebExceptionHandler` 생성(`AbstractErrorWebExceptionHandler` 확장)
  - `ApiErrorWebExceptionHandler` 의 `@Order(-2)` 는 아래 내용에 따른 우선순위 지정
    - `ResponseStatusExceptionHandler` 는 스프링 프레임워크에 의해 0 으로 정렬됨.
    - `DefaultErrorWebExceptionHandler` 는 -1 로 정렬됨.
    - 이 두 가지보다 우선 순위를 지정하지 않으면 실행되지 않음.


### R2DBC

- MongoDB 와 같은 많은 NoSQL 데이터베이스는 이미 리액티브 데이터베이스 드라이버를 제공
- R2DBC(Reactive Relational Database Connectivity) 기반 드라이버는 완전한 논-블로킹/리액티브 API 호출을 위해 JDBC 대신 관계형 데이터베이스에 사용해야 함. 
  - H2, Oracle Database, MySQL, MariaDB, SQL Server, PostgreSQL 등 거의 모든 인기있는 관계형 데이터베이스는 R2DBC 드라이버를 지원.
- 스프링 데이터 R2DBC 는 ReactiveCrudRepository, ReactiveSortingRepository, RxJava2CrudRepository, RxJava3CrudRepository 와 같은 Reactor 및 RxJava 를 위한 다양한 리파지토리 제공
  - 예: OrderRepository.java


# 참고

- [Project Reactor](https://projectreactor.io/)
- [프로젝트 리액터 고급 활용](https://devsh.tistory.com/entry/프로젝트-리액터-고급-활용)
- [Hot vs Cold Publisher](https://p-bear.tistory.com/80)
- [스프링 리액티브](https://docs.spring.io/spring-boot/reference/web/reactive.html)
- [스프링 데이터 R2DBC](https://spring.io/projects/spring-data-r2dbc)
