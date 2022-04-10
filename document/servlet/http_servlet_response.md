이번 장에서는 [HttpServletRequest(링크)](https://imprint.tistory.com/181?category=1003393)에 이어 HttpServletResponse에 대해서 알아본다.
글의 하단부에 참고한 강의와 공식문서의 경로를 첨부하였으므로 자세한 내용은 강의나 공식문서에서 확인한다.
모든 코드는 [깃허브(링크)](https://github.com/roy-zz/mvc)에 올려두었다.

---

HttpServletRequest는 HTTP 메시지를 파싱하여 개발자가 사용하기 용이하게 변경해주었다.
HttpServletResponse는 개발자가 HTTP 응답코드를 지정하고 헤더와 바디를 생성하면 HTTP 응답 메시지 생성해준다.

---

### 기본적인 사용법

응답 메시지의 Content-Type을 지정할 수 있다.
Cookie값과 유효기간을 지정할 수 있다.
페이지를 Redirect시켜 다른 페이지로 사용자를 이동시킬 수 있다.

```java
@WebServlet(name = "responseHeaderServlet", urlPatterns = "/response-header")
public class ResponseHeaderServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setStatus(SC_OK);
        response.setHeader("Content-Type", "text/plain;charset=utf-8");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        content(response);
        cookie(response);
        redirect(response);
        PrintWriter writer = response.getWriter();
        writer.println("OK");
    }

    private void content(HttpServletResponse response) {
        response.setContentType("text/plain");
        response.setCharacterEncoding("utf-8");
    }

    private void cookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("customCookie", "cookie");
        cookie.setMaxAge(600);
        response.addCookie(cookie);
    }

    private void redirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("/basic/default-form.html");
    }
}
```

---

### 요청에 대한 단순 HTML 메시지 응답 (Text 데이터)

HTML을 응답할 때는 Content-Type을 text/html로 지정해야한다.
IntelliJ의 도움을 받아서 코드를 작성하여도 전부 String이라 오타의 여지가 있다.
이렇게 사용했던 시절이 있었다는 것만 기억해두도록 한다.

```java
@WebServlet(name = "responseHtmlServlet", urlPatterns = "/response-html")
public class ResponseHtmlServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");
        PrintWriter writer = response.getWriter();
        writer.println("<html>");
        writer.println("<body>");
        writer.println("    <div>HTML Response</div>");
        writer.println("</body>");
        writer.println("</html>");
    }
}
```

---

### API에 대한 HTTP 메시지 응답 (JSON 데이터)

Json을 반환할 때는 content-type을 application/json으로 지정해야 한다.

```java
@WebServlet(name = "responseJsonServlet", urlPatterns = "/response-json")
public class ResponseJsonServlet extends HttpServlet {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("content-Type", "application/json");
        response.setCharacterEncoding("utf-8");
        DefaultData defaultData = new DefaultData();
        defaultData.setUsername("Roy");
        defaultData.setAge(20);
        String responseBody = objectMapper.writeValueAsString(defaultData);
        response.getWriter().write(responseBody);
    }

    @Getter @Setter
    public static class DefaultData {
        private String username;
        private int age;
    }
}
```

application/json은 utf-8 형식을 사용하도록 정의되어 있으며 charset=utf-8과 같은 추가 파라미터를 지원하지 않는다.
application/json;charset=utf-8과 같은 사용은 의미없는 파라미터를 추가한 것이 된다.
response.getWriter()를 사용하여 Json 데이터를 입력하면 자동으로 추가 파라미터가 생성되므로 response.getOutputStream()을 사용하여 추가 파라미터가 생기지 않도록 해야한다.

---

**참고한 강의**:

- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%ED%95%B5%EC%8B%AC-%EC%9B%90%EB%A6%AC-%EA%B8%B0%EB%B3%B8%ED%8E%B8

- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-1

**Spring 공식문서**:

- https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#spring-web