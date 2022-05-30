[이전 장(링크)](https://imprint.tistory.com/266) 에서는 스프링의 메시지 기능을 사용하여 체계적인 오류 코드와 메시지 처리 방법에 대해서 알아보았다.
이번 장에서는 스프링의 `Bean Validation`을 사용한 검증 방법에 대해서 알아본다.
모든 코드는 [깃 허브(링크)](https://github.com/roy-zz/mvc) 에 올려두었다.

---

### Bean Validation

우리는 자바 코드를 작성하여 데이터의 유효성 검사를 진행하였다.  
특정 필드의 값에 대한 검증 로직은 대부분 유사한 검증이기 때문에 간단한 애노테이션 작성을 통해서도 가능하다.  
이렇게 검증 로직을 모든 프로젝트에 적용할 수 있도록 공통화하고 표준화한 것을 `Bean Validation`이라고 한다.  
  
`Bean Validation`은 특정 구현체가 아니라 `Bean Validation 2.0(JSR-380)`이라는 기술 표준이다.  
검증 애노테이션의 인터페이스 모음이며 `Bean Validation`을 구현한 구현체로는 대표적으로 `하이버네이트 Validator`가 있다.  
(이름만 하이버네이트일 뿐 JPA, 하이버네이트와는 관련이 없다.)
  
`하이버네이트 Validator`에는 많은 애노테이션과 기능들이 있다. 필요한 기능이 있는지 궁금한 경우 아래의 링크를 통해서 필요한 기능을 제공하는 애노테이션이 있는지 확인해보도록 한다.  
  
- [공식 사이트](http://hibernate.org/validator/)
- [공식 메뉴얼](https://docs.jboss.org/hibernate/validator/6.2/reference/en-US/html_single/)
- [검증 애노테이션 모음](https://docs.jboss.org/hibernate/validator/6.2/reference/en-US/html_single/#validator-defineconstraints-spec)

---

### 의존성 추가

`Bean Validation`을 사용하기 위해서는 `spring-boot-starter-validation` 라이브러리의 의존성을 추가해야 한다.  

**build.gradle**
```
implementation 'org.springframework.boot:spring-boot-starter-validation'
```

의존성을 추가하면 추가되는 라이브러리의 `jakarta.validation-api`는 Bean Validation 인터페이스이며 `hibernate-validator`는 구현체다.

---

### Item 클래스 수정

기존에 사용하던 `Item` 클래스의 코드에 `Bean Validation` 애노테이션을 적용해 본다.

```java
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.ScriptAssert;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class Item {
    private Long id;
    @NotBlank
    private String itemName;
    @NotNull
    @Range(min = 1000, max = 1_000_000)
    private Integer price;
    @NotNull
    @Max(9999)
    private Integer quantity;
    public Item() {
    }
    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
```

애노테이션의 이름만으로 기능을 유추할 수 있지만 자세한 사항이 궁금한 경우 상단에 `하이버네이트 Validator` 관련 페이지에서 확인한다.  
우리는 `Bean Validator`를 사용하기 위해 `javax.validation.constraints.*`경로와 `org.hibernate.validator.constraints.*` 경로의 애노테이션을 사용하였다.  
`javax.validation`으로 시작하면 특정 구현체에 관계없이 제공되는 표준 인터페이스이며, `org.hibernate.validator`로 시작하면 `하이버네이트 Validator`를 사용할 때만 제공되는 검증 기능이다.  
`spring-boot-validation`을 사용하는 경우 자동으로 `하이버네이트 Validator`를 사용하게 되므로 `org.ibernate.validator` 경로의 애노테이션을 사용하여도 크게 문제가 되지 않는다.
또한 `@Validated` 애노테이션에는 `@Valid`에는 없는 `groups`라는 기능이 포함되어있다.

---

### Item 클래스 테스트

`Item` 클래스를 테스트하는 코드를 작성한다.  
`Validator`를 사용하기 위해서는 `ValidatorFactory`를 사용하여 `Validator`를 생성해야 한다.  
`BeanValidation`을 테스트하는 코드는 아래와 같다.

```java
public class BeanValidationTest { 
    @Test
    void beanValidation() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Item item = new Item(); 
        item.setItemName(" ");
        item.setPrice(0);
        item.setQuantity(10000);
        Set<ConstraintViolation<Item>> violations = validator.validate(item);
        for (ConstraintViolation<Item> violation : violations) {
            System.out.println("violation=" + violation);
            System.out.println("violation.message=" + violation.getMessage());
        }
    } 
}
```
  
`Validator`의 `validate(...)` 메서드의 인자로 검증을 원하는 대상을 입력하면 `ConstraintVilation` 객체가 `Set`에 담겨서 반환된다.  
검증 결과가 위반되는 결과만 해당 `Set`에 담기게 되는 구조이므로 만약 `Set`이 비어있다면 검증 대상이 위반하는 필드를 가지고 있지 않다는 의미가 된다.
  
위에서 작성한 테스트 출력은 아래와 같다.
  
```bash
violation={interpolatedMessage='공백일 수 없습니다', propertyPath=itemName, rootBeanClass=class hello.itemservice.domain.item.Item, messageTemplate='{javax.validation.constraints.NotBlank.message}'} violation.message=공백일 수 없습니다
violation={interpolatedMessage='9999 이하여야 합니다', propertyPath=quantity, rootBeanClass=class hello.itemservice.domain.item.Item, messageTemplate='{javax.validation.constraints.Max.message}'} violation.message=9999 이하여야 합니다
violation={interpolatedMessage='1000에서 1000000 사이여야 합니다', propertyPath=price, rootBeanClass=class hello.itemservice.domain.item.Item, messageTemplate='{org.hibernate.validator.constraints.Range.message}'} violation.message=1000에서 1000000 사이여야 합니다
```
  
`@NotBlank`가 달려있는 `itemName` 필드에 공백을 입력하였으므로 "공백일 수 없습니다"라는 메시지가 포함되었다.  
`@Max` 애노테이션으로 최대값을 9999로 제한한 `quantity`필드에 최대값을 초과하는 값이 입력되었으므로 "9999 이하여야 합니다"라는 메시지가 포함되었다.  
`@Range` 애노테이션으로 범위를 제한하는 `price`필드에 범위를 초과하는 숫자가 입력되었으므로 "1000에서 1000000 사이여야 합니다"라는 메시지가 포함되었다.  

이전에 우리가 작성했던 코드와는 다르게 간단한 애노테이션과 속성값만으로 검증이 완료되는 것을 확인할 수 있다.

---

### 작동 원리

스프링 부트는 `spring-boot-starter-validation` 라이브러리가 있는 경우 자동으로 `Bean Validator`를 글로벌 `Validator`로 등록한다.  
`LocalValidatorFactoryBean`이 글로벌 `Validator`로 등록되어 우리가 위에서 사용한 `@Range`와 같은 애노테이션이 붙은 필드를 확인하여 검증을 수행한다.  
글로벌로 등록된 `Validator`를 작동시키기 위해서 우리는 `@Valid`, `@Validated`만 적용하면 자동으로 작동되어 `BindingResult`에 `FieldError`, `ObjectError`를 생성하여 넣어준다.  
  
`Bean Validator`가 검증을 수행하는 절차는 아래와 같다.  
- `@ModelAttribute` 각각의 필드에 타입 변환을 시도한다.
  - 만약 필드 변환에 실패하면 `typeMismatch`로 `FieldError`에 추가한다.
- 타입 변환에 성공한 경우 `Validator`를 적용한다.
  
어떻게 보면 당연한 절차로 필드 변환조차 실패한 경우 `Validator`를 적용할 필요가 없기 때문에 스프링은 이러한 작업을 생략한다.  
예를 들어 타입이 `Integer`인 필드에 문자열이 입력된 경우 해당 값의 범위를 확인하는 등의 검증은 불필요해지기 때문이다.

---

### 에러 코드

`Bean Validation`을 사용하더라도 우리는 원하는 오류 메시지를 클라이언트에게 전달할 수 있다.  
`Bean Validation`이 적용하면 마치 `typeMismatch`가 적용되는 것과 같이 오류 코드가 애노테이션 이름으로 적용된다.  
예를 들어 `@NotBlank`를 사용하면 `MessageCodesResolver`를 통해서 아래와 같은 메시지 코드가 생성된다.  
  
- NotBlank.item.itemName
- NotBlank.itemName
- NotBlank.java.lang.String
- NotBlank

우리는 `errors.properties`에 아래와 같이 입력하여 원하는 메시지를 사용할 수 있다.
메시지에서 `{0}`은 필드명을 의미하며 이외의 파라미터는 애노테이션마다 다르므로 공식문서를 확인해야 한다.

```yaml
NotBlank={0}은 공백이 불가능합니다. 
Range={0}은 {1} ~ {2} 사이의 값만 허용됩니다. 
Max={0}의 최대값은 {1} 입니다.
```

만약 설정파일에 저장하지 않고 따로 애노테이션의 속성으로 메시지를 지정하고 싶다면 아래와 같이 `message` 속성을 사용하면 된다.

```java
@NotBlank(message = "아이템명은 공백이 불가능합니다.")
private String itemName;
```

`Bean Validator`가 메시지를 찾는 순서는 아래와 같다.

1. 생성된 메시지 코드 순서대로 `messageSource`에서 메시지를 찾는다.
2. 애노테이션의 `message` 속성을 사용한다.  
3. 1, 2가 발견되지 않는 경우 라이브러리에서 제공하는 기본 값을 사용한다.

---

### 오브젝트 오류

지금까지 특정 필드에 우리가 예상한 것과 다른 값이 들어오는 검증하는 방법에 대해서 알아보았다.  
특정 필드를 대상으로 하는 것이 아닌 객체 관련된 오류(ObjectError)는 `@ScriptAssert` 애노테이션을 사용하여 해결할 수 있다.

```java
@Data
@ScriptAssert(lang = "javascript", script = "_this.price * _this.quantity >= 10000")
public class Item {
    // ...
}
``` 

메시지 코드의 경우 애노테이션을 사용하여 필드를 검증하는 것과 같이 `ScriptAssert.*` 메시지 코드가 생성된다.  
하지만 입력되는 값들이 전부 문자열이므로 컴파일 시점에 오류를 파악할 수 없으며 런타임 시점에 문제가 발생해야 개발자는 알 수 있다.  
또한 `_this`를 사용하여 검증 대상을 객체로 한정하고 있기 때문에 검증하려는 범위가 넓어지면 대응이 힘들어진다.
  
굳이 `@ScriptAssert` 애노테이션을 사용하는 것보다 오브젝트 오류 관련 부분만 따로 모아서 자바 코드로 관리하는 것이 유리하다.

```java
if (item.getPrice() != null && item.getQuantity() != null) {
    int resultPrice = item.getPrice() * item.getQuantity();
    if (resultPrice < 10000) {
        bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
    }
}
```

---

### 엔티티를 공통으로 사용하게 되는 경우

데이터를 등록할 때와 수정할 때는 요구사항이 다를 수 있다.  
예를 들어 `Item`을 생성하여 DB에 값을 넣는 경우에는 아이템의 `id`값을 입력할 수 없어야 하고 DB에 입력되는 시점에 값이 할당되어야 한다.  
하지만 `Item`을 수정하는 경우에는 어떤 `Item`을 수정하는지 알아야 하기 때문에 `id`값이 필수로 입력되어야 한다.  
(물론 일반적인 경우라면 `Path Variable`로 추출하겠지만 이번 예시에서는 `Body`에 포함한다)  
  
만약 수정 시점에 `id`값이 필수이기 때문에 `Item` 클래스를 아래와 같이 수정하면 `Item`을 등록하는 시점에는 `id`가 없다는 오류가 발생하게 될 것이다.

```java
@Data
public class Item {
    @NotNull 
    private Long id;
    @NotBlank
    private String itemName;
    @NotNull
    @Range(min = 1000, max = 1000000)
    private Integer price;
    @NotNull
    @Max(9999) 
    private Integer quantity;
}
```

물론 `JPA`를 조금이라도 사용해본 개발자라면 엔티티를 클라이언트에게 바로 노출하는 것은 위험하기 때문에 따로 `DTO`를 생성하여 작업할 것이다.  
 
---

### Groups




































---

**참고한 강의**:
- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%ED%95%B5%EC%8B%AC-%EC%9B%90%EB%A6%AC-%EA%B8%B0%EB%B3%B8%ED%8E%B8
- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-1
- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-2

**참고한 문서**:
- [Thymeleaf 기본 메뉴얼](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html)
- [Thymeleaf 스프링 통합 메뉴얼](https://www.thymeleaf.org/doc/tutorials/3.0/thymeleafspring.html)