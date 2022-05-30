[이전 장(링크)](https://imprint.tistory.com/265) 에서는 기본적인 방법으로 파라미터의 유효성을 검사하는 방법에 대해서 알아본다.
이번 장에서는 스프링의 메시지 기능을 사용하여 체계적인 오류 코드와 메시지 처리 방법에 대해서 알아본다.
모든 코드는 [깃허브(링크)](https://github.com/roy-zz/mvc) 에 올려두었다.

---

### 메시지 사용

`message.properties` 파일과 `errors.properties` 파일을 추가하고 스프링 부트가 해당 메시지 파일을 인식할 수 있게 `application.properties` 파일을 수정한다.

**application.properties**
```yaml
spring.messages.basename=messages,errors
```

**errors.properties**
```yaml
required.item.itemName=상품 이름은 필수입니다. 
range.item.price=가격은 {0} ~ {1} 까지 허용합니다. 
max.item.quantity=수량은 최대 {0} 까지 허용합니다. 
totalPriceMin=가격 * 수량의 합은 {0}원 이상이어야 합니다. 현재 값 = {1}
```
  
`FieldError`와 `ObjectError`의 생성자는 파라미터로 `errorCode`와 `arguments`를 전달받는다.  
오류가 발생하는 경우 코드로 메시지를 찾기 위해 사용되며 메시지에 사용되는 매개변수를 지정할 수 있다.

```java
public String addItem(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
    if (!StringUtils.hasText(item.getItemName())) {
        bindingResult.addError(new FieldError("item", "itemName", item.getItemName(), false, new String[]{"required.item.itemName"}, null, null));
    }
    if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
        bindingResult.addError(new FieldError("item", "price", item.getPrice(), false, new String[]{"range.item.price"}, new Object[]{1000, 1000000}, null));
    }
    if (item.getQuantity() == null || item.getQuantity() >= 9999) {
        bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(), false, new String[]{"max.item.quantity"} ,new Object[]{9999}, null));
    }
    if (item.getPrice() != null && item.getQuantity() != null) {
        int resultPrice = item.getPrice() * item.getQuantity();
        if (resultPrice < 10000) {
            bindingResult.addError(new ObjectError("item",new String[]{"totalPriceMin"} ,new Object[]{10000, resultPrice}, null));
        }
    }
    if (bindingResult.hasErrors()) {
        log.info("errors={} ", bindingResult);
    }
}
```

- `codes`는 `required.item.itemName`와 같이 메시지 코드의 키값을 지정한다.  
  만약 메시지 코드가 하나가 아니라 배열로 전달된다면 순서대로 매칭하여 처음에 매칭되는 메시지가 사용된다.  
- `arguments`는 `Object[{...}]`와 같이 메시지에 사용될 변수를 전달할 수 있다.

---

### FieldError, ObjectError 생략

매번 검증하는 코드에 `FieldError`, `ObjectError` 객체를 생성해야 하는데 이 부분을 수정하고 오류 코드도 더 간단하게 지정 가능하도록 수정해본다.  
우리는 컨트롤러에서 `BindingResult`는 검증해야 할 객체인 `target`바로 다음에 오도록 순서를 지정하기 때문에 `BindingResult` 객체는 자신이 검증해야 하는 객체인 `target`을 알고 있다.
  
이미 `BindingResult`가 검증해야 하는 객체를 알고 있기 때문에 직접 `FieldError`, `ObjectError`를 생성하지 않고 `rejectValue`, `reject` 메서드를 사용하여 검증 오류를 다룰 수 있다.

```java
public String addItem(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
    if (!StringUtils.hasText(item.getItemName())) {
        bindingResult.rejectValue("itemName", "required");
    }
    if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
        bindingResult.rejectValue("price", "range", new Object[]{1000, 10000000}, null);
    }
    if (item.getQuantity() == null || item.getQuantity() >= 9999) {
        bindingResult.rejectValue("quantity", "max", new Object[]{9999}, null);
    }
    if (item.getPrice() != null && item.getQuantity() != null) {
        int resultPrice = item.getPrice() * item.getQuantity();
        if (resultPrice < 10000) {
            bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
        }
    }
    if (bindingResult.hasErrors()) {
        log.info("errors={} ", bindingResult);
    }
}
```

**rejectValue**
```java
void rejectValue(@Nullable String field, String errorCode, @Nullable Object[] errorArgs, @Nullable String defaultMessage);
```

- field: 오류가 발생한 필드의 이름을 의미한다.
- errorCode: 오류 코드로 메시지에 등록된 코드가 아니라 뒤에서 설명할 `messageResolver`를 위한 오류 코드다.
- errorArgs: 오류 메시지에서 `{0}`을 치환하기 위한 값으로 사용된다.
- defaultMessage: 오류 메시지를 찾을 수 없을 때 사용하는 기본 메시지다.
  
`BindingResult`는 매개변수의 위치를 기반으로 어떤 객체를 검증하는지 알고 있기 떄문에 `target`에 대한 정보없이 검증이 가능하다.
  
`FieldError()`를 직접 다룰 때는 오류 코드를 `range.item.price`와 같이 모두 입력했지만 `rejectValue`를 사용하면 오류코드를 사용하는 경우에는 간단하게 `range`로 입력하였다.  
하지만 스프링에서는 우리가 원하는 메시지를 찾아서 출력하였다. 이렇게 작동하는지 이해하기 위해서는 **MessageCodesResolver**를 이해해야 한다.

---

### 메시지 범용 활용

오류 메시지의 경우 특정 필드에 종속되도록 자세하게 만드는 경우와 그렇지 않게 범용적으로 만드는 경우가 있다.

**특정 필드에 종속되는 경우**
- required.item.itemName: 상품 이름은 필수 입니다.
- range.item.price: 상품의 가격 범위 오류 입니다.
  
**범용적으로 만드는 경우**
- required: 필수 값 입니다.
- range: 범위 오류 입니다.

범용적으로 만드는 경우 재사용성이 높아지지만 세밀한 메시지를 작성하기에는 무리가 있다.  
하지만 특정 필드에 종속적으로 만드는 경우에는 특정 필드와 같은 속성을 가지고 있는 곳에만 사용할 수 있다는 단점이 있다.  
좋은 방법으로는 범용성으로 사용하다가, 세밀하게 작성해야 하는 경우에는 세밀한 내용이 적용되도록 메시지에 단계를 두는 방법이 있다.

예를 들어 `required`라고하는 오류 코드가 있다.

```yaml
required: 필수 값 입니다.
```

그런데 오류 메시지에 `required.item.itemName`과 같이 특정 객체명과 필드명에 종속되는 세밀한 메시지가 있으면 이 메시지를 높은 우선순위로 사용하는 것이다.

```yaml
# Level1
required.item.itemName: 상품 이름은 필수 입니다.
# Level2
required: 필수 값 입니다.
``` 

세밀한 메시지가 있는 경우 우선 확인하고, 없으면 범용적인 메시지를 선택하도록 개발하면 된다.  
범용성 있게 잘 개발해두면, 간단한 메시지 추가로 편리하게 오류 메시지를 관리할 수 있게 된다.
  
스프링은 **MessageCodesResolver**를 통해서 이러한 기능을 지원한다.

---

### MessageCodesResolver

`MessageCodesResolver`는 검증 오류 코드로 메시지 코드들을 생성한다.  
`MessageCodesResolver`는 인터페이스이며 `DefaultMessageCodesResolver`가 기본 구현체이다. 주로 `ObjectError`와 `FieldError`와 함께 사용된다.

#### DefaultMessageCodesResolver의 기본 메시지 생성 규칙

**객체 오류**
객체 오류의 경우 아래와 같은 순서로 2가지 생성

1. code + "." + object name
2. code

예) 오류 코드: required, object name: item
1. required.item
2. required

**필드 오류**
필드 오류의 경우 다음 순서로 4가지 메시지 코드 생성
1. code + "." + object name + "." + field
2. code + "." + field
3. code + "." + field type
4. code

예) 오류 코드: typeMismatch, object name "user", field "age", field type: int
1. "typeMismatch.user.age"
2. "typeMismatch.age"
3. "typeMismatch.int"
4. "typeMismatch"

**동작 방식**

`rejectValue`, `reject`는 내부에서 `MessageCodesResolver`를 사용하며 여기에서 메시지 코드들을 생성한다.
`FieldError`, `ObjectError`의 생성자를 보면, 오류 코드를 하나가 아니라 여러 오류 코드를 가질 수 있으며 `MessageCodesResolver`를 통해서 생성된 순서대로 오류 코드를 보관한다.  

**FieldError** `rejectValue("itemName", "required")`
아래와 같은 4가지 오류 코드를 자동으로 생성한다.
- required.item.itemName
- required.item.Name
- required.java.lang.String
- required

**ObjectError** `reject("totalPriceMin")`
아래와 같은 2가지 오류 코드를 자동으로 생성한다.
- totalPriceMin.item
- totalPriceMin

---

### 스프링의 오류 메시지 처리

검증 오류 코드는 **개발자가 직접 `rejectValue()`를 호출**하는 경우와 **스프링이 검증 오류에 추가**(주로 타입 캐스팅 오류)한 경우가 있다.  
숫자가 입력되어야 하는 필드에 문자열을 입력하면 `BindingResult`에는 개발자가 직접 입력하지 않아도 `FieldError`가 담겨있는 것을 확인할 수 있다.  
`BindingResult`를 사용하는 경우 타입 캐스팅 시 오류가 발생하여도 컨트롤러 코드에 진입한다.  

```
codes[typeMismatch.item.price,typeMismatch.price,typeMismatch.java.lang.Integer,typeMismatch]
```

스프링은 타입 오류가 발생하면 `typeMismatch`라는 오류 코드를 사용하며 해당 오류 코드가 `MessageCodesResolver`를 통해서 아래와 같이 네 개의 메시지 코드를 생성한다.  
  
- typeMismatch.item.price
- typeMismatch.price
- typeMismatch.java.lang.Integer
- typeMismatch
  
이렇게 스프링에서 자동으로 메시지 코드를 생성하기 때문에 우리는 `error.properties`와 같은 설정파일만 수정하면 코드의 수정없이 원하는 메시지를 단계별로 설정할 수 있다.  
(`Spring Cloud`를 사용하면 애플리케이션 재실행없이 설정파일을 적용할 수 있다. 즉, 애플리케이션의 재실행없이 화면에 표시되는 문구를 바꿀수 있게 된다.)

---

### Validator(검증기) 분리

지금까지 우리가 작성한 코드는 검증을 위한 코드가 컨트롤러 코드의 대부분을 차지하면서 가독성을 떨어뜨렸다.  
이는 `AOP`적인 관점에서 보았을 때 좋지 못한 방식이므로 검증을 위한 기능을 별도의 클래스로 역할을 분리해본다.
  
1. `Item` 엔티티를 검증하는 `ItemValidator` 클래스를 생성한다.
  
**ItemValidator**
```java
@Component
public class ItemValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Item.class.isAssignableFrom(clazz);
    }
    @Override
    public void validate(Object target, Errors errors) {
        Item item = (Item) target;
        if (!StringUtils.hasText(item.getItemName())) {
            errors.rejectValue("itemName", "required");
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            errors.rejectValue("price", "range", new Object[]{1000, 10000000}, null);
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            errors.rejectValue("quantity", "max", new Object[]{9999}, null);
        }

        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                errors.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }
    }
}
```

우리가 작성한 `ItemValidator` 클래스는 스프링의 `Validator` 인터페이스를 구현하고 있기 때문에 스프링에서 체계적인 검증이 가능해진다.
  
`support()` 메서드는 `isAssignableFrom()` 메서드를 사용하여 호환되는 클래스인지 확인한다.    
- isAssignableFrom: 특정 **Class**가 어떠한 클래스/인터페이스를 상속/구현했는지 확인한다.
- instanceof: 특정 **Object**가 어떠한 클래스/인터페이스를 상속/구현했는지 확인한다.
  
`validate()` 메서드는 실제로 우리가 필요로하는 검증 로직을 구현하고 있다.  
  
2. `WebDataBinder` 연동
  
스프링의 `@InitBinder` 애노테이션을 사용하여 `WebDataBinder`를 통해 해당 컨트롤러에 `Validator`(검증기)를 자동으로 적용할 수 있다.  
`@InitBinder`를 통한 적용 방법은 해당 컨트롤러에만 적용된다.

```java
@InitBinder
public void init(WebDataBinder dataBinder) {
    dataBinder.addValidators(itemValidator);
}
```

3. `@Validated` 애노테이션 추가

```java
@PostMapping("/add")
public String addItemV6(
    @Validated @ModelAttribute Item item, 
    BindingResult bindingResult, 
    RedirectAttributes redirectAttributes, 
    Model model) {
    // ...
}
```

`@Validated` 애노테이션을 우리가 검증하려는 대상에 붙여주면 `WebDataBinder`에 등록된 검증기를 찾아서 실행한다.  
여러 검증기가 등록되어 있다면 우리가 1단계에서 작성한 `support()` 메서드를 통해서 어떠한 검증기가 실행되야 하는지 확인한다.  
(마치 `DispatcherServlet`의 프론트 컨트롤러 패턴과 유사하게 동작)
  
우리는 대상을 검증하기 위해 대상에게 `@Validated`를 붙여주었다.  
`@Validated`이외에 `@Valid`라는 애노테이션도 사용이 가능하며 동일하게 동작한다.  
`@Validated`의 경우 스프링 전용 검증 애노테이션이고 `@Valid`는 자바 표준 검증 애노테이션이다.

---

**참고한 강의**:
- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%ED%95%B5%EC%8B%AC-%EC%9B%90%EB%A6%AC-%EA%B8%B0%EB%B3%B8%ED%8E%B8
- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-1
- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-2

**참고한 문서**:
- [Thymeleaf 기본 메뉴얼](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html)
- [Thymeleaf 스프링 통합 메뉴얼](https://www.thymeleaf.org/doc/tutorials/3.0/thymeleafspring.html)