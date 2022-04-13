이번 장에서는 스프링 MVC의 HTTP Request의 기본적인 사용법에 대해서 알아본다.
글의 하단부에 참고한 강의와 공식문서의 경로를 첨부하였으므로 자세한 내용은 강의나 공식문서에서 확인한다.
모든 코드는 [깃허브(링크)](https://github.com/roy-zz/mvc)에 올려두었다.

---

### 헤더 조회

아래와 같은 방법으로 @RequestHeader 애노테이션을 사용하여 헤더의 정보를 조회할 수 있다.

```java
@Slf4j
@RestController
@RequestMapping(value = "/basic-request")
public class BasicRequestController {
    @GetMapping(value = "/header")
    public String headers(
            HttpServletRequest request, HttpServletResponse response,
            HttpMethod httpMethod, Locale locale,
            @RequestHeader MultiValueMap<String, String> headerMap,
            @RequestHeader("host") String host,
            @CookieValue(value = "cookieRun", required = false) String cookie
    ) {
        log.info("request: {}", request);
        log.info("response: {}", response);
        log.info("httpMethod: {}", httpMethod);
        log.info("locale: {}", locale);
        log.info("header: {}", headerMap);
        log.info("host: {}", host);
        log.info("cookieRun: {}", cookie);
        return "OK";
    }
}
```

출력 결과는 아래와 같다.

```bash
request: org.apache.catalina.connector.RequestFacade@6363e28b
response: org.apache.catalina.connector.ResponseFacade@70c0f6
httpMethod: GET
locale: en_KR
header: {user-agent=[PostmanRuntime/7.29.0], accept=[*/*], postman-token=[6c639112-e59a-4658-8cc9-f40b65f159c4], host=[localhost:8080], accept-encoding=[gzip, deflate, br], connection=[keep-alive], cookie=[cookieRun=Cookie is running]}
host: localhost:8080
cookieRun: null
```

위에서 조회한 정보 이외에도 획득할 수 있는 정보가 많이 있다.
우리가 필요로 하는 대부분의 정보는 이미 구현되어 있으므로 필요한 정보가 있다면 공식문서를 먼저 검색해보도록 한다.

**파라미터 목록(공식문서)**: https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-ann-
arguments

**응답 값 목록(공식문서)**: https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-ann-
return-types

---

### 요청 파라미터(쿼리 파라미터, HTML Form)

클라이언트에서 서버로 데이터를 전송하는 방법은 주로 세가지 방법 중에 선택된다.

**쿼리 파라미터(GET)**
**HTML Form(POST)**
**HTTP Message Body(POST, PUT, PATCH)**

---

#### 쿼리 파라미터 & HTML Form

두 방식은 거의 동일하므로 한 번에 테스트한다.
필자의 경우 html파일을 만들지 않고 전부 Postman으로 테스트를 진행할 예정이다.
Postman을 설정하는 방법은 아래와 같다.

**쿼리 파라미터로 전송하는 경우**

![](image/query-parameter.png)

Params탭으로 이동하여 원하는 Key와 Value를 입력한다.
어떤 방식으로 요청이 가는지 궁금하다면 우측의 Code를 눌러서 확인해보도록 한다.

**Form-data로 전송하는 경우**

![](image/my-mvc-diagram.png)

Body탭으로 이동하여 form-data를 선택하고 원하는 Key와 Value를 입력한다.
어떤 방식으로 요청이 가는지 궁금하다면 우측의 Code를 눌러서 확인해보도록 한다.


**Version1**: HttpServletRequest와 HttpServletResponse를 활용한다.
반환 타입이 없으면서 직접 response 객체에 값을 넣어주면 View를 조회하지 않는다.

```java
@Slf4j
@RestController
@RequestMapping(value = "/basic-request")
public class BasicRequestController {
    @GetMapping(value = "/request-param", headers = "X-API-VERSION=1.0")
    public void requestParamV1(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));
        log.info("username: {}, age: {}", username, age);
        response.getWriter().write("OK");
    }
}
```

**Version2**: @RequestParam의 name속성의 값과 파라미터의 Key를 바인딩한다.
@RequestBody를 추가하여 View 조회를 무시하고 결과 값을 HTTP Message Body에 넣는다.

```java
@Slf4j
@RestController
@RequestMapping(value = "/basic-request")
public class BasicRequestController {
    @ResponseBody
    @RequestMapping(value = "/request-param", headers = "X-API-VERSION=2.0")
    public String requestParamV2(
            @RequestParam("username") String memberName,
            @RequestParam("age") int memberAge
    ) {
        log.info("username: {}, age: {}", memberName, memberAge);
        return "OK";
    }
}
```

**Version3**: @RequestParam을 사용하고 HTTP 파라미터 이름의 변수와 자바 변수의 이름을 맞추어 @RequestParam의 name 속성을 생략한다.

```java
@Slf4j
@RestController
@RequestMapping(value = "/basic-request")
public class BasicRequestController {
    @ResponseBody
    @RequestMapping(value = "/request-param", headers = "X-API-VERSION=3.0")
    public String requestParamV3(
            @RequestParam String username,
            @RequestParam int age
    ) {
        log.info("username: {}, age: {}", username, age);
        return "OK";
    }
}
```

**Version4**: Primitive 타입이나, Primitive 타입을 감싸고 있는 Wrapper 클래스라면 @RequestParam을 생략해도 변수명만 동일하다면 바인딩이 가능하다.
직관적이지 못하기 때문에 사용이 추천되지 않는다.

```java
@Slf4j
@RestController
@RequestMapping(value = "/basic-request")
public class BasicRequestController {
    @ResponseBody
    @RequestMapping(value = "/request-param", headers = "X-API-VERSION=4.0")
    public String requestParamV4(String username, int age) {
        log.info("username: {}, age: {}", username, age);
        return "OK";
    }
}
```

---





---

**참고한 강의**:

- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%ED%95%B5%EC%8B%AC-%EC%9B%90%EB%A6%AC-%EA%B8%B0%EB%B3%B8%ED%8E%B8

- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-1

**Spring 공식문서**:

- https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#spring-web