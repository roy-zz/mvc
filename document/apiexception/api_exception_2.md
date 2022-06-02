[이전 장(링크)](https://imprint.tistory.com/273) 에서는 서블릿을 통한 API 예외 처리 방법과 스프링 부트의 기본 API 예외 처리 방법에 대해서 알아보았다.  
이번 장에서는 스프링이 제공하는 **ExceptionResolver**를 사용하여 API 예외 처리를 하는 방법에 대해서 알아본다.  
모든 코드는 [깃허브(링크)](https://github.com/roy-zz/mvc) 에 올려두었다.  

---

### 개요

이전 장에서 `HandlerExceptionResolver`를 직접 구현하여 예외 처리를 하면서 예외 처리가 생각보다 간단하지 않다는 것을 알게 되었다.  
이번 장에서는 스프링에서 기본적으로 제공하는 `ExceptionResolver`를 사용하여 보다 간편하게 API를 예외 처리하는 방법에 대해서 알아볼 것이다.  
스프링에서 기본적으로 제공되어 `HandlerExceptionResolverComposite`에 등록되는 `ExceptionResolver` 목록은 아래와 같다.

1. ExceptionHandlerExceptionResolver: `@ExceptionHandler` 애노테이션을 처리하며 대부분의 API 예외를 처리한다.
2. ResponseStatusExceptionResolver: HTTP 상태 코드를 지정해준다.
3. DefaultHandlerExceptionResolver: 스프링 내부 기본 예외를 처리한다.

숫자가 작을수록 우선순위가 높기 때문에 우선권을 가진다.

---

### ResponseStatusExceptionResolver

`ResponseStatusExceptionResolver`는 예외에 따라서 HTTP 상태 코드를 지정해주는 역할을 하며 아래와 같은 두 가지 경우를 처리한다.

- `@ResponseStatus` 애노테이션이 달려있는 예외
- `ResponseStatusException` 예외가 발생하는 경우

테스트를 위해서 `RuntimeException`을 상속받는 `BadRequestException` 클래스를 생성한다.
  
```java
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "클라이언트의 잘못된 요청")
public class BadRequestException extends RuntimeException {
}
```
  
컨트롤러에 강제로 `BadRequestException`을 발생시키는 메서드를 추가한다.

```java
@Slf4j
@RestController
@RequestMapping("/api")
public class ApiExceptionController {
    // ...
    @GetMapping("/response-status-exception-1")
    public String responseStatusException1() {
        throw new BadRequestException();
    }
    // ...
}
```

서버를 재실행하고 새로 생성한 API를 호출해보면 `Status Code 500`이 아닌 `Status Code 400`이 발생하는 것을 확인할 수 있다.

#### 메시지 기능

메시지 & 국제화에서 확인한 것처럼 `message.properties` 파일에 미리 에러 메시지를 정해놓고 애노테이션에서 해당 메시지를 가져다 쓸 수 있다.  
`@ResponseStatus` 애노테이션이 `message.properties` 메시지를 가져다쓰도록 수정해본다.

```java
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "message.bad-request")
public class BadRequestException extends RuntimeException {
}
```

`messages.properties` 파일에 위에서 지정한 메시지 내용을 추가한다.

```properties
message.bad-request=클라이언트의 잘못된 요청입니다.
```

#### ResponseStatusException

`@ResponseStatus` 애노테이션의 경우 우리가 직접 생성한 예외에 추가가 가능하였다. 하지만 자바에서 기본으로 제공하는 예외나 라이브러리의 예외에는 추가할 수 없다는 치명적인 단점이 있다.  
또한, 애노테이션을 사용한 방식이기 때문에 조건에 따라서 동적으로 변경하는 것도 어렵다. 이러한 경우에는 `responseStatusException`을 사용한다.

```java
@Slf4j
@RestController
@RequestMapping("/api")
public class ApiExceptionController {
    // ...
    @GetMapping("/response-status-exception-2")
    public String responseStatusException2() {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "error.bad-request", new IllegalArgumentException());
    }
    // ...
}
```

---

### DefaultHandlerExceptionResolver

`DefaultHandlerExceptionResolver`는 스프링 내부에서 발생하는 스프링 예외를 처리한다.  
우리가 지금까지 사용한 기능중에서는 사용자가 입력한 파라미터가 개발자가 지정한 타입과 일치하지 않는 경우 타입 캐스팅에서 오류가 발생하기 때문에 500(Internal Server Exception)이 발생해서 서블릿 컨테이너를 통해 `WAS`까지 전달되야 한다.  
하지만 중간에서 `DefaultHandlerExceptionResolver`가 500 오류가 아닌 400(Bad Request) 오류로 변경해주기 때문에 우리는 따로 처리하지 않고 사용자에게 400 오류를 보여줄 수 있었다.  

```java
public class DefaultHandlerExceptionResolver extends AbstractHandlerExceptionResolver {
    // ...
    protected ModelAndView handleTypeMismatch(TypeMismatchException ex, HttpServletRequest request, 
                                              HttpServletResponse response, @Nullable Object handler) throws IOException {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        return new ModelAndView();
    }
    // ...
}
```

코드를 살펴보면 잘못된 파라미터 바인딩이 발생하는 경우 `DefaultHandlerExceptionResolver.handleTypeMismatch` 메서드가 호출되어 `response.sendError`를 통하여 400 오류로 변경해주는 것을 알 수 있다.
  
`ResponseStatusExceptionResolver`의 경우 직접 예외를 반환해야 하며 `DefaultHandlerExceptionResolver`의 경우 스프링 예외만 처리해주고 반환 타입이 `ModelAndView`라는 점에서 API 예외를 처리에는 적절하지 않다.  
그래서 지금붙터 살펴볼 `ExceptionHandlerExceptionResolver`가 가장 많이 사용된다.

---

### ExceptionHandlerExceptionResolver

브라우저 환경의 클라이언트에게 오류가 발생한 경우의 HTML 파일을 전달하는 것은 `BasicErrorController` 사용하는 방법이 생산성이 높다.  
하지만 API의 경우 각 시스템마다 응답 스펙이 모두 다르기 때문에 항상 확장 및 수정에 유연하게 대처할 수 있어야 한다. 또한 같은 예외라고 하더라도 컨트롤러, 또는 패키지마다 다른 응답을 내려주어야 할 수도 있다.  
이러한 단점들을 보완하고 개발자 친화적으로 만들어진 것이 `ExceptionHandlerExceptionResolver`다.
  
`ExceptionHandlerExceptionResolver`는 예외를 처리하기 위해서 `@ExceptionHandler`라는 애노테이션을 사용한다.  
스프링은 기본적으로 `ExceptionHandlerExceptionResolver`를 사용하고 있으며 등록되는 `ExceptionResolver` 중에서도 가장 우선순위가 높으며 실무에서는 대부분 이 기능을 사용한다.
  
지금부터 사용 방법에 대해서 알아본다.

#### ErrorResult

예외의 정보를 담을 `ErrorResponse` 클래스를 생성한다.

```java
@Data
@AllArgsConstructor
public class ErrorResponse {
    private String code;
    private String message;
}
```

#### ApiExceptionController2

```java
@Slf4j
@RestController
@RequestMapping("/api-2")
public class ApiExceptionController2 {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse illegalExceptionHandle(IllegalArgumentException exception) {
        log.error("call illegal exception handle", exception);
        return new ErrorResponse("ILLEGAL_ARGUMENT_EXCEPTION", exception.getMessage());
    }
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> userExceptionHandle(UserException exception) {
        log.error("call user exception handle", exception);
        ErrorResponse errorResponse = new ErrorResponse("USER_EXCEPTION", exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResponse exceptionHandle(Exception exception) {
        log.error("call exception handle", exception);
        return new ErrorResponse("EXCEPTION", exception.getMessage());
    }
}
```

`@ExceptionHandler` 애노테이션을 선언하고, 해당 컨트롤러에서 처리하고 싶은 예외를 지정해주면 되고 지정한 예외의 하위 예외까지 처리한다.  
`@ExceptionHandler` 애노테이션이 달린 메서드(이하 A)가 위치하는 컨트롤러에서 예외가 발생하면 A가 호출된다.  
예시에서 우리는 `IllegalArgumentException`이라고 정확한 타입을 지정했지만 `IllegalArgumentException`의 하위 타입까지 전부 처리가 가능하다.
  
스프링에서 대부분의 규칙이 그러하듯 정확한 것이 우선순위를 가진다.
우리가 만든 예제에서 `IllegalArgumentException`은 `RuntimeException`의 하위 클래스이며 `RuntimeException`은 `Exception`의 하위 클래스다.  
그렇기 때문에 `IllegalArgumentException`이 발생하면 `illegalExceptionHandle`메서드도 처리할 수 있고 `exceptionHandle`메서드도 처리할 수 있다.  
하지만 정확한 타입을 명시하고 있는 `illegalExceptionHandle`의 우선순위가 높기 때문에 `illegalExceptionHandle`에 의해 처리된다.
  
`userExceptionHandle` 메서드와 같이 파라미터로 예외를 받는 경우 `@ExceptionHandler`의 예외 속성을 생략할 수 있다.  
`@ExceptionHandler`의 경우 `@Controller`와 같이 많은 파라미터 응답을 지원하므로 필요한 경우 [공식문서](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-ann-exceptionhandler-args) 를 확인하도록 한다.

#### 전체적인 요청의 흐름

`IllegalArgumentException`을 처리하는 `illegalExceptionHandle` 메서드를 예시로 전체적인 요청의 흐름을 살펴본다.

1. 컨트롤러를 호출하고 `IllegalArgumentException`이 컨트롤러 밖으로 던져진다.
2. 예외가 발생한 경우에 `ExceptionResolver`가 작동하고 가장 우선순위가 높은 `ExceptionHandlerExceptionResolver`가 실행된다.
3. `ExceptionHandlerExceptionResolver`는 해당 컨트롤러에서 `IllegalArgumentException`을 처리할 수 있는 `@ExceptionHandler`가 있는지 확인한다.
4. 확인 결과 `illegalExceptionHandle` 메서드가 있으므로 해당 메서드를 실행한다. 이때 `@RestController`에서 실행되기 때문에 `@ResponseBody`가 적용되며 HTTP 컨버터가 사용되어 응답은 JSON 형태가 된다.
5. `@ResponseStatus(HttpStatus.BAD_REQUEST)`라고 직접 지정하였으므로 상태 코드는 400이 된다.

---

### @ControllerAdvice

`@ExceptionHandler` 애노테이션을 통해서 예외 처리를 깔끔하게 처리하였지만 예외 처리를 위한 코드가 컨트롤러에 섞여있다는 단점이 있다.  
그리고 위에서 살펴본 것과 같이 사용하는 경우 `@ExceptionHandler`가 달려있는 메서드의 재사용도 불가능하다.

`@ControllerAdvice`를 사용하거나 `@ResponseBody`가 추가되어 있는 `@RestControllerAdvice`를 사용하면 이러한 문제를 해결해 준다.
`@ControllerAdvice`는 대상으로 지정한 여러 컨트롤러에 `@ExceptionHandler`, `@InitBinder`기능을 부여하는 역할을 한다.

#### ExceptionControllerAdvice

`ExceptionControllerAdvice`를 생성하고 이전에 만들었던 `@ExceptionHandler` 애노테이션이 붙은 메서드를 전부 옮겨준다.

```java
@Slf4j
@RestControllerAdvice
public class ExceptionControllerAdvice {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse illegalExceptionHandle(IllegalArgumentException exception) {
        log.error("call illegal exception handle", exception);
        return new ErrorResponse("ILLEGAL_ARGUMENT_EXCEPTION", exception.getMessage());
    }
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> userExceptionHandle(UserException exception) {
        log.error("call user exception handle", exception);
        ErrorResponse errorResponse = new ErrorResponse("USER_EXCEPTION", exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResponse exceptionHandle(Exception exception) {
        log.error("call exception handle", exception);
        return new ErrorResponse("EXCEPTION", exception.getMessage());
    }
}
```

#### 적용 대상 컨트롤러 지정

만약 `@ControllerAdvice`에 대상을 지정하지 않으면 모든 컨트롤러를 대상으로 작동한다.  
아래와 같은 방법으로 적용되는 대상을 한정할 수 있다.

```java
// Target all Controllers annotated with @RestController
@ControllerAdvice(annotations = RestController.class)
public class ExampleAdvice1 {}
// Target all Controllers within specific packages
@ControllerAdvice("org.example.controllers")
public class ExampleAdvice2 {}
// Target all Controllers assignable to specific classes
@ControllerAdvice(assignableTypes = {ControllerInterface.class, AbstractController.class})
public class ExampleAdvice3 {}
```

일반적으로 두번째 예시처럼 컨트롤러가 위치한 패키지를 지정하는 방식으로 많이 사용된다.  
사용하는 시점에 필요한 옵션은 [공식문서](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-ann-controller-advice) 에서 찾아서 확인해보도록 한다.

---

**참고한 강의**:
- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%ED%95%B5%EC%8B%AC-%EC%9B%90%EB%A6%AC-%EA%B8%B0%EB%B3%B8%ED%8E%B8
- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-1
- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-2

**참고한 문서**:
- [Thymeleaf 기본 메뉴얼](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html)
- [Thymeleaf 스프링 통합 메뉴얼](https://www.thymeleaf.org/doc/tutorials/3.0/thymeleafspring.html)