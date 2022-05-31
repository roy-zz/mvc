이번 장에서는 서블릿 필터를 사용하는 방법에 대해서 알아본다.
모든 코드는 [깃허브(링크)](https://github.com/roy-zz/mvc) 에 올려두었다.

---

### 소개

이번 장에서는 **서블릿의 필터** 기능을 사용하는 방법에 대해서 알아본다. 비슷한 기능으로는 **스프링의 인터셉터** 기능이 있다.  
  
우리는 이전에 쿠키와 세션을 사용하여 로그인 기능을 구현하였다. 해당 기능을 사용하여 로그인을 하는 경우 사용자가 로그인을 하지 않아도 `URL` 주소만 알고 있으면 원하는 페이지에 접근할 수 있다는 치명적인 단점이 있다.  
모든 컨트롤러에서 로그인 여부를 확인하는 로직을 작성하면 되지만 모든 컨트롤러에서 주로직이 아닌 로그인 유무를 확인하는 부로직을 다루어야 한다는 치명적인 단점이 있다.  
또한, 추후 로그인 관련 로직이 변경되면 모든 컨트롤러의 코드가 변경되어야 한다. 이는 확장에는 열려있고 변경에는 닫혀있어야 한다는 `OCP` 원칙을 위반하는 행위다.  
  
모든 컨트롤러에서 로그인한 사용자만 접근 가능하도록 하는 것처럼 여러 로직에서 공통으로 관심이 있는 것을 **공통 관심사(cross-cutting concern)** 라고 한다.  
일반적으로 스프링에서 공통 관심사는 AOP를 사용하여 처리하지만 웹과 관련된 공통 관심사는 **서블릿 필터** 또는 **스프링 인터셉터**를 사용하여 해결한다.  
대표적으로 웹과 관련된 공통 관심사를 처리할 때는 HTTP 헤더나 URL 정보들이 필요한데, 서블릿 필터나, 스프링 인터셉터는 `HttpServletRequest`를 제공한다.
  
#### 필터 흐름

서블릿의 필터를 사용하게 되면 사용자의 요청은 아래와 같은 흐름으로 우리에게 전달된다.
필터는 서블릿보다 앞에서 클라이언트의 요청을 받게되며 여기서 서블릿은 우리가 알고 있는 `디스패처 서블릿`이라고 생각하면 된다.

```
HTTP 요청 -> WAS -> 필터 -> 서블릿 -> 컨트롤러
```

필터를 사용하여 로그인된 사용자와 로그인되지 않은 사용자를 구분하여 로그인되지 않은 사용자의 요청은 서블릿까지 도달하지 않도록 구현할 수 있다.

```
HTTP 요청 -> WAS -> 필터(로그인 O) -> 서블릿 -> 컨트롤러
HTTP 요청 -> WAS -> 필터(로그인 X) -> 이후 진행 X
```

동시에 여러개의 필터를 체이닝하여 우리가 원하는 기능을 하도록 구현할 수 있다.  

```
HTTP 요청 -> WAS -> 요청 로그 필터 -> 로그인 검증 필터 -> 기타 필터 -> 서블릿 -> 컨트롤러
```

만약 예시와 같이 구현되어 있다면 로그인하지 않은 사용자의 요청은 `로그인 검증 필터`에 의해 서블릿까지 도달하지 못하지만 `요청 로그 필터`에 의해 로그로 남게 할 수 있따.

#### Filter

우리가 구현해야 하는 필터 인터페이스는 아래와 같은 구조로 되어 있다.  
~~(왜 굳이 public 키워드를 사용하였을까)~~

```java
public interface Filter {
    public default void init(FilterConfig filterConfig) throws ServletException {}
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException;
    public default void destroy() {}
}
```

필터 인터페이스를 구현하면 서블릿 컨테이너가 싱글톤 객체를 생성하고 빈으로 관리한다.

- init: 필터 초기화 메서드로 서블릿 컨테이너가 생성될 때 호출된다.
- doFilter(): 고객의 요청이 올 때마다 해당 메서드가 호출되며 우리가 원하는 로직을 작성하는 곳이다.
- destroy(): 필터 종료 메서드로 서블릿 컨테이너가 종료될 떄 호출된다.

---

### 요청 로그 필터

가장 단순한 필터인 모든 요청을 로그로 남기는 필터를 만들어 본다.
  
#### LogFilter 구현체

`Filter` 인터페이스를 구현하는 `LogFilter` 클래스를 생성한다.

```java
@Slf4j
public class LogFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("LogFilter call init");
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("LogFilter call doFilter");
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();
        String uuid = UUID.randomUUID().toString();
        try {
            log.info("LogFilter try [{}][{}]", uuid, requestURI);
            chain.doFilter(request, response);
        } catch (Exception e) {
            throw e;
        } finally {
            log.info("LogFilter finally [{}][{}]", uuid, requestURI);
        }
    }
    @Override
    public void destroy() {
        log.info("LogFilter call destroy");
    }
}
```

- doFilter: 
  - HTTP 요청이 오면 `doFilter`가 호출된다.
  - HTTP 요청이 아닌 경우에도 필터를 사용할 수 있도록 `ServletRequest`가 인자값으로 넘어온다.  
    우리는 HTTP로 통신하기 때문에 `HttpServletRequest`로 다운캐스팅 하여 사용하면 된다.
- UUID: 한번에 여러개의 요청이 왔을 때 요청을 구분하기 위해 임의의 `uuid`를 출력하기 위해 사용된다.
- chain.doFilter(): 다음 필터가 있으면 필터를 호출하고 필터가 없는 경우 서블릿을 호출한다.  
  만약 해당 코드가 없으면 다음 단계로 진행되지 않는다.

#### 필터 등록

필터를 빈으로 등록하기 위해서는 아래와 같이 구성 요소로 등록해주어야 한다.

```java
@Configuration
public class WebConfig {
    @Bean
    public FilterRegistrationBean logFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new LogFilter());
        filterRegistrationBean.setOrder(1);
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }
}
```

`FilterRegistrationBean`을 사용하여 필터를 등록하는 방법 대신 `@ServletComponentScan`, `@WebFilter`를 사용하여 필터를 등록하는 방법이 있다.  
하지만 우리가 사용하지 않은 두 방법은 필터의 순서를 조절할 수 없으므로 스프링 부트를 사용한다면 `FilterRegistrationBean`을 사용하는 것이 좋다.  

- setFilter: 등록할 필터를 지정한다.
- setOrder: 여러개의 필터가 체이닝될 때 몇번째로 동작할 것인지 순서를 지정한다.
- addUrlPatterns: 필터를 적용할 서블릿 URL 패턴을 지정한다. 이름에서 알 수 있듯이 여러개의 필터를 지정할 수 있다.
  
만약 실무에서 HTTP 요청시 같은 요청에서 발생한 모든 로그에 같은 식별자를 남기고 싶다면 `logback mdc`를 사용하면 된다.

---

### 인증 확인 필터

로그인 되지 않은 사용자는 우리가 허용한 페이지 이외의 페이지에는 접근하지 못하도록 막아주는 `인증 확인 필터`를 만들어본다.

#### AuthenticationFilter 구현체

`Filter` 인터페이스를 구현하는 `AuthenticationFilter` 클래스를 생성한다.  
`Filter` 인터페이스의 `init`, `destroy` 메서드는 `default` 메서드이기 때문에 반드시 구현해야할 필요는 없다.

```java
@Slf4j
public class AuthenticationFilter implements Filter {
    private static final String[] whitelist = {"/", "/members/add", "/login", "/logout", "/css/*"};
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        try {
            log.info("인증 검증 필터 시작 {}", requestURI);
            if (!isContainWhitelist(requestURI)) {
                log.info("화이트 리스트에 포함되지 않는 경우 검증 로직 실행 {}", requestURI);
                HttpSession session = httpRequest.getSession(false);
                if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
                    log.info("로그인 되지 않은 사용자의 요청 {}", requestURI);
                    httpResponse.sendRedirect("/login?redirectURL=" + requestURI);
                    return;
                }
            }
            chain.doFilter(request, response);
        } catch (Exception e) {
            throw e;
        } finally {
            log.info("인증 검증 필터 종료 {} ", requestURI);
        }
    }
    private boolean isContainWhitelist(String requestURI) {
        return PatternMatchUtils.simpleMatch(whitelist, requestURI);
    }
}
```

- whitelist: 인증 필터를 적용된 이후 특정 리소스는 인증되지 않은 사용자도 접근할 수 있어야 한다.  
  예를 들어 인증을 하기 위한 로그인 페이지와 로그인 페이지를 그리기 위한 css파일은 사용자에게 제공되어야 하기때문에 화이트 리스트로 등록되어 인증 필터에서 처리되지 않도록 한다.
- isContainWhitelist: 화이트 리스트를 제외한 모든 경우에 인증 검증 로직을 적용한다.
- httpResponse.sendRedirect: 로그인 되어있지 않은 사용자는 로그인 화면으로 리다이렉트 한다. 이때 로그인을 성공적으로 마치는 경우 기존에 접속하려는 페이지로 이동시키기 위하여 `?redirectURL={}` 이 추가되었다.  
  만약 로그인되어 있지 않은 사용자가 아이템 목록에 접근하는 경우 로그인 페이지로 이동시키고 로그인이 완료되면 다시 아이템 목록으로 이동시킨다.  
  여기서는 redirectURL 쿼리 파라미터만 추가하므로 컨트롤러에서 추가 작업이 필요하다.
- return: 로그인 되어 있지 않은 사용자의 경우 더 이상 필터를 진행하지 않게하기 위해서 사용된다.  
  `redirect`를 사용했기 때문에 `redirect`가 응답으로 사용되고 요청은 완료된다.

#### 필터 등록

필터를 빈으로 등록하기 위해서는 아래와 같이 구성 요소로 등록해주어야 한다.

```java
@Configuration
public class WebConfig {
    @Bean
    public FilterRegistrationBean authenticationFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new AuthenticationFilter());
        filterRegistrationBean.setOrder(2);
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }
}
```

#### 컨트롤러 수정

로그인 하는 사용자가 기존에 접속하려는 페이지가 있는 경우(쿼리 파라미터로 `redirect`경로가 있는 경우)에 로그인이 완료되면 기존에 접속하려는 페이지로 이동시키는 코드를 추가한다.

```java
@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;
    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult,
                          @RequestParam(defaultValue = "/") String redirectURL,
                          HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            // 파라미터 바인딩에 실패하는 경우 처리
        }
        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
        if (loginMember == null) {
            bindingResult.reject("로그인 실패", "아이디 또는 비밀번호가 일치하지 않습니다.");
            // 로그인 실패하는 경우 처리
        }
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
        return "redirect:" + redirectURL;
    }
}
```
  
파라미터로 `redirectURL`을 받아오고 있으며 기본값은 "/"로 되어있다.  
만약 로그인 되어 있지 않은 상태로 어떤 페이지에 접근을 시도하다 막힌 경우 우리가 만든 `AuthenticationFilter`에 의해 쿼리 파라미터로 `redirectURL`을 가지게 된다.  
`redirectURL`이 존재하는 경우 기존에 접속하려던 페이지로 `redirect`시킨다.
  
공통 관심사(cross-cutting concern)를 서블릿 필터에 적용하여 추후 로그인 기능에 유지보수가 필요한 경우 `AuthenticationFilter`만 확인하면 되도록 수정되었다.
  
추가로 서블릿 필터를 사용하는 경우 다음 필터나 서블릿을 호출할 때 자신이 전달받은 `request`, `response`를 다른 객체로 변경할 수 있다.  
자주 사용되지는 않지만 스프링 인터셉터에서는 제공하지 않는 기능이므로 이러한 기능이 필요한 경우 서블릿 필터를 사용해야 한다.

---

**참고한 강의**:
- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%ED%95%B5%EC%8B%AC-%EC%9B%90%EB%A6%AC-%EA%B8%B0%EB%B3%B8%ED%8E%B8
- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-1
- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-2

**참고한 문서**:
- [Thymeleaf 기본 메뉴얼](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html)
- [Thymeleaf 스프링 통합 메뉴얼](https://www.thymeleaf.org/doc/tutorials/3.0/thymeleafspring.html)
