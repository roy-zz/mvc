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












---

**참고한 강의**:

- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%ED%95%B5%EC%8B%AC-%EC%9B%90%EB%A6%AC-%EA%B8%B0%EB%B3%B8%ED%8E%B8

- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-1

**Spring 공식문서**:

- https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#spring-web