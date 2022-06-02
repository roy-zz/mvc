이번 장에서는 서블릿을 통해서 예외 처리를 하는 방법에 대해서 알아본다.
모든 코드는 [깃허브(링크)](https://github.com/roy-zz/mvc) 에 올려두었다.

---

### 개요

스프링을 사용하지 않은 순수 서블릿 컨테이너는 아래와 같은 2가지 방식으로 예외 처리를 지원한다.
  
- Exception(예외)
- response.sendError(HTTP status code, Error Message)

#### Exception

**자바**
  
자바의 메인 메서드를 직접 실행하는 경우에 `main`이라는 이름을 가진 스레드가 실행된다.  
실행 도중에 예외를 처리하지 않아서 처음에 실행된 `main` 스레드까지 예외가 전달되면 발생한 예외에 대한 정보를 출력하고 스레드는 종료된다.
  
**웹 애플리케이션**
  
웹 애플리케이션은 사용자의 요청별로 스레드풀에 있는 스레드가 할당되고 서블릿 컨테이너 안에서 실행된다.  
애플리케이션에서 예외가 발생하는 경우 `try & catch`를 사용하여 예외를 처리하면 문제가 발생하지 않지만 만약 개발자가 예상하지 못한 예외가 서블릿의 외부로 전달되면 아래와 같은 방식으로 동작한다.
  
```
WAS <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러(예외의 시작)
```
  
컨트롤러에서 발생한 예외가 인터셉터, 서블릿, 필터를 거쳐서 WAS(일반적으로 톰캣)까지 전달된다.
  
#### 예외 처리 확인

예외가 `WAS`까지 전달되는 경우 어떻게 동작하는지 테스트를 해본다.  
스프링 부트에서 기본으로 제공하는 예외 페이지를 비활성화 하기 위해서 아래와 같이 `application.properties` 파일을 수정한다.
  
```properties
server.error.whitelabel.enabled=false
```

호출되는 경우 바로 `RuntimeException`을 발생시키는 컨트롤러를 생성한다.

```java
@Controller
public class ServletExceptionController {
    @GetMapping("/cause-exception")
    public void causeException() {
        throw new RuntimeException("강제로 발생시킨 예외");
    }
}
```

서버를 실행시키고 위에서 만든 주소로 접속하면 









---

**참고한 강의**:
- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%ED%95%B5%EC%8B%AC-%EC%9B%90%EB%A6%AC-%EA%B8%B0%EB%B3%B8%ED%8E%B8
- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-1
- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-2

**참고한 문서**:
- [Thymeleaf 기본 메뉴얼](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html)
- [Thymeleaf 스프링 통합 메뉴얼](https://www.thymeleaf.org/doc/tutorials/3.0/thymeleafspring.html)