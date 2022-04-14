이번 장에서는 스프링 MVC의 HTTP Reponse에 대해서 알아본다.
글의 하단부에 참고한 강의와 공식문서의 경로를 첨부하였으므로 자세한 내용은 강의나 공식문서에서 확인한다.
모든 코드는 [깃허브(링크)](https://github.com/roy-zz/mvc)에 올려두었다.

---

### 정적 리소스

스프링 부트는 클래스패스의 다음 디렉토리에 있는 정적 리소스를 반환한다.

- /static
- /public
- /resources
- /META-INF/resources

src/main/resources는 리소스를 보관하는 곳이며 클래스패스의 시작 경로다.
src/main/resources/static 디렉토리에 리소스를 넣으면 스프링 부트가 정적 리소스로 서비스를 제공한다.

---

### 뷰 템플릿

우리의 서비스는 뷰 템플릿을 사용하여 HTML 파일을 생성하여 클라이언트에게 전달한다.
스프링 부트는 뷰 템플릿 경로를 src/main/resources/templates로 사용한다.

src/main/resources/templates에 default.html 파일을 생성한다.

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<p th:text="${data}">empty</p>
</body>
</html>
```

**Version1**

고객의 요청이 들어오면 새로운 ModelAndView 객체를 생성하여 반환한다.

```java
@Controller
public class ViewResponseController {
    @RequestMapping(value = "/response-view", headers = "X-API-VERSION=1.0")
    public ModelAndView responseViewV1() {
        return new ModelAndView("response/default");
    }
}
```

**Version2**

템플릿 파일의 경로를 반환하여 스프링이 ModelAndView를 생성하여 반환하게 유도한다.

```java
@Controller
public class ViewResponseController {
    @RequestMapping(value = "/response-view", headers = "X-API-VERSION=2.0")
    public String responseViewV2(Model model) {
        model.addAttribute("data", "hello");
        return "response/default";
    }
}
```

**Version3**

클라이언트에서 요청할 때 미리 템플릿 파일의 경로를 지정하여 요청한다.
컨트롤러는 파라미터로 받은 Model 객체에 템플릿에 전달할 attribute만 추가한다.

```java
@Controller
public class ViewResponseController {
    @RequestMapping(value = "/response/default", headers = "X-API-VERSION=3.0")
    public void responseViewV3(Model model) {
        model.addAttribute("data", "hello");
    }
}
```

문자열(String)을 반환할 때 View의 경로로 반환할 수도 있고 HTTP 메시지의 바디로도 반환할 수 도 있다.
컨트롤러 레벨이나 메서드 레벨에 @ResponseBody가 있다면 뷰 리졸버를 실행하지 않고 HTTP 메시지 바디에 직접 문자열을 넣어서 반환한다.
@ResponseBody가 없다면 뷰 리졸버가 실행되어 뷰를 찾아서 렌더링한다.

Void를 반환하는 경우에 @Controller를 사용하고 HttpServletResponse, OutputStream(Writer) 같이 Http 메시지 바디를 처리하는 파라미터가 없으면 요청 URL를 기준으로 뷰 리졸버를 실행하여 뷰를 찾아 렌더링한다.
글을 적으면서도 혼란스럽다. 직관적인 방식이 많이 있으므로 굳이 직관적이지 않은 방식은 사용할 필요가 없다.

@ResponseBody, HttpEntity를 사용하면 뷰 템플릿을 사용하는 것이 아니라 HTTP 메시지 바디에 직접 응답 데이터를 출력할 수 있다.

---

### 메시지 바디에 직접 입력

API로 데이터를 제공하는 경우 HTML이 아니라 데이터를 전달해야 하므로 HTTP 메시지 바디에 Json 데이터를 반환하는 방법을 알아본다.

**String Version1**

서블릿을 사용할 때와 유사하게 반환한다.
HttpServletResponse 객체를 통해서 HTTP 메시지 바디에 직접 OK 메시지를 담아서 전달한다.

```java
@Slf4j
@Controller
public class BodyResponseController {
    @GetMapping(value = "/response-body-string", headers = "X-API-VERSION=1.0")
    public void responseBodyV1(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.getWriter().write("OK");
    }
}
```

**String Version2**

HttpEntity를 상속받은 ResponseEntity를 사용하여 응답한다.
ResponseEntity는 HttpEntity가 가지고 있는 속성과 함께 HTTP 응답 코드를 설정할 수 있다.

```java
@Slf4j
@Controller
public class BodyResponseController {
    @GetMapping(value = "/response-body-string", headers = "X-API-VERSION=2.0")
    public ResponseEntity<String> responseBodyV2() {
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
}
```

**String Version3**

단순 문자열을 반환한다. 
@ResponseBody를 사용했기 때문에 View를 찾지않고 메시지 바디에 데이터를 담아서 반환한다.

```java
@Slf4j
@Controller
public class BodyResponseController {
    @ResponseBody
    @GetMapping(value = "response-body-string", headers = "X-API-VERSION=3.0")
    public String responseBodyV3() {
        return "OK";
    }
}
```

**Json Version1**

ResponseEntity를 반환한다.
메시지 컨버터를 통해서 객체가 Json 형식으로 변환되어 반환된다.

```java
@Slf4j
@Controller
public class BodyResponseController {
    @GetMapping(value = "/response-body-json", headers = "X-API-VERSION=1.0")
    public ResponseEntity<ResponseDTO> responseBodyJsonV1() {
        ResponseDTO dto = new ResponseDTO();
        dto.setUsername("Roy");
        dto.setAge(20);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}
```

**Json Version2**

@ResponseBody를 사용하여 데이터를 반환한다.
@ResponseBody를 사용하는 경우 ResponseEntity와 다르게 응답코드를 지정할 수 없다.
@ResponseStatus 애노테이션을 사용하여 응답코드를 따로 설정해주어야 한다.
애노테이션 기반이기 때문에 동적으로 응답 값이 바뀔수 없으므로 동적으로 변경되어야 한다면 ResponseEntity를 사용해야한다.

```java
@Slf4j
@Controller
public class BodyResponseController {
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/response-body-json", headers = "X-API-VERSION=2.0")
    public ResponseDTO responseBodyJsonV2() {
        ResponseDTO dto = new ResponseDTO();
        dto.setUsername("Roy");
        dto.setAge(20);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());
        return dto;
    }
}
```

---

**참고한 강의**:

- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%ED%95%B5%EC%8B%AC-%EC%9B%90%EB%A6%AC-%EA%B8%B0%EB%B3%B8%ED%8E%B8

- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-1

**Spring 공식문서**:

- https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#spring-web