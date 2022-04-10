이번 장부터는 Servlet, JSP, MVC 패턴의 차이에 대해서 알아본다.
글의 하단부에 참고한 강의와 공식문서의 경로를 첨부하였으므로 자세한 내용은 강의나 공식문서에서 확인한다.
모든 코드는 [깃허브(링크)](https://github.com/roy-zz/mvc)에 올려두었다.

---

Servlet, JSP, MVC 패턴의 차이에 대해서 알아본다.
이번 장에서는 Servlet을 사용해 본다.

세가지 방식에서 사용될 Member클래스와 MemberRepository클래스다.

**Member**
```java
import static lombok.AccessLevel.PRIVATE;

@Getter @Setter
@NoArgsConstructor(access = PRIVATE)
public class Member {
    private Long id;
    private String username;
    private int age;
    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }
}
```

**MemberRepository**
```java
@NoArgsConstructor(access = PRIVATE)
public class MemberRepository {
    private static final Map<Long, Member> REPOSITORY = new ConcurrentHashMap<>();
    private static final AtomicLong ID = new AtomicLong(0L);
    private static final MemberRepository INSTANCE = new MemberRepository();

    public static MemberRepository getInstance() {
        return INSTANCE;
    }
    public Member save(Member member) {
        member.setId(ID.updateAndGet((n) -> ++n));
        REPOSITORY.put(member.getId(), member);
        return member;
    }
    public Member findById(Long id) {
        return REPOSITORY.get(id);
    }
    public List<Member> findAll() {
        return new ArrayList<>(REPOSITORY.values());
    }
    public void clear() {
        REPOSITORY.clear();
    }
}
```

MemberRepository의 경우 싱글톤으로 생성하였다.
동시성 이슈를 방지하기 위해 ConcurrentHashMap과 AtomicLong을 사용하였다.

---

### Servlet를 사용한 애플리케이션 개발

**MemberFormServlet**

```java
@WebServlet(name = "memberFormServlet", urlPatterns = "/servlet/members/new-form")
public class MemberFormServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");
        PrintWriter writer = response.getWriter();
        writer.write("<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <title>Title</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<form action=\"/servlet/saveMembers\" method=\"post\">\n" +
                        "    username: <input type=\"text\" name=\"username\" />\n" +
                        "    age:      <input type=\"text\" name=\"age\" />\n" +
                        "    <button type=\"submit\">전송</button>\n" +
                        "</form>\n" +
                        "</body>\n" +
                        "</html>\n");
    }
}
```

클라이언트에게 저장하려는 사용자의 정보를 입력하는 화면을 제공한다.

---

**MemberSaveServlet**

```java
@WebServlet(name = "memberSaveServlet", urlPatterns = "/servlet/members/save")
public class MemberSaveServlet extends HttpServlet {
    private final MemberRepository memberRepository = MemberRepository.getInstance();
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));
        Member newMember = new Member(username, age);
        memberRepository.save(newMember);
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");
        PrintWriter writer = response.getWriter();
        writer.write("<html>\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "성공\n" +
                        "<ul>\n" +
                        "    <li>id=" + newMember.getId() + "</li>\n" +
                        "    <li>username=" + newMember.getUsername() + "</li>\n" +
                        "    <li>age=" + newMember.getAge() + "</li>\n" +
                        "</ul>\n" +
                        "<a href=\"/index.html\">메인</a>\n" +
                        "</body>\n" +
                        "</html>");
    }
}
```

MemberFormServlet 페이지에서 입력한 데이터를 전달받아서 저장소에 저장하는 역할을 한다.
저장하고 난 후의 데이터를 사용하여 동적 HTML 파일을 만들어서 응답한다.

---

**MemberListServlet**

```java
@WebServlet(name = "memberListServlet", urlPatterns = "/servlet/members")
public class MemberListServlet extends HttpServlet {
    private final MemberRepository memberRepository = MemberRepository.getInstance();
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");
        List<Member> storedMembers = memberRepository.findAll();
        PrintWriter writer = response.getWriter();
        writer.write("<html>");
        writer.write("<head>");
        writer.write("    <meta charset=\"UTF-8\">");
        writer.write("    <title>Title</title>");
        writer.write("</head>");
        writer.write("<body>");
        writer.write("<a href=\"/index.html\">메인</a>");
        writer.write("<table>");
        writer.write("    <thead>");
        writer.write("    <th>id</th>");
        writer.write("    <th>username</th>");
        writer.write("    <th>age</th>");
        writer.write("    </thead>");
        writer.write("    <tbody>");
        for (Member member : storedMembers) {
            writer.write("    <tr>");
            writer.write("        <td>"+member.getId()+"</td>");
            writer.write("        <td>"+member.getUsername()+"</td>");
            writer.write("        <td>"+member.getAge()+"</td>");
            writer.write("    </tr>");
        }
        writer.write("    </tbody>");
        writer.write("</table>");
        writer.write("</body>");
        writer.write("</html>");
    }
}
```

저장된 사용자들의 데이터를 저장소에서 조회하여 동적으로 HTML 파일을 생성한다.
이렇게 String으로 HTML 파일을 만들어보니 JSP나 다른 템플릿 엔진이 없던 시절의 개발자들은 정말 이렇게 개발하였을지 의심까지 된다.

JSP, 템플릿 엔진이 없던 시절에는 이렇게 비효율적으로 동적 페이지를 생성하였다는 정도만 기억하고 다음 장에서 JSP로 같은 기능을 하는 애플리케이션을 개발해본다.

---

**참고한 강의**:

- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%ED%95%B5%EC%8B%AC-%EC%9B%90%EB%A6%AC-%EA%B8%B0%EB%B3%B8%ED%8E%B8

- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-1

**Spring 공식문서**:

- https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#spring-web