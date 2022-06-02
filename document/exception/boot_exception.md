[이전 장(링크)](https://imprint.tistory.com/271) 에서는 서블릿을 통해서 예외 처리를 하는 방법에 대해서 알아보았다.
이번 장에서는 스프링 부트의 기능을 사용하여 예외 처리를 하는 방법에 대해서 알아본다.
모든 코드는 [깃허브(링크)](https://github.com/roy-zz/mvc) 에 올려두었다.

---

### 오류 페이지

서블릿을 통한 예외 처리를 위해서는 아래와 같이 복잡한 과정을 거쳐야 했다.

- `WAS`의 재요청이 발생하는 경우 예외의 종류에 따른 컨트롤러를 매핑하기 위한 `WebServerCustomizer`생성
- `WebServerCustomizer`에서 예외의 종류에 따른 `ErrorPage` 객체 추가 및 컨트롤러와 매핑
- 예외 처리를 위한 컨트롤러인 `ExceptionHandleController` 생성
  
하지만 스프링 부트를 사용하는 경우 이런 과정이 전부 생략된다.  
`ErrorPage` 객체를 자동으로 등록하고 `resource/template/error` 경로에 있는 `*.html` 파일과 매핑한다.  
이렇게 자동으로 설정되는 경우 `new ErrorPage("/error")`와 같이 직접 지정하지 않는 경우 기본 오류 페이지를 사용한다.  
`WAS`까지 예외가 전달되거나, `response.sendError`가 호출되는 모든 경우에 `/error`를 호출하게 된다.  
  
서블릿을 통한 예외 처리는 우리가 직접 `ExceptionHandleController`를 생성해야 했지만 스프링 부트의 기본설정을 사용하는 경우 `BasicErrorController`라는 스프링 컨트롤러가 자동으로 등록된다.  
이러한 모든 작업은 `ErrorMvcAutoConfiguration`이라는 클래스가 우리 대신 진행해준다.

```java
@Controller
@RequestMapping("${server.error.path:${error.path:/error}}")
public class BasicErrorController extends AbstractErrorController {
	private final ErrorProperties errorProperties;
    // ...
	@RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
	public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
		HttpStatus status = getStatus(request);
		Map<String, Object> model = Collections
				.unmodifiableMap(getErrorAttributes(request, getErrorAttributeOptions(request, MediaType.TEXT_HTML)));
		response.setStatus(status.value());
		ModelAndView modelAndView = resolveErrorView(request, response, status, model);
		return (modelAndView != null) ? modelAndView : new ModelAndView("error", model);
	}
    // ...
}
```

이미 스프링 부트에서 `BasicErrorController`라는 클래스를 만들어 두었으므로 우리는 오류 페이지 화면만 개발하면 된다.  
선택 가능한 뷰가 여러개 있는 경우 스프링은 아래와 같은 규칙으로 우선순위를 정하여 화면을 렌더링되야 하는 화면을 정한다.

1. 뷰 템플릿은 정적 리소스보다 우선순위가 높다.
2. 구체적인 리소스가 덜 구체적인 리소스보다 우선순위가 높다.

예를 들면 아래와 같다.

**500 코드가 발생해서 뷰 템플릿을 찾는 경우**

1. `resources/templates/error/500.html`을 찾는다. 
2. 1단계에서 원하는 리소스를 찾지 못하는 경우 `resource/templates/error/5xx.html`을 찾는다.
  
**500 코드가 발생해서 정적 리소스(static, public)을 찾는 경우**

1. `resources/templates/error/500.html`을 찾는다.
2. 1단계에서 원하는 리소스를 찾지 못하는 경우 `resources/templates/error/5xx.html`을 찾는다.

모든 경우에 원하는 리소스를 찾지 못하는 경우 기본 오류 페이지인 `resources/templates/error.html` 파일을 찾는다.

---

### BasicErrorController

`BasicErrorController`는 아래와 같은 정보를 `model`에 담아서 뷰에 전달하고 뷰 템플릿은 이 값을 화면에 표시할 수 있다.

```
timestamp: Fri Feb 05 00:00:00 KST 2021
status: 400
error: Bad Request
exception: org.springframework.validation.BindException * trace: 예외 trace
message: Validation failed for object='data'. Error count: 1 * errors: Errors(BindingResult)
path: 클라이언트 요청 경로
```

하지만 오류 관련 정보들을 고객에게 노출하는 것은 보안상 매우 좋지 않다.  
`application.properties` 파일을 수정하여 오류 정보를 `model`에 포함할지 여부를 선택할 수 있다.

```
server.error.include-exception=true           // exception 포함 여부
server.error.include-message=on_param         // message 포함 여부
server.error.include-stacktrace=on_param      // trace 포함 여부
server.error.include-binding-errors=on_param  // errors 포함 여부
```

`include-exception`을 제외하면 아래와 같이 세개의 옵션을 사용할 수 있다.

- never: 항상 포함하지 않는다.
- always: 항상 포함한다.
- on_param: 파라미터가 있는 경우 포함한다.

---

### 참고

- `application.properties`에 `server.error.whitelabel.enabled` 옵션을 수정하면 오류 처리 화면을 찾지못하는 경우 스프링의 `whitelabel` 오류 페이지를 적용할 수 있다.
- `server.error.path` 옵션을 수정하여 오류 페이지를 찾을 때 기본으로 사용되는 `/error` 경로를 다른 경로로 수정할 수 있다.
- 에러 공통 처리 기능을 확장하고 싶은 경우 `ErrorController` 인터페이스의 구현체를 생성하거나 `BasicErrorController`를 상속받는 클래스를 생성한다.

---

**참고한 강의**:
- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%ED%95%B5%EC%8B%AC-%EC%9B%90%EB%A6%AC-%EA%B8%B0%EB%B3%B8%ED%8E%B8
- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-1
- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-2

**참고한 문서**:
- [Thymeleaf 기본 메뉴얼](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html)
- [Thymeleaf 스프링 통합 메뉴얼](https://www.thymeleaf.org/doc/tutorials/3.0/thymeleafspring.html)