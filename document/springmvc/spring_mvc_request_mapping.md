이번 장에서는 [스프링 MVC 핸들러(링크)](https://imprint.tistory.com/196)에 이어 스프링 MVC의 @RequestMapping에 대해서 알아본다.
글의 하단부에 참고한 강의와 공식문서의 경로를 첨부하였으므로 자세한 내용은 강의나 공식문서에서 확인한다.
모든 코드는 [깃허브(링크)](https://github.com/roy-zz/mvc)에 올려두었다.

---

### @RequestMapping

@RequestMapping을 사용하면 핸들러(컨트롤러)로 등록된다.
이렇게 등록된 핸들러는 RequestMappingHandlerMapping과 RequestMappingHandlerAdapter를 통해서 처리된다.
현재 대부분의 실무에서는 @RequestMapping을 사용하여 개발을 진행하는 추세이다.
이번 장에서는 기존의 코드를 @RequestMapping을 사용하여 리팩토링을 진행하고 코드가 어디까지 간결해지는지 확인해본다.

---

### Version 1

```java
@Controller
public class SpringMemberFormControllerV1 {
    @RequestMapping("/springmvc/v1/members/new-form")
    public ModelAndView process() {
        return new ModelAndView("new-form");
    }
}
```

@Controller 애노테이션을 사용하여 컴포넌트의 스캔 대상이 되며 빈으로 등록되며 스프링 MVC에서 애노테이션 기반의 컨트롤러로 인식한다.
@RequestMapping의 요청 정보를 매핑하여 해당 URL이 호출되면 메서드가 호출되며 애노테이션 기반으로 등록되기 때문에 메서드 이름은 개발자 임의로 지정해도 무관하다.

```java
@Controller
public class SpringMemberSaveControllerV1 {
    private final MemberRepository repository = MemberRepository.getInstance();
    @RequestMapping("/springmvc/v1/members/save")
    public ModelAndView process(HttpServletRequest request) {
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));
        Member newMember = new Member(username, age);
        repository.save(newMember);
        ModelAndView mv = new ModelAndView("save-result");
        mv.addObject("member", newMember);
        return mv;
    }
}
```

스프링이 제공하는 ModelAndView의 모델에 데이터를 추가할 때는 addObject가 사용되며 화면을 동적으로 렌더링할 때 사용된다.

```java
@Controller
public class SpringMemberListControllerV1 {
    private final MemberRepository repository = MemberRepository.getInstance();
    @RequestMapping("/springmvc/v1/members")
    public ModelAndView process() {
        List<Member> storedMembers = repository.findAll();
        ModelAndView mv = new ModelAndView("members");
        mv.addObject("members", storedMembers);
        return mv;
    }
}
```

@RequestMapping이 좋다고하여 사용해 보았지만 아직까지 이렇다하게 편해진 모습은 보이지 않는다.

---

### Version 2

각 핸들러마다 전부 클래스 파일을 따로 생성해주어야 하는 불편함이 있다.
이번에는 하나의 클래스 파일에 우리가 작성한 핸들러들을 모두 통합시켜본다.

```java
@Controller
@RequestMapping("/springmvc/v2/members")
public class SpringMemberControllerV2 {
    private final MemberRepository memberRepository = MemberRepository.getInstance();
    @RequestMapping("/new-form")
    public ModelAndView newForm() {
        return new ModelAndView("new-form");
    }

    @RequestMapping("/save")
    public ModelAndView save(HttpServletRequest request, HttpServletResponse response) {
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));
        Member newMember = new Member(username, age);
        memberRepository.save(newMember);
        ModelAndView mv = new ModelAndView("save-result");
        mv.addObject("member", newMember);
        return mv;
    }

    @RequestMapping
    public ModelAndView members() {
        List<Member> storedMembers = memberRepository.findAll();
        ModelAndView mv = new ModelAndView("members");
        mv.addObject("members", storedMembers);
        return mv;
    }
}
```

클래스 레벨에 @RequestMapping을 사용하여 Prefix URL Path를 지정한다.
메서드 레벨의 @RequestMapping은 전부 Prefix뒤에 자신이 선언한 URL 주소가 추가되어 핸들러로 등록된다.

아직 우리는 매번 화면을 렌더링하기 위해 ModelAndView 객체를 생성해야하는 불편함이 있다.

---

### Version 3

이번에는 매번 ModelAndView 객체를 생성해야하는 불편함을 제거해보고 HttpMethod를 적용해본다.

```java
@Controller
@RequestMapping("/springmvc/v3/members")
public class SpringMemberControllerV3 {
    private final MemberRepository repository = MemberRepository.getInstance();
    @RequestMapping(value = "/new-form", method = RequestMethod.GET)
    public String newForm() {
        return "new-form";
    }

    @PostMapping("/save")
    public String save(
            @RequestParam("username") String username,
            @RequestParam("age") int age,
            Model model
    ) {
        Member newMember = new Member(username, age);
        repository.save(newMember);
        model.addAttribute("member", newMember);
        return "save-result";
    }

    @GetMapping
    public String members(Model model) {
        List<Member> storedMembers = repository.findAll();
        model.addAttribute("members", storedMembers);
        return "members";
    }
}
```

@RequestMapping을 사용하는 경우 모든 HttpMethod를 사용하여 호출이 가능하다.
이러한 방법은 RESTful한 설계 방식과는 맞지 않다. @RequestMapping(method = RequestMethod.GET)과 같이 GET 메서드를 통해서만 호출되도록 해야한다.
@GetMapping은 내부에 @RequestMapping(method = RequestMethod.GET)을 포함하고 있어서 간편하게 GET에 대한 요청만 받을 수 있도록 해준다.

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RequestMapping(method = RequestMethod.GET)
public @interface GetMapping {
    // 중간 생략
}
```

필요한 경우 파라미터에 Model을 지정하여 사용할 수 있다.
@ResponseBody와 @RestController를 사용하지 않으면 String을 반환하는 것 만으로 View로 렌더링 할 수 있다.

---

**참고한 강의**:

- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%ED%95%B5%EC%8B%AC-%EC%9B%90%EB%A6%AC-%EA%B8%B0%EB%B3%B8%ED%8E%B8

- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-1

**Spring 공식문서**:

- https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#spring-web