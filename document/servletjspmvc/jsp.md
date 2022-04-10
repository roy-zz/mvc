이번 장부터는 Servlet, JSP, MVC 패턴의 차이에 대해서 알아본다.
글의 하단부에 참고한 강의와 공식문서의 경로를 첨부하였으므로 자세한 내용은 강의나 공식문서에서 확인한다.
모든 코드는 [깃허브(링크)](https://github.com/roy-zz/mvc)에 올려두었다.

---

Servlet, JSP, MVC 패턴의 차이에 대해서 알아본다.
이번 장에서는 JSP를 사용하여 애플리케이션을 개발해본다.
사용되는 Member와 MemberRepository 클래스는 이전에 작성한 [Servlet vs JSP vs MVC 패턴 - 1(링크)](https://imprint.tistory.com/183?category=1003393)를 참고한다.

---

### JSP를 사용한 애플리케이션 개발

JSP를 사용하기 위해선 두 개의 의존성을 추가해야한다.
build.gradle의 dependencies 경로에 아래의 의존성을 추가한다.

```bash
implementation("org.apache.tomcat.embed:tomcat-embed-jasper")
implementation("javax.servlet:jstl")
```

**new-form.jsp**
```html
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<form action="/jsp/members/save.jsp" method="post">
    username: <input type="text" name="username" />
    age: <input type="text" name="age" />
    <button type="submit">전송</button>
</form>
</body>
</html>
```

클라이언트에게 새로운 사용자를 저장하기 위해 필요한 정보를 입력할 화면을 제공한다.
가장 첫줄인 <%@ page ~ %> 부분은 이 파일이 jsp 파일이라는 의미를 가진다.

---

**save.jsp**
```html
<%@ page import="com.roy.mvc.servlet.web.servlet.domain.MemberRepository" %>
<%@ page import="com.roy.mvc.servlet.web.servlet.domain.Member" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    MemberRepository memberRepository = MemberRepository.getInstance();
    String username = request.getParameter("username");
    int age = Integer.parseInt(request.getParameter("age"));
    Member newMember = new Member(username, age);
    memberRepository.save(newMember);
%>
<html>
<head>
    <meta charset="UTF-8">
</head>
<body>
성공
<ul>
    <li>id=<%=newMember.getId()%></li>
    <li>username=<%=newMember.getUsername()%></li>
    <li>age=<%=newMember.getAge()%></li>
</ul>
<a href="/index.html">메인</a>
</body>
</html>
```

new-form.jsp에서 입력받은 데이터를 기반으로 사용자를 저장하는 역할을 한다.
"<% page import ~ %>" 부분은 자바 코드에서 import를 하는 것처럼 jsp파일에서 java 파일을 import하기 위해 사용한다.
"<% ~ %>" 부분은 자바 코드를 사용하기 위해서 필수적으로 사용해야하는 부분이다.
li부분의 newMember에서 사용자의 Id를 조회하는 부분에서도 자바 코드를 사용해야하기 때문에 <% ~ %>을 사용한 것을 알 수 있다.

---

**member.jsp**

```html
<%@ page import="java.util.List" %>
<%@ page import="com.roy.mvc.servlet.web.servlet.domain.MemberRepository" %>
<%@ page import="com.roy.mvc.servlet.web.servlet.domain.Member" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    MemberRepository memberRepository = MemberRepository.getInstance();
    List<Member> members = memberRepository.findAll();
%>
<html>
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<a href="/index.html">메인</a>
<table>
    <thead>
    <th>id</th>
    <th>username</th>
    <th>age</th>
    </thead>
    <tbody>
    <%
        for (Member member : members) {
            out.write("<tr>");
            out.write("<td>" + member.getId() + "</td>");
            out.write("<td>" + member.getUsername() + "</td>");
            out.write("<td>" + member.getAge() + "</td>");
            out.write("</tr>");
        }
    %>
    </tbody>
</table>
</body>
</html>
```

저장되어 있는 모든 사용자의 정보를 화면에 출력하는 역할을 한다.

---

### 서블릿과 JSP의 한계

서블릿의 경우 화면을 만들기 위해 자바 코드에 문자열이 섞이며 혼란스러운 코드가 작성되었다.
JSP를 사용하는 경우 순수 서블릿을 사용하는 방식보다는 깔끔하게 변경되었고 HTML파일에 동적으로 변경되야하는 부분만 자바 코드가 추가되는 형식으로 변경되었다.

하지만 JSP 파일 하나가 화면 표시 역할, 서비스 역할, 리포지토리 역할을 하는 것을 확인할 수 있다.
이러한 문제점을 해결하기 위해 MVC 패턴이 등장하였다.

다음 장에서는 MVC 패턴에 대해서 알아본다.

---

**참고한 강의**:

- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%ED%95%B5%EC%8B%AC-%EC%9B%90%EB%A6%AC-%EA%B8%B0%EB%B3%B8%ED%8E%B8

- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-1

**Spring 공식문서**:

- https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#spring-web