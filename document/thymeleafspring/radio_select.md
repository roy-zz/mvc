[이전 장(링크)](https://imprint.tistory.com/252) 에서는 타임리프의 스프링 통합 기능 중 `체크박스`에 대해서 알아보았다.
이번 장에서는 통합 기능 중 `라디오 버튼`과 `셀렉트 박스`에 대해서 알아본다.
모든 코드는 [깃 허브(링크)](https://github.com/roy-zz/mvc) 에 올려두었다.

---

### Radio Button

`라디오 버튼`은 여러 선택지 중에서 하나를 선택할 때 사용할 수 있다.
이번에는 `라디오 버튼`을 자바 `Enum`을 사용하여 활용하는 방법에 대해서 알아본다.

**ItemType**
```java
@Getter
@RequiredArgsConstructor
public enum ItemType {
    BOOK("도서"),
    FOOD("음식"),
    ETC("기타");
    private final String description;
}

@ModelAttribute("itemTypes")
public ItemType[] itemTypes() {
    return ItemType.values();
}
```

`ItemType` 또한 컨트롤러에서 대부분 사용되는 기능이므로 `@ModelAttribute`를 사용하였다.
`values()`를 사용하면 `Enum`의 모든 정보를 배열로 반환한다.

**addForm.html**
```html
    <!-- 생략 -->
    <div>
        <div>상품 종류</div>
        <div th:each="type : ${itemTypes}" class="form-check form-check-inline">
            <input type="radio" th:field="*{itemType}" th:value="${type.name()}" class="form-check-input">
            <label th:for="${#ids.prev('itemType')}" th:text="${type.description}" class="form-check-label">
                BOOK
            </label>
        </div>
    </div>
    <!-- 생략 -->
```

랜더링된 페이지로 이동하여 `소스 보기`를 선택하고 완성된 HTML 파일을 확인해본다.

**Result**
```html
    <!-- 생략 -->
    <div>
        <div>상품 종류</div>
        <div class="form-check form-check-inline">
            <input type="radio" value="BOOK" class="form-check-input" id="itemType1" name="itemType">
            <label for="itemType1" class="form-check-label">도서</label>
        </div>
        <div class="form-check form-check-inline">
            <input type="radio" value="FOOD" class="form-check-input" id="itemType2" name="itemType">
            <label for="itemType2" class="form-check-label">음식</label>
        </div>
        <div class="form-check form-check-inline">
            <input type="radio" value="ETC" class="form-check-input" id="itemType3" name="itemType">
            <label for="itemType3" class="form-check-label">기타</label>
        </div>
    </div>
    <!-- 생략 -->
```

`체크박스`의 경우 체크를 선택하지 않는 경우 데이터가 넘어가지 않기 때문에 `히든필드`를 사용하였다.
`라디오 버튼`의 경우 아무것도 선택되어 있지 않은 경우 결과는 `null`로 발생하고 이러한 결과가 우리가 기대하는 결과이기 때문에 `히든필드`를 사용하지 않았다.

**참고**
HTML에서 타임리프를 사용하여 아래와 같이 자바의 `Enum`에 바로 접근이 가능하다.
하지만 `Enum` 파일을 지정하는 방식이 단순 문자열이기 때문에 추후 패키지 구조의 변경이나 클래스 명이 변경되면 많은 이슈가 발생할 수 있으므로 추천되는 방법은 아니다.

```html
<div th:each="type : ${T(hello.itemservice.domain.item.ItemType).values()}"</div>
```

---

### Select Box

`셀렉트 박스`는 여러 선택지 중에서 하나를 선택할 때 사용된다.
이번에는 `셀렉트 박스`를 자바 객체를 사용하여 활용하는 방법에 대해서 알아본다.

사용자는 아래의 `deliveryCodes` 리스트에 담긴 `DeliveryCode` 객체 선택지 중에서 선택할 수 있다.
(셀렉트 박스를 위한 목록을 만들기 위해 매번 `new`를 사용해여 새로운 객체를 생성하는 것은 자원 낭비이므로 `static` 영역에 생성하여 사용하는 방법이 좋지만 예시에서는 가독성을 위해 매번 생성하여 사용하는 방식으로 진행한다.)

```java
@ModelAttribute("deliveryCodes")
public List<DeliveryCode> deliveryCodes() {
    return List.of(
            new DeliveryCode("FAST", "빠른 배송"),
            new DeliveryCode("NORMAL", "일반 배송"),
            new DeliveryCode("SLOW", "느린 배송")
    );
}

@Data
@AllArgsConstructor
public class DeliveryCode {
    private String code;
    private String displayName;
}
```

**addForm.html**
```html
    <!-- 생략 -->
    <div>
        <div>배송 방식</div>
        <select th:field="*{deliveryCode}" class="form-select">
            <option value="">==배송 방식 선택==</option>
            <option th:each="deliveryCode : ${deliveryCodes}" th:value="${deliveryCode.code}"
                    th:text="${deliveryCode.displayName}">FAST</option>
        </select>
    </div>
    <!-- 생략 -->
```

해당 셀렉트 박스가 렌더링 되는 페이지로 이동하여 `소스보기`를 선택하고 완성된 HTML 파일을 확인해본다.

```html
    <!-- 생략 -->
    <div>
        <div>배송 방식</div>
        <select class="form-select" id="deliveryCode" name="deliveryCode">
            <option value="">==배송 방식 선택==</option>
            <option value="FAST">빠른 배송</option>
            <option value="NORMAL">일반 배송</option>
            <option value="SLOW">느린 배송</option>
        </select>
    </div>
    <!-- 생략 -->
```

---

**참고한 강의**:
- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%ED%95%B5%EC%8B%AC-%EC%9B%90%EB%A6%AC-%EA%B8%B0%EB%B3%B8%ED%8E%B8
- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-1
- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-2

**참고한 문서**:
- [Thymeleaf 기본 메뉴얼](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html)
- [Thymeleaf 스프링 통합 메뉴얼](https://www.thymeleaf.org/doc/tutorials/3.0/thymeleafspring.html)