이번 장부터는 Servlet, JSP, MVC 패턴의 차이에 대해서 알아본다.
글의 하단부에 참고한 강의와 공식문서의 경로를 첨부하였으므로 자세한 내용은 강의나 공식문서에서 확인한다.
모든 코드는 [깃허브(링크)](https://github.com/roy-zz/mvc)에 올려두었다.

---

[Servlet vs JSP vs MVC 패턴 - 1(링크)](https://imprint.tistory.com/183?category=1003393)와 [Servlet vs JSP vs MVC 패턴 - 2(링크)](https://imprint.tistory.com/184)을 통해서 순수 Servlet과 JSP로 개발한 애플리케이션의 한계에 대해서 알아보았다.
JSP의 도입으로 순서 서블릿의 단점은 어느정도 보완이 되었으나 여전히 JSP 파일 하나가 너무 많은 책임을 가진다는 한계점이 존재하였다.
이번 장에서는 JSP의 한계점인 "JSP 파일 하나가 너무 많은 책임을 가진다"라는 문제점을 MVC 패턴을 사용하여 해결해본다.
사용되는 Member와 MemberRepository 클래스는 이전에 작성한 [Servlet vs JSP vs MVC 패턴 - 1(링크)](https://imprint.tistory.com/183?category=1003393)를 참고한다.

---

"JSP 파일 하나가 너무 많은 책임을 가진다"라는 한계점에는 단일 책임의 원칙 이외의 문제점들도 있다.
"화면을 렌더링하는 기능(이하 렌더링 기능)"과 "서비스 로직을 통해 화면에 표시할 데이터를 생성하는 기능(이하 데이터 생성 기능)" 두 가지의 기능적인 라이프 사이클이 하나로 묶여있다는 점이다.
예를들어 화면 변경 요청이 들어와서 작업을 진행할 때 렌더링 기능에만 수정이 필요하지만 렌더링 기능과 데이터 생성 기능이 하나의 파일에 있기 때문에 서로 영향을 받을 수 있다.
렌더링 기능과 데이터 생성 기능은 관심사가 다르기 때문에 분리하여 자신의 담당 업무만 처리하는 것이 효율적이다.

이렇게 하나의 JSP 파일에 많아진 책임을 분리하기 위한 패턴이 MVC(Model View Controller)패턴이다.

**Model**: View에 출력할 데이터를 담는다. 

**View**: Model에 담겨있는 데이터를 화면에 표시하는 역할을 한다.

**Controller**: HTTP 요청을 받아서 파라미터를 검증하고 비즈니스 로직을 통해서 Model에 담을 데이터를 생성하는 역할을 한다.

---

### MVC 패턴을 사용한 애플리케이션 개발

#### 회원 등록 화면

회원 등록을 위한 화면을 제공하는 페이지를 요청하는 Servlet을 생성한다.
아래의 Servlet클래스는 클라이언트의 요청을 받아 필요한 JSP 파일을 응답한다.

**MVCMemberFormServlet**
```java
@WebServlet(name = "mvcMemberFormServlet", urlPatterns = "/servlet-mvc/members/new-form")
public class MVCMemberFormServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String viewPath = "/WEB-INF/views/new-form.jsp";
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);
    }
}
```

"forward()" 메서드의 경우 다른 서블릿이나 JSP로 이동할 수 있으며 서버 내부에서 호출하기 때문에 어떠한 동작이 일어나는지 클라이언트는 알 수 없다.
하지만 "redirect()"의 경우 서버 내부에서 작동하는 것이 아니라 클라이언트를 다른 페이지로 이동시키는 역할만을 담당한다.
"/WEB-INF" 경로의 파일은 클라이언트 파일을 지정하여 JSP파일에 접근할 수 없다. 
JSP파일에 접근하기 위해서는 컨트롤러나 서블릿을 통해서 접근해야한다.

MVCMemberFormServlet을 통해 클라이언트에게 전달될 JSP파일을 작성한다.

**new-form.jsp**
```html
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<form action="save" method="post">
    username: <input type="text" name="username" /> age: <input type="text" name="age" /> <button type="submit">전송</button>
</form>
</body>
</html>
```

회원 등록 화면의 경우 동적으로 변경되어야 할 부분이 없기 때문에 JSP로 구현한 방법과 동일하다.

---

### 회원 저장 화면

클라이언트의 요청을 받아서 새로운 사용자를 생성하고 생성된 사용자의 정보를 다시 클라이언트에게 전달하는 MVCMemberSaveServlet 클래스 파일을 생성한다.

**MVCMemberSaveServlet**
```java
@WebServlet(name = "mvcMemberSaveServlet", urlPatterns = "/servlet-mvc/members/save")
public class MVCMemberSaveServlet extends HttpServlet {

    private final MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));
        Member newMember = new Member(username, age);
        memberRepository.save(newMember);

        request.setAttribute("member", newMember);
        
        String viewPath = "/WEB-INF/views/save-result.jsp";
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);
    }
}
```

HttpServletRequest는 컨트롤러(서블릿)와 View간의 데이터 전달을 위하여 Attribute를 제공한다.
데이터를 제공하는 쪽(컨트롤러)에서는 setAttribute()로 데이터를 집어넣고 데이터를 사용하는 쪽에서는 getAttribute()를 통해서 데이터를 조회한다.

서블릿으로부터 전달받은 데이터를 화면에 표시하는 JSP파일을 생성한다.

**save-result.jsp**
```html
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
</head>
<body> 성공
<ul>
    <li>id=${member.id}</li>
    <li>username=${member.username}</li>
    <li>age=${member.age}</li>
</ul>
<a href="/index.html">메인</a>
</body>
</html>
```

서블릿에서 request에 setAttribute("member", ~~)와 같이 데이터를 집어넣었다.
이를 사용하는 JSP 파일에서는 Attribute의 키 값을 기반으로 . 탐색을 통하여 데이터를 조회할 수 있다.
"<% 자바 코드 %>" 문법으로도 같은 기능을 하는 코드를 작성할 수 있다. 
코드가 복잡해진다는 단점은 있지만 IDE를 통하여 자바 문법을 검사받을 수 있다는 장점도 있다.

---

### 회원 목록 화면

저장소에 저장되어 있는 회원 목록을 조회하여 HttpServletRequest 객체에 저장하는 역할을 하는 MVCMemberListServlet 클래스를 생성한다.

**MVCMemberListServlet**
```java
@WebServlet(name = "mvcMemberListServlet", urlPatterns = "/servlet-mvc/members")
public class MVCMemberListServlet extends HttpServlet {
    private final MemberRepository memberRepository = MemberRepository.getInstance();
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Member> storedMembers = memberRepository.findAll();

        request.setAttribute("members", storedMembers);

        String viewPath = "/WEB-INF/views/members.jsp";
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);
    }
}
```

JSP파일로 데이터를 전달하는 부분은 MVCMemberSaveServlet과 동일하다.
전달받은 회원 목록을 화면에 표시하는 JSP 파일을 생성한다.


```html
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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
<c:forEach var="item" items="${members}">
        <tr>
            <td>${item.id}</td>
            <td>${item.username}</td>
            <td>${item.age}</td>
        </tr>
</c:forEach>
    </tbody>
</table>
</body>
</html>
```

화면에 회원 목록을 표시하는 부분 또한 회원을 저장하는 코드와 유사하다.
다만 회원이 여러명인 경우를 위하여 members 컬렉션을 반복문을 돌면서 처리하는 부분만 추가되었다.

---

### Servlet을 통한 MVC 패턴의 한계

MVC 패턴을 통해서 JSP의 단점인 "JSP 파일 하나의 책임이 많다"는 해결되었다.
하지만 dispatcher를 통한 포워딩 코드와 JSP 파일의 경로를 지정하는 코드들이 중복된다.
이러한 중복되는 코드는 사용하는 템플릿 엔진이 변경되면 해당되는 모든 코드를 찾아서 수정해야하는 문제가 발생한다.

다음 장부터는 프론트 컨트롤러(Front Controller)패턴을 사용하여 이러한 한계점들을 해결해본다.

---

**참고한 강의**:

- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%ED%95%B5%EC%8B%AC-%EC%9B%90%EB%A6%AC-%EA%B8%B0%EB%B3%B8%ED%8E%B8

- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-1

**Spring 공식문서**:

- https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#spring-web