이번 장에서는 [자바 서블릿(링크)](https://imprint.tistory.com/180?category=1003393)에 이어 HttpServletRequest에 대해서 알아본다.
글의 하단부에 참고한 강의와 공식문서의 경로를 첨부하였으므로 자세한 내용은 강의나 공식문서에서 확인한다.
모든 코드는 [깃허브(링크)](https://github.com/roy-zz/mvc)에 올려두었다.

---

### HttpServletRequest 역할

서블릿은 개발자가 HTTP 요청 메시지를 편리하게 사용할 수 있도록 개발자 대신 파싱하여 HttpServletRequest 객체를 생성한다.
우리는 클라이언트가 보낸 HTTP 요청을 확인하는 것이 아니라 서블릿이 요청을 토대로 생성한 HttpServletRequest를 가지고 개발을 진행하면 된다.

**HTTP 요청 메시지**
POST /save HTTP/1.1
Host: localhost:8080
Content-Type: application/x-www-form-urlencoded

username=roy@age=32
---

첫 줄에는 Http 메소드, URL, 쿼리 스트링, 프로토콜 정보가 있다.
다음으로는 헤더 정보와 바디에 포함된 form 파라미터 형식과 message body 데이터가 있다.

이외에도 HttpServletRequest는 여러가지 기능들을 가지고 있다.

**임시 저장소 기능**
- 저장: request.setAttribute(name, value)
- 조회: request.getAttribute(name)

**세션 관리 기능**
- request.getSession(create: true)

---

### HttpServletRequest 기본 사용법

#### Start Line 조회

```java
private void printStartLine(HttpServletRequest request) {
    System.out.println("--- REQUEST-START-LINE START ---");
    System.out.printf("request.getMethod(): %s%n", request.getMethod());
    System.out.printf("request.getProtocol(): %s%n", request.getProtocol());
    System.out.printf("request.getScheme(): %s%n", request.getScheme());
    System.out.printf("request.getRequestURL(): %s%n", request.getRequestURL());
    System.out.printf("request.getRequestURI(): %s%n", request.getRequestURI());
    System.out.printf("request.getQueryString(): %s%n", request.getQueryString());
    System.out.printf("request.isSecure(): %s%n", request.isSecure());
    System.out.println("--- REQUEST-START-LINE END ---");
}
```

출력 결과는 아래와 같다.

```bash
--- REQUEST-START-LINE START ---
request.getMethod(): GET
request.getProtocol(): HTTP/1.1
request.getScheme(): http
request.getRequestURL(): http://localhost:8080/request-header
request.getRequestURI(): /request-header
request.getQueryString(): username=roy
request.isSecure(): false
--- REQUEST-START-LINE END ---
```

---

#### Header 조회

```java
private void printHeaders(HttpServletRequest request) {
    System.out.println("--- HEADERS START ---");
    request.getHeaderNames().asIterator().forEachRemaining(
            headerName -> System.out.printf("%s: %s%n", headerName, request.getHeader(headerName))
    );
    System.out.println("--- HEADERS END ---");
}
```

출력 결과는 아래와 같다.

```bash
--- HEADERS START ---
host: localhost:8080
connection: keep-alive
cache-control: max-age=0
sec-ch-ua: " Not A;Brand";v="99", "Chromium";v="98", "Google Chrome";v="98"
sec-ch-ua-mobile: ?0
sec-ch-ua-platform: "macOS"
upgrade-insecure-requests: 1
user-agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.109 Safari/537.36
accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9
sec-fetch-site: none
sec-fetch-mode: navigate
sec-fetch-user: ?1
sec-fetch-dest: document
accept-encoding: gzip, deflate, br
accept-language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7
--- HEADERS END ---
```

#### Header Utils 조회

```java
private void printHeaderUtils(HttpServletRequest request) {
    System.out.println("--- HEADER 편의 조회 START ---");
    System.out.println("[Host 편의 조회]");
    System.out.printf("request.getServerName(): %s%n", request.getServerName());
    System.out.printf("request.getServerPort(): %s%n", request.getServerPort());

    System.out.println("[Accept-Language 편의 조회]");
    request.getLocales().asIterator().forEachRemaining(
            locale -> System.out.printf("locale: %s%n", locale));
    System.out.printf("request.getLocale(): %s%n", request.getLocale());

    System.out.println("[Cookie 편의 조회]");
    if (Objects.nonNull(request.getCookies())) {
        Arrays.stream(request.getCookies()).forEach(cookie ->
            System.out.printf("%s: %s%n", cookie.getName(), cookie.getValue())
        );
    }

    System.out.println("[Content 편의 조회]");
    System.out.printf("request.getContentType(): %s%n", request.getContentType());
    System.out.printf("request.getContentLength(): %s%n", request.getContentLength());
    System.out.printf("request.getCharacterEncoding(): %s%n", request.getCharacterEncoding());
    System.out.println("--- HEADER 편의 조회 END ---");
    System.out.println();
}
```

출력 결과는 아래와 같다.

```bash
--- HEADER 편의 조회 START ---
[Host 편의 조회]
request.getServerName(): localhost
request.getServerPort(): 8080
[Accept-Language 편의 조회]
locale: ko_KR
locale: ko
locale: en_US
locale: en
request.getLocale(): ko_KR
[Cookie 편의 조회]
[Content 편의 조회]
request.getContentType(): null
request.getContentLength(): -1
request.getCharacterEncoding(): UTF-8
--- HEADER 편의 조회 END ---
```

---

#### 기타 정보 조회

기타 정보의 경우 HTTP 메시지는 아니며 WAS에서 생성한 데이터다.

```java
private void printEtc(HttpServletRequest request) {
    System.out.println("--- 기타 조회 START ---");

    System.out.println("[Remote 정보]");
    System.out.printf("request.getRemoteHost(): %s%n", request.getRemoteHost());
    System.out.printf("request.getRemoteAddr(): %s%n", request.getRemoteAddr());
    System.out.printf("request.getRemotePort(): %s%n", request.getRemotePort());
    System.out.println();

    System.out.println("[Local 정보]");
    System.out.printf("request.getLocalName(): %s%n", request.getLocalName());
    System.out.printf("request.getLocalAddr(): %s%n", request.getLocalAddr());
    System.out.printf("request.getLocalPort(): %s%n", request.getLocalPort());

    System.out.println("--- 기타 조회 END ---");
    System.out.println();
}
```

출력은 아래와 같다.

```bash
--- 기타 조회 START ---
[Remote 정보]
request.getRemoteHost(): 0:0:0:0:0:0:0:1
request.getRemoteAddr(): 0:0:0:0:0:0:0:1
request.getRemotePort(): 64872

[Local 정보]
request.getLocalName(): localhost
request.getLocalAddr(): 0:0:0:0:0:0:0:1
request.getLocalPort(): 8080
--- 기타 조회 END ---
```

---

### HTTP 요청 데이터

클라이언트에서 서버로 데이터를 전달하는 방법은 크게 세가지가 있다.

#### 쿼리 파라미터(GET)

/path?username=roy&age=20과 같이 바디에는 데이터가 없고 URL의 쿼리 파라미터에 데이터를 포함해서 전달한다.
데이터를 조회하는 조건을 전달할 때 많이 사용한다.

username=roy, age=20을 서버로 전달하는 예를 알아본다.

```java
@WebServlet(name = "requestParamServlet", urlPatterns = "/request-param")
public class RequestParamServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("[모든 파라미터 조회 - START]");
        request.getParameterNames().asIterator()
                        .forEachRemaining(name -> System.out.printf("%s: %s%n", name, request.getParameter(name)));
        System.out.println("[모든 파라미터 조회 - END]");
        System.out.println();

        System.out.println("[파라미터 이름으로 조회]");
        String username = request.getParameter("username");
        System.out.printf("request.getParameter(username): %s%n", username);
        String age = request.getParameter("age");
        System.out.printf("request.getParameter(age): %s%n", age);
        System.out.println();

        System.out.println("[Key가 중복되는 파라미터 조회]");
        System.out.println("request.getParameterValues(username)");
        String[] usernames = request.getParameterValues("username");
        Arrays.stream(usernames).forEach(name -> {
            System.out.println("name = " + name);
        });

        response.getWriter().write("ok");
    }
}
```

출력된 결과는 아래와 같다.

```bash
[모든 파라미터 조회 - START]
username: roy
age: 20
[모든 파라미터 조회 - END]

[파라미터 이름으로 조회]
request.getParameter(username): roy
request.getParameter(age): 20

[Key가 중복되는 파라미터 조회]
request.getParameterValues(username)
name = roy
name = perry
```

파라미터의 Key가 중복될 때 파라미터 이름으로 조회하면 처음으로 검색된 값을 반환한다.
Key가 중복되는 경우(물론 Key가 중복되면 Collection 타입으로 변경해야겠지만) getParameterValues로 조회해야한다. 

---

#### HTML Form(POST)

content-type: application/x-www-form-urlencoded와 같이 사용되며 메시지 바디에 쿼리 파라미터 형식으로 데이터를 전달한다.
회원가입, 상품 주문등에 주로 사용된다.

src/main/webapp/basic/hello-form.html을 아래와 같이 생성한다.

```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Test</title>
</head>
<body>
<form action="/request-param" method="post">
    username: <input type="text" name="username" /> age: <input type="text" name="age" /> <button type="submit">전송</button>
</form>
</body>
</html>
```

GET URL 쿼리 파라미터 형식은 클라이언트에서 서버로 데이터를 전달할 때 HTTP 메시지 바디를 사용하지 않기 때문에 content-type이 없다.
HTML Form 형식의 경우 HTTP 메시지 바디에 데이터가 들어가기 때문에 어떤 형식인지 content-type을 지정해야한다.
Form 데이터를 전송하는 형식을 application/x-www-form-urlencoded라고 한다.

---

#### Message Body(POST, PUT, PATCH)

HTTP 메시지 바디에 데이터를 직접 담아서 요청한다.
HTTP API에서 주로 사용되며 데이터 형식으로는 JSON이 많이 사용되며 XML과 TEXT도 사용된다.

**API 메시지 바디에 단순 텍스트 전송**

```java
@WebServlet(name = "requestBodyTextServlet", urlPatterns = "/request-body-text")
public class RequestBodyTextServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletInputStream inputStream = request.getInputStream();
        String body = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        System.out.println("body = " + body);
        response.getWriter().write("OK");
    }
}
```

inputStream은 byte 코드를 반환한다. byte 코드를 String으로 변경하려면 문자표인 Charset을 지정해주어야한다.

---

**API 메시지 바디에 JSON 데이터 전송**

```java
@WebServlet(name = "requestBodyJsonServlet", urlPatterns = "/request-body-json")
public class RequestBodyJsonServlet extends HttpServlet {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletInputStream inputStream = request.getInputStream();
        String body = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        System.out.println("body = " + body);
        DefaultData defaultData = objectMapper.readValue(body, DefaultData.class);
        System.out.println("defaultData. = " + defaultData.getUsername());
        System.out.println("defaultData.getAge() = " + defaultData.getAge());
        response.getWriter().write("OK");
    }

    @Getter
    static class DefaultData {
        private String username;
        private int age;
    }
}
```

JSON 형식의 데이터를 담을 클래스인 DefaultData 클래스를 내부 클래스로 만들었다.
새로운 클래스 파일로 생성해도 동일하게 작동한다.
JSON 결과를 파싱하는 라이브러리는 SpringMVC에서 기본으로 제공하는 Jackson 라이브러리를 사용하였다.

---

**참고한 강의**:

- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%ED%95%B5%EC%8B%AC-%EC%9B%90%EB%A6%AC-%EA%B8%B0%EB%B3%B8%ED%8E%B8

- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-1

**Spring 공식문서**:

- https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#spring-web