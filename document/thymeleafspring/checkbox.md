[이전 장(링크)](https://imprint.tistory.com/251) 에서는 타임리프의 스프링 통합 기능 중 `입력 폼`에 대해서 알아보았다.
이번 장에서는 통합 기능 중 `체크박스`에 대해서 알아본다.
모든 코드는 [깃 허브(링크)](https://github.com/roy-zz/mvc) 에 올려두었다.

---

### Checkbox

#### Java Files

예제를 만들기 위한 `Class`, `Enum` 파일들을 살펴본다.

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
```

**DeliveryCode**
```java
@Data
@AllArgsConstructor
public class DeliveryCode {
    private String code;
    private String displayName;
}
```

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

---

### 단일 체크박스 (히든 필드)

**Controller**
```java
@PostMapping("/add")
public String addItem(@ModelAttribute Item item, RedirectAttributes redirectAttributes) {
    log.info("item.open={}", item.getOpen());
    // 생략...
}
```

**addForm.html**
```html
    <!-- 생략 -->
    <div>판매 여부</div>
    <div>
        <div class="form-check">
            <input type="checkbox" id="open" name="open" class="form-check-input">
            <label for="open" class="form-check-label">판매 오픈</label>
        </div>
    </div>
    <!-- 생략 -->
```

컨트롤러의 `item.getOpen()`의 결과의 출력에 집중한다.
출력되는 로그를 확인해보면 체크 박스를 선택하지 않는 경우 `item.open`의 결과가 `false`가 아닌 `null`인 것을 알 수 있다.

```bash
[nio-8080-exec-4] c.r.m.i.web.form.FormItemController      : item.open=true // 체크 박스를 선택하는 경우
[nio-8080-exec-7] c.r.m.i.web.form.FormItemController      : item.open=null // 체크 박스를 선택하지 않는 경우
```

이러한 결과는 백엔드 개발자인 우리가 선호하는 결과가 아니지만 HTML checkbox가 이러한 방식으로 구현되어 있으므로 우리는 이에 대한 대응을 해야한다.
`_open`과 같이 우리가 원하는 체크 박스의 이름 앞에 언더스코어를 붙여서 모든 경우에 전송되는 히든 필드를 만들어준다.

```html
    <!-- 생략 -->
    <div>판매 여부</div>
    <div>
        <div class="form-check">
            <input type="checkbox" id="open" name="open" class="form-check-input">
            <input type="hidden" id="_open" value="on">
            <label for="open" class="form-check-label">판매 오픈</label>
        </div>
    </div>
    <!-- 생략 -->
```

출력되는 결과를 보면 우리가 원하는 결과가 출력되는 것을 확인할 수 있다.

```bash
[nio-8080-exec-4] c.r.m.i.web.form.FormItemController      : item.open=true // 체크 박스를 선택하는 경우
[nio-8080-exec-7] c.r.m.i.web.form.FormItemController      : item.open=false // 체크 박스를 선택하지 않는 경우
```

체크박스를 체크하는 경우 스프링 MVC가 `open`에 값이 있는 것을 확인하고 `_open`은 무시한다.
체크박스를 체크하지 않는 경우 `open`이 없고 `_open`만 있기 때문에 체크박스가 체크되지 않았다고 판단한다.

---

### 단일 체크박스 (폼 기능)

체크박스가 체크되지 않는 경우 상태가 `null`로 발생하는 것을 막기위해 히든필드를 사용하는 방법에 대해서 알아보았다.
하지만 이렇게 모든 체크박스에 히든필드를 붙여주는 것은 중복되는 코드를 발생시킨다.
타임리프가 제공하는 `폼 기능`을 사용하여 이러한 부분을 자동으로 처리해본다.

변경된 HTML파일은 아래와 같다.

**addForm.html**
```html
    <!-- 생략 -->
    <div>판매 여부</div>
    <div>
        <div class="form-check">
            <input type="checkbox" id="open" th:field="*{open}" class="form-check-input">
            <label for="open" class="form-check-label">판매 오픈</label>
        </div>
    </div>
    <!-- 생략 -->
```

프로젝트를 재실행시키고 해당 파일을 렌더링하는 주소로 접속하여 HTML 파일을 확인해본다.
우리가 이전에 작성한 것과 동일하게 히든필드가 자동으로 붙어있는 것을 확인할 수 있다.

**Result**
```html
    <!-- 생략 -->
    <div>판매 여부</div>
    <div>
        <div class="form-check">
            <input type="checkbox" id="open" class="form-check-input" name="open" value="true"><input type="hidden" name="_open" value="on"/>
            <label for="open" class="form-check-label">판매 오픈</label>
        </div>
    </div>
    <!-- 생략 -->
```

출력 결과 또한 우리가 예상한 것과 동일하다.

```bash
[nio-8080-exec-2] c.r.m.i.web.form.FormItemController      : item.open=true // 체크 박스를 선택하는 경우
[nio-8080-exec-3] c.r.m.i.web.form.FormItemController      : item.open=false // 체크 박스를 선택하지 않는 경우
```

**참고**
- `th:object`를 사용하지 않으면 그래프 탐색 방식(ex. item.open)과 같은 방식으로 사용해야한다.
- `disabled`를 사용하면 사용자가 체크박스를 체크하지 못하도록 할 수 있다.
- `th:field`를 사용하면 값이 `true`인 경우 체크를 자동으로 처리해준다.

---

### 다중 체크박스

다중 체크박스를 사용하여 하나 이상의 값을 체크할 수 있도록 해본다.

체크박스의 목록에 사용될 `regions Map`을 반환하는 메서드를 정의하였다.
해당 메서드에는 `@ModelAttribute` 애노테이션을 사용하였다.

모든 컨트롤러에서 `Model`에 공통적으로 넣어주어야하는 속성이 있다고 가정하였을 때 모든 코드에서 `model.addAttribute(...)`과 같은 방식으로 데이터를 넣어주어야 한다.
`@ModelAttribute`를 컨트롤러의 메서드 레벨에 정의하는 경우 해당 컨트롤러를 요청할 때 자동으로 `model`에 담기도록 할 수 있다.

```java
@ModelAttribute("regions")
public Map<String, String> regions() {
    Map<String, String> regions = new LinkedHashMap<>();
    regions.put("SEOUL", "서울");
    regions.put("BUSAN", "부산");
    regions.put("JEJU", "제주");
    return regions;
}
```

위에서 정의한 `regions`를 체크박스로 만드는 코드를 확인해본다.

**addForm.html**
```html
    <!-- 생략 -->
    <div>
        <div>등록 지역</div>
        <div th:each="region : ${regions}" class="form-check form-check-inline">
            <input type="checkbox" th:field="*{regions}" th:value="${region.key}" class="form-check-input">
            <label th:for="${#ids.prev('regions')}"
                   th:text="${region.value}" class="form-check-label">서울</label>
        </div>
    </div>
    <!-- 생략 -->
```

- `th:for="${#ids.prev('regions')}"`: 다중 체크박스의 경우 동일한 `name`으로 생성할 수 있다. 하지만 `id`는 고유해야한다. 
타임리프는 `each`루프 안에서 반복해서 만들 때 임의로 1, 2, 3 숫자를 뒤에 붙여 고유한 `id`를 만들어준다.
주의해야하는 부분은 HTML의 id가 동적으로 만들어지기 때문에 `<label for="id">`와 같은 정적인 방식으로는 개발이 불가능하다.
타임리프에서 제공하는 `ids.prev(...)`, `ids.next(...)`을 사용하여 `label`이 바라보는 `id`도 동적으로 생성되도록 해야한다.

우리가 예상한 결과가 맞는지 렌더링된 웹 브라우저에서 `소스 보기`를 선택하여 결과를 확인해본다.

```html
    <!-- 생략 -->
    <div>
        <div>등록 지역</div>
        <div class="form-check form-check-inline">
            <input type="checkbox" value="SEOUL" class="form-check-input" id="regions1" name="regions"><input type="hidden" name="_regions" value="on"/>
            <label for="regions1"
                   class="form-check-label">서울</label>
        </div>
        <div class="form-check form-check-inline">
            <input type="checkbox" value="BUSAN" class="form-check-input" id="regions2" name="regions"><input type="hidden" name="_regions" value="on"/>
            <label for="regions2"
                   class="form-check-label">부산</label>
        </div>
        <div class="form-check form-check-inline">
            <input type="checkbox" value="JEJU" class="form-check-input" id="regions3" name="regions"><input type="hidden" name="_regions" value="on"/>
            <label for="regions3"
                   class="form-check-label">제주</label>
        </div>
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