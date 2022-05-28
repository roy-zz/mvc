이번 장에서는 클라이언트가 전달한 파라미터의 유효성을 검사하는 방법에 대해서 알아본다.  
모든 코드는 [깃허브(링크)](https://github.com/roy-zz/mvc) 에 올려두었다.

---

### Validation(유효성 검사)

**컨트롤러**의 중요한 역할중 하나는 HTTP 요청이 정상인지 검증하는 것이다.  
그리고 정상 로직보다 이런 검증 로직을 잘 개발하는 것이 더 어려울 수 있다.

참고로 클라이언트에서 파라미터를 검증하는 것과 서버에서 파라미터를 검증하는 차이는 아래와 같다.

- 클라이언트 검증은 조작할 수 있으므로 보안에 취약하다.  
  예를 들어 API 요청에 필요한 정보를 직접 `포스트 맨`이나 `curl`로 요청하는 경우가 있다.
- 서버만에서만 검증하면, 즉각적인 고객 사용성이 부족해진다.  
  예를 들어 비밀번호 유효성의 경우 요청버튼을 클릭 하였을 때가 아니라 입력하는 즉시 화면에 표시되어야 한다.
- 툴을 적절히 섞어서 사용하되, 최종적으로 서버에서 검증을 해야 한다.
- API 방식을 사용하면 API 스펙을 잘 정의해서 검증 오류를 API 응답 결과에 잘 남겨주어야 한다.  
  단순히 400 (Bad Request)만 클라이언트에게 전달하는 경우 클라이언트는 어떤 값에 문제가 있는 것인지 알기 힘들다.

---

### BindingResult - 1

`BindingResult`는 스프링이 제공하는 검증 오류를 처리하는 방법으로 검증 오류가 발생하면 저장된다.  
`BindingResult`의 위치는 유효성을 검증하려는 파라미터인 `@ModelAttribute Item item` 뒤로 해야 한다.  
사용법은 아래와 같다.

```java
public String addItem(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
    if (!StringUtils.hasText(item.getItemName())) {
        bindingResult.addError(new FieldError("item", "itemName", "상품 이름은 필수 입니다."));
    }
    if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
        bindingResult.addError(new FieldError("item", "price", "가격은 1,000 ~ 1,000,000 까지 허용합니다."));
    }
    if (item.getQuantity() == null || item.getQuantity() >= 9999) {
        bindingResult.addError(new FieldError("item", "quantity", "수량은 최대 9,999 까지 허용합니다."));
    }
    if (item.getPrice() != null && item.getQuantity() != null) {
        int resultPrice = item.getPrice() * item.getQuantity();
        if (resultPrice < 10000) {
            bindingResult.addError(new ObjectError("item", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
        }
    }
    if (bindingResult.hasErrors()) {
        log.info("errors={} ", bindingResult);
    }
}
``` 

필드에 오류가 있는 경우 `FieldError` 객체를 생성하여 `BindingResult`의 `addError` 메서드를 통해 담아두면 된다.

```java
public FieldError(String objectName, String field, String defaultMessage) {}
```

예시에서 `objectName`의 경우 검증하려는 객체이므로 "item"이 된다.  
`field` 오류가 발생한 필드의 이름을 지정하면 된다. `defaultMessage`에는 오류가 발생한 경우 클라이언트에 전달할 메시지를 의미한다.

```java
public ObjectError(String objectName, String defaultMessage) {}
```

특정 필드에 문제가 발생한 것이 아니라 파라미터 전반에 문제가 발생하였다면 `ObjectError` 객체를 생성하여 `BindingResult`의 `addError` 메서드를 통해 담아두면 된다.
파라미터의 종류는 `FieldError`와 동일하다.

---

### BindingResult - 2

`BindingResult`가 파라미터에 있으면 `@ModelAttribute`에 데이터 바인딩 시 오류가 발생해도 컨트롤러가 호출된다.  
만약 `@ModelAttribute`에 바인딩하는 시점에 타입 오류가 발생한다면 `BindingResult`가 없는 경우에는 400 오류가 발생하면서 컨트롤러가 호출되지 않고, 오류 페이지로 이동된다.  
만약 `BindingResult`가 있다면 오류 정보인 `FieldError`를 `BindingResult`에 담아서 컨트롤러가 정상 호출된다.  
  
`BindingResult`에 검증 오류를 적용하는 3가지 방법으로는 아래와 같은 방법이 있다.
  
- `@ModelAttribute`의 객체에 타입 오류 등으로 바인딩이 실패하는 경우에 스프링이 `FieldError`를 생성해서 `BindingResult`에 넣어준다.  
- 개발자가 직접 `BindingResult`에 `FieldError`를 생성해서 넣어준다.
- `Vilidator`을 사용해서 검증한다.

```java
public interface BindingResult extends Errors {
    // 
}
public interface Errors {
    //
}
```

`BindingResult`는 인터페이스이며 `Errors` 인터페이스를 상속받고 있다.  
실제로 넘어오는 것은 `BeanPropertyBindingResult`라는 구현체이며 `BindingResult`와 `Errors`를 전부 구현하고 있으므로 `BindingResult` 대신 `Errors`를 사용해도 된다.  
`Errors` 인터페이스는 단순한 오류 저장과 조회 기능을 제공하며 `BindingResult`는 추가적인 기능들을 제공한다.  
`addError` 메서드도 `BindingResult`가 제공하는 메서드이므로 관례상 `Errors`보다 많이 사용된다.

BindingResult - 1 에서는 오류 메시지를 사용자에게 보여줄 때 사용자가 입력한 값을 보여줄 수 없었다.  
사용자들이 입력한 값은 무엇이며 왜 잘못되었는지를 보여주기 위해 `FieldError`의 다른 생성자를 사용해본다.

```java
public FieldError(String objectName, String field, String defaultMessage);
public FieldError(String objectName, String field, @Nullable Object rejectedValue, boolean bindingFailure, @Nullable String[] codes, @Nullable Object[] arguments, @Nullable String defaultMessage)
```

수정된 코드는 아래와 같다.

```java
public String addItem(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
    if (!StringUtils.hasText(item.getItemName())) {
        bindingResult.addError(new FieldError("item", "itemName", item.getItemName(), false, null, null, "상품 이름은 필수 입니다."));
    }
    if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
        bindingResult.addError(new FieldError("item", "price", item.getPrice(), false, null, null, "가격은 1,000 ~ 1,000,000 까지 허용합니다."));
    }
    if (item.getQuantity() == null || item.getQuantity() >= 9999) {
        bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(), false, null ,null, "수량은 최대 9,999 까지 허용합니다."));
    }
    if (item.getPrice() != null && item.getQuantity() != null) {
        int resultPrice = item.getPrice() * item.getQuantity();
        if (resultPrice < 10000) {
            bindingResult.addError(new ObjectError("item",null ,null, "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
        }
    }
    if (bindingResult.hasErrors()) {
        log.info("errors={} ", bindingResult);
        return "validation/v2/addForm";
    }
}
```

사용자의 입력 데이터가 컨트롤러의 `@ModelAttribute`에 바인딩되는 시점에 타입 오류가 발생하면 객체는 입력받은 값을 가지고 있을 수 없다.
예를 들어 Integer 타입이 들어와야 하는 필드에 문자열이 입력된 경우 Integer 타입의 필드는 문자열 값을 저장할 수 없다.  
오류가 발생한 경우 사용자 입력 값을 보관하는 별도의 방법이 필요하고 `FieldError`는 오류 발생시 사용자가 입력한 값을 저장하는 기능을 제공한다.  
  
`FieldError`의 생성자에서 `rejectedValue` 필드가 오류 발생시 사용자 입력 값을 저장하는 필드다.  
`bindingFailure`는 타입 오류와 같은 바인딩이 실패했는지 여부를 적어주면 된다.
  
타입 오류로 바인딩에 실패하면 스프링은 `FieldError`를 생성하면서 사용자가 입력한 값을 넣어둔다.  
해당 오류를 `BindingResult`에 담아서 컨트롤러를 호출하기 때문에 타입 오류와 같은 바인딩 실패시에도 사용자의 오류 메시지를 정상 출력할 수 있다.

---

**참고한 강의**:
- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%ED%95%B5%EC%8B%AC-%EC%9B%90%EB%A6%AC-%EA%B8%B0%EB%B3%B8%ED%8E%B8
- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-1
- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-2

**참고한 문서**:
- [Thymeleaf 기본 메뉴얼](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html)
- [Thymeleaf 스프링 통합 메뉴얼](https://www.thymeleaf.org/doc/tutorials/3.0/thymeleafspring.html)