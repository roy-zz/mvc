이번 장에서는 타임리프의 스프링 통합 기능 중 `입력 폼`에 대해서 알아본다.
모든 코드는 [깃 허브(링크)](https://github.com/roy-zz/mvc) 에 올려두었다.

---

### 개요

타임리프는 크게 `기본 메뉴얼`과 `스프링 통합 메뉴얼` 2가지 메뉴얼을 제공한다.
타임리프는 스프링 없이도 동작하지만, 스프링과 통합을 위해 다양한 기능을 제공하며 이러한 이유로 많은 백엔드 개발자가 `타임리프`를 선택하고 있다.
스프링 통합으로 인해 추가되는 기능은 아래와 같다.

- 스프링의 SpringEL 문법
- ${@myBean.doSomething()}처럼 스프링 빈 호출 지원
- 편리한 폼 관리를 위한 추가 속성
  - `th:object`
  - `th:field`, `th:errors`, `th:errorclass`
- 폼 컴포넌트 기능
  - `checkbox`, `radio button`, `List`등을 편리하게 사용할 수 있는 기능 지원
- 스프링의 메시지, 국제화 기능의 편리한 통합
- 스프링의 검증, 오류 처리 통합
- 스프링의 변환 서비스 통합(ConversionService)

스프링 부트를 사용하는 경우 아래의 의존성 하나만으로 타임리프와 관련된 설정용 빈을 모두 자동으로 등록해준다.

```bash
implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
```

타임리프 관련 설정을 변경하는 경우에는 [여기](https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-application-properties.html#common-application-properties-templating) 를 참고하여 `application.yml`파일을 수정하면 된다.

---

### 입력 폼 처리

타임리프가 제공하는 입력 폼 기능을 적용해서 기존 프로젝트의 `폼 코드`를 효율적으로 개선해본다.

- `th:object`: 커맨드 객체를 지정한다.
- `*{...}`: 선택 변수 식이라고 한다. `th:object`에서 선택한 객체에 접근한다.
- `th:field`: HTML 태그의 `id`, `name`, `value` 속성을 자동으로 처리해준다.

예를 들어 렌더링 전에 `<input type="text" th:field="*{itemName}" />` 이러한 태그가 있었다면 렌더링 후에는 `<input type="text" id="itemName" name="itemName" th:value="*{itemName}" />`으로 변경된다.

---

### 등록 폼

`th:object`를 적용하려면 해당 오브젝트의 정보를 넘겨주어야 한다.
등록 폼이기 때문에 데이터가 비어있는 오브젝트를 만들어서 뷰에 전달한다.

#### 예시

**Item**
```java
@Data
@NoArgsConstructor
public class Item {
    private Long id;
    private String itemName;
    private Integer price;
    private Integer quantity;
    private Boolean open;
    private List<String> regions = Collections.emptyList();
    private ItemType itemType;
    private String deliveryCode;
    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
```

**Controller**
```java
@GetMapping("/add")
public String addForm(Model model) {
    model.addAttribute("item", new Item());
    return "form/addForm";
}
```

**addForm.html**
```html
    <!-- 생략...-->
    <form action="item.html" th:action th:object="${item}" method="post">
        <div>
            <label for="itemName">상품명</label>
            <input type="text" id="itemName" th:field="*{itemName}" class="form-control" placeholder="이름을 입력하세요">
        </div>
        <div>
            <label for="price">가격</label>
            <input type="text" id="price" th:field="*{price}" class="form-control" placeholder="가격을 입력하세요">
        </div>
        <div>
            <label for="quantity">수량</label>
            <input type="text" id="quantity" th:field="*{quantity}" class="form-control" placeholder="수량을 입력하세요">
        </div>
    <!-- 생략...-->
```

`Controller`에서 model에 item이라는 key로 빈 객체를 전달했다.
`th:object="${item}"`: `<form>`에서 사용할 객체를 지정한다. 선택 변수 식(`${...}`)을 적용할 수 있다.
`th:field="*{itemName}"`:
  - `*{itemName}`는 선택 변수 식을 사용했는데, `${item.itemName}`과 결과는 동일하다. 위에서 `th:object`로 `item`을 선택했기 때문에 선택 변수 식을 적용할 수 있다.
  - `id`: `th:field`에서 지정한 변수 이름과 같다.(id="itemName")
  - `name`: `th:field`에서 지정한 변수 이름과 같다.(name="itemName")
  - `value`: `th:field`에서 지정한 변수의 값을 사용한다.(value="")

`th:field`는 `id`,`name`,`value` 속성을 모두 만들어주기 때문에 생략되어도 동일하게 작동한다.

예를 들어 렌더링 전에 `name`과 `value`이 생략되어 `<input type="text" id="itemName" th:field="*{itemName}" class="form-control" placeholder="이름을 입력하세요">`라는 코드가 있다고 가정한다.
`th:field`에 의해 렌더링된 결과는 `<input type="text" id="itemName" class="form-control" placeholder="이름을 입력하세요" name="itemName" value="">`가 된다.

---

### 수정 폼

#### 예시

**Controller**
```java
@GetMapping("/{itemId}/edit")
public String editForm(@PathVariable Long itemId, Model model) {
    Item item = itemRepository.findById(itemId);
    model.addAttribute("item", item);
    return "form/editForm";
}
```

**editForm.html**
```html
    <!-- 생략 -->
    <form action="item.html" th:action th:object="${item}" method="post">
        <div>
            <label for="id">상품 ID</label>
            <input type="text" id="id" class="form-control" th:field="*{id}" readonly>
        </div>
        <div>
            <label for="itemName">상품명</label>
            <input type="text" id="itemName" class="form-control" th:field="*{itemName}" >
        </div>
        <div>
            <label for="price">가격</label>
            <input type="text" id="price" class="form-control" th:field="*{price}">
        </div>
        <div>
            <label for="quantity">수량</label>
            <input type="text" id="quantity" class="form-control" th:field="*{quantity}">
        </div>
    <!-- 생략 -->
```

`수정 폼` 역시 `등록 폼`과 비슷한 형태로 작성되어 있는 것을 확인할 수 있다.
`th:field`에 의해 `id`, `name`, `value`가 누락되더라도 자동으로 처리된다.

`th:object`, `th:field` 덕분에 불필요하게 반복되는 코드를 생략할 수 있었다.
이러한 장점은 추후 검증(Validation)에서 위력을 발휘한다.

---

**참고한 강의**:
- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%ED%95%B5%EC%8B%AC-%EC%9B%90%EB%A6%AC-%EA%B8%B0%EB%B3%B8%ED%8E%B8
- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-1
- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-2

**참고한 문서**:
- [Thymeleaf 기본 메뉴얼](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html)
- [Thymeleaf 스프링 통합 메뉴얼](https://www.thymeleaf.org/doc/tutorials/3.0/thymeleafspring.html)