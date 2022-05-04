이번 장에서는 타임리프의 `템플릿 조각`과 `레이아웃` 기능에 대해서 알아본다.
글의 하단부에 참고한 강의와 공식문서의 경로를 첨부하였으므로 자세한 내용은 강의나 공식문서에서 확인한다.
모든 코드는 [깃 허브(링크)](https://github.com/roy-zz/mvc) 에 올려두었다.

---

### 개요

웹 페이지를 개발할 때 페이지마다 중복되는 공통 영역들이 많이 있다.
상단 영역이나 하단 영역등등 여러 페이지에서 함께 사용하는 영역들이 있다. 이런 부분을 코드를 복사해서 사용한다면 변경 시 여러 페이지를 다 수정해야 하므로 비효율 적이다.
타임리프는 이러한 문제를 해결하기 위해 `템플릿 조각(Template Fragment)`과 `레이아웃(Layout)`기능을 지원한다.

---

### Fragment

#### 예시

**Controller**
```java
@GetMapping("/fragment")
public String fragment() {
    return "template/fragment/fragmentMain";
}
```

**footer.html**
```html
<footer th:fragment="copy">
    푸터 자리 입니다.
</footer>
<footer th:fragment="copyParam (param1, param2)">
    <p>파라미터 자리 입니다.</p>
    <p th:text="${param1}"></p>
    <p th:text="${param2}"></p>
</footer>
```

**fragmentMain.html**
```html
<h1>부분 포함</h1>
<h2>부분 포함 insert</h2>
<div th:insert="~{template/fragment/footer :: copy}"></div>

<h2>부분 포함 replace</h2>
<div th:replace="~{template/fragment/footer :: copy}"></div>

<h2>부분 포함 단순 표현식</h2>
<div th:replace="template/fragment/footer :: copy"></div>

<h1>파라미터 사용</h1>
<div th:replace="~{template/fragment/footer :: copyParam ('데이터1', '데이터2')}"></div>
```

**Result**

```html
<h1>부분 포함</h1>
<h2>부분 포함 insert</h2>
<div><footer>
    푸터 자리 입니다.
</footer></div>

<h2>부분 포함 replace</h2>
<footer>
    푸터 자리 입니다.
</footer>

<h2>부분 포함 단순 표현식</h2>
<footer>
    푸터 자리 입니다.
</footer>

<h1>파라미터 사용</h1>
<footer>
    <p>파라미터 자리 입니다.</p>
    <p>데이터1</p>
    <p>데이터2</p>
</footer>
```

---

`template/fragment/footer :: copy`은 `template/fragment/footer.html` 템플릿에 있는 `th:fragment="copy"`라는 부분을 가져와서 사용하겠다는 의미이다.

- 부분 포함 insert: `th:insert`를 사용하면 현재 태그 `div` 내부에 추가한다.
- 부분 포함 replace: `th:replace`를 사용하면 현재 태그 `div`를 대체한다.
- 부분 포함 단순 표현식: `~{...}`를 사용하는 것이 원칙이지만 코드가 간단하면 생략이 가능하다.
- 파라미터 사용: `fragment`를 사용할 때 파라미터를 전달하여 동적으로 렌더링할 수 있다.

---

### Template Layout - 1

`fragment`의 경우 `footer`와 같은 일부분을 사용하는 방법이다.
이번에는 조금 더 큰 개념으로 `layout`을 사용하는 방법에 대해서 알아본다.

이러한 방법은 `header`에 포함되는 `css`, `javascript`와 같은 공통된 정보를 모든 HTML파일에 개별로 작성하는 것이 아니라 한 곳에 모아두고 사용하는 방법을 말한다.
공통된 부분을 하나의 파일로 만들어서 공유하고 각 페이지마다 다른 정보는 따로 작성하여 사용하면 된다.

#### 예시

**Controller**
```java
@GetMapping("/layout")
public String layout() {
    return "template/layout/layoutMain";
}
```

**base.html**
```html
<html xmlns:th="http://www.thymeleaf.org">
<head th:fragment="common_header(title, links)">
    <title th:replace="${title}">레이아웃 타이틀</title>
    <!-- 공통 -->
    <link rel="stylesheet" type="text/css" media="all" th:href="@{/css/awesomeapp.css}">
    <link rel="shortcut icon" th:href="@{/images/favicon.ico}">
    <script type="text/javascript" th:src="@{/sh/scripts/codebase.js}"></script>
    <!-- Custom -->
    <th:block th:replace="${links}" />
</head>
```

**layoutMain.html**
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head th:replace="template/layout/base :: common_header(~{::title}, ~{::link})">
    <title>메인 타이틀</title>
    <link rel="stylesheet" th:href="@{/css/bootstrap.min.css}">
    <link rel="stylesheet" th:href="@{/themes/smoothness/jquery-ui.css}">
</head>
<body>
메인 컨텐츠
</body>
</html>
```

**Result**
```html
<!DOCTYPE html>
<html>
<head>

    <title>메인 타이틀</title>

    <!-- 공통 -->
    <link rel="stylesheet" type="text/css" media="all" href="/css/awesomeapp.css">
    <link rel="shortcut icon" href="/images/favicon.ico">
    <script type="text/javascript" src="/sh/scripts/codebase.js"></script>

    <!-- Custom -->
    <link rel="stylesheet" href="/css/bootstrap.min.css"><link rel="stylesheet" href="/themes/smoothness/jquery-ui.css">

</head>
<body>
메인 컨텐츠
</body>
</html>
```

---

`common_header(~{::title}, ~{::link})` 부분을 확인해본다.
- `::title`: 현재 페이지의 title 태그들을 전달한다.
- `::link`: 현재 페이지의 link 태그들을 전달한다.

결과적으로 메인 타이틀이 전달한 부분으로 교체되었다.
공통 부분은 그대로 유지되고, 추가된 부분에 전달한 `link`들이 포함된 것을 확인할 수 있다.

`Layout`개념을 가지고 해당 `Layout`에 필요한 코드를 전달하여 완성하는 방식이다.

---

### Template Layout - 2

이전에 `<head>`을 적용시켜 보았다면 이번에는 `<html>` 전체에 적용해본다.

**Controller**
```java
@GetMapping("/layoutExtend")
public String layoutExtends() {
    return "template/layoutExtend/layoutExtendMain";
}
```

**layoutFile**
```html
<!DOCTYPE html>
<html th:fragment="layout (title, content)" xmlns:th="http://www.thymeleaf.org">
<head>
    <title th:replace="${title}">레이아웃 타이틀</title>
</head>
<body>
<h1>레이아웃 H1</h1>
<div th:replace="${content}">
    <p>레이아웃 컨텐츠</p>
</div>
<footer>
    레이아웃 푸터
</footer>
</body>
</html>
```

**layoutExtendMain**
```html
<!DOCTYPE html>
<html th:replace="~{template/layoutExtend/layoutFile :: layout(~{::title}, ~{::section})}"
      xmlns:th="http://www.thymeleaf.org">
<head>
    <title>메인 페이지 타이틀</title>
</head>
<body>
<section>
    <p>메인 페이지 컨텐츠</p>
    <div>메인 페이지 포함 내용</div>
</section>
</body>
</html>
```

**Result**
```html

<!DOCTYPE html>
<html>
<head>
    <title>메인 페이지 타이틀</title>
</head>
<body>
<h1>레이아웃 H1</h1>
<section>
    <p>메인 페이지 컨텐츠</p>
    <div>메인 페이지 포함 내용</div>
</section>
<footer>
    레이아웃 푸터
</footer>
</body>
</html>
```

---

`layoutFile.html`의 기본 레이아웃에 `<html>`에 `th:fragment` 속성이 정의되어 있다.
레이아웃 파일을 기본으로 하고 여기에 필요한 내용을 전달해서 일부분을 변경하는 것으로 이해하면 된다.
`layoutExtendMain.html`은 현재 페이지이지만 `<html>` 자체를 `th:replace`를 사용하여 변경한다.
결국 `layoutFile.html`에 필요한 내용을 전달하면서 `<html>`을 `layoutFile.html`로 변경해서 사용하였다.

---

**참고한 강의**:
- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%ED%95%B5%EC%8B%AC-%EC%9B%90%EB%A6%AC-%EA%B8%B0%EB%B3%B8%ED%8E%B8
- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-1
- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-2

**참고한 문서**:
- [Thymeleaf 공식 사이트](https://www.thymeleaf.org/)
- [Thymeleaf 기본 기능](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html)
- [Thymeleaf 스프링 통합](https://www.thymeleaf.org/doc/tutorials/3.0/thymeleafspring.html)