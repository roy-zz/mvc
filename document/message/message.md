이번 장에서는 `메시지, 국제화`를 적용하고 테스트하는 방법에 대해서 알아본다.  
글의 하단부에 참고한 강의와 공식문서의 경로를 첨부하였으므로 자세한 내용은 강의나 공식문서에서 확인한다.  
모든 코드는 [깃 허브(링크)](https://github.com/roy-zz/mvc) 에 올려두었다.

---

### 메시지 기능의 필요성

아래와 같은 방식으로 `상품명`, `가격`이라는 문구가 하드코딩 되어있다고 가정해본다.

```html
<div>
    <label for="itemName">상품명</label>
    <input type="text" id="itemName" class="form-control" th:field="*{itemName}" >
</div>
<div>
    <label for="price">가격</label>
    <input type="text" id="price" class="form-control" th:field="*{price}">
</div>
```

`상품명`이라는 문구를 `상품이름`이라는 문구로 변경해달라는 요청이 들어오면 우리는 `상품명`이라는 모든 문구를 찾아서 변경해주어야 한다.  
만약 우리가 `HTML`코드에 `상품명` 문구를 직접 입력하여 사용한 것이 아니라 한 곳에서 관리하고 관리되는 `메시지`를 가져다 쓴 것이라면 우리는 관리되는 한 곳만 수정하면 된다.
이렇게 메시지가 한 곳에서 관리되도록 하는 기능을 **메시지 기능**이라고 한다.

---

### 국제화 기능의 필요성

사용자가 접속하는 국가 및 환경에 따라 문구가 다르게 표시되어야 하는 경우가 있다.  
영어권에 있는 사용자라면 영어로 보여야 하며, 한국에 있는 사용자라면 한국어로 보여야 한다. 또 한국에 있는 사용자라도 브라우저 설정이 영어라면 페이지는 영어로 표시되어야 한다.  
이렇게 여러한 환경에 맞게 대응해야 하는 경우 우리는 **스프링의 국제화** 기능을 사용할 수 있다.

아래와 같이 두개의 파일을 만들어두고 서버에 요청을 보낼 때 HTTP `accept-language` 헤더 값을 사용하여 사용자의 환경에 맞는 언어를 표시할 수 있다.

**message_en.properties**
```properties
item=Item
item.id=Item ID
item.itemName=Item Name
item.price=price
item.quantity=quantity
```

**message_ko.properties**
```properties
item=상품
item.id=상품 ID
item.itemName=상품명
item.price=가격
item.quantity=수량
```

---

### 적용

#### 직접 등록

만약 스프링 부트를 사용하지 않거나 따로 설정을 하고 싶다면 아래와 같이 `MessageSource`를 빈으로 등록해주어야 한다.

```java
@Bean
public MessageSource messageSource() {
    ResourceBundleMessageSource messageSource = new
    ResourceBundleMessageSource();
    messageSource.setBasenames("messages", "errors");
    messageSource.setDefaultEncoding("utf-8");
    return messageSource;
}
```

`basename`은 설정 파일의 이름을 지정하는 것이며 예시처럼 `messages`로 지정하는 경우 `messages.properties` 파일을 읽어서 사용한다.  
추가 국제화 기능을 사용하고 싶다면 `message_en.properties`, `message_ko.properties`와 같이 파일명 마지막에 언어에 대한 정보를 입력하면 된다.  
만약 스프링이 찾을 수 없는 국제화 파일이라면 `resources`경로의 `messages.properties`를 기본으로 사용한다. `setBasenames(...)`에 여러 파일을 한번에 지정할 수 있다.

#### 스프링 부트

만약 스프링 부트를 사용한다면 스프링에서 `MessageSource`를 자동으로 스프링 빈으로 등록하기 때문에 따로 설정할 필요가 없다.
스프링 부트에서의 기본값은 `spring.messages.basename=messages`다. 만약 다르게 설정하고 싶다면 `application.properties`파일을 아래와 같이 수정하면 된다.  

```properties
spring.messages.basename=messages,config.i18n.messages
```

또한 여러 언어를 설정하기 위한 파일들인 `messages_ko.properties`, `messages_en.properties` 파일들도 자동으로 인식된다.

#### *.properties 파일 생성

`/resources` 경로에 아래와 같이 `messages_ko.properties`와 `messages_en.properties` 파일을 생성한다.
아래와 같이 파일이 생성되면 기본적으로 한국어가 선택될 것이며 영어를 요청하는 경우에만 영어가 선택될 것이다.

**messages_ko.properties**
```properties
name=로이
name.full=로이 최 {0}
```

**message_en.properties**
```properties
name=roy
name.full=roy choi {0}
```

#### MessageSource

스프링에서는 `MessageSource` 인터페이스를 통해서 메시지 소스에 대한 값을 가져올 수 있다.

```java
public interface MessageSource {
	@Nullable
	String getMessage(String code, @Nullable Object[] args, @Nullable String defaultMessage, Locale locale);
	String getMessage(String code, @Nullable Object[] args, Locale locale) throws NoSuchMessageException;
	String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException;

}
```

---

### 테스트

우리가 작성한 코드가 정상적으로 작동하는지 테스트 코드를 작성하여 실행시켜 본다.
스프링 부트 환경에서 테스트가 실행될 수 있도록 `@SpringBootTest` 애노테이션을 사용하였고 `MessageSource`를 빈으로 주입받았다.

```java
@SpringBootTest
public class MyMessageSourceTest {
    @Autowired
    private MessageSource messageSource;
}
```

**기본 `Locale`의 `name` 조회**
```java
@Test
void getName() {
    String name = messageSource.getMessage("name", null, null);
    assertEquals("로이", name);
}
```

**존재하지 않는 값 조회(Exception)**
```java
@Test
void getNotExistValue() {
    assertThrows(NoSuchMessageException.class, () -> {
        messageSource.getMessage("not_exist_value", null, null);
    });
}
```

**존재하지 않는 값 조회(기본값 지정)**
```java
@Test
void getNotExistValueWithDefaultValue() {
    String defaultMessage = messageSource.getMessage("not_exist_value", null, "기본 이름", null);
    assertEquals("기본 이름", defaultMessage);
}
```

**기본 `Locale` 사용과 사용자 지정 `Locale` 사용**
```java
@Test
void defaultLanguage() {
    String defaultLocale = messageSource.getMessage("name", null, null);
    String krLocale = messageSource.getMessage("name", null, Locale.KOREA);
    assertEquals("로이", defaultLocale);
    assertEquals("로이", krLocale);
}

@Test
void englishLanguage() {
    String enLocale = messageSource.getMessage("name", null, Locale.ENGLISH);
    assertEquals("roy", enLocale);
}
```

---

**참고한 강의**:
- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%ED%95%B5%EC%8B%AC-%EC%9B%90%EB%A6%AC-%EA%B8%B0%EB%B3%B8%ED%8E%B8
- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-1
- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-2

**참고한 문서**:
- [Thymeleaf 공식 사이트](https://www.thymeleaf.org/)
- [Thymeleaf 기본 기능](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html)
- [Thymeleaf 스프링 통합](https://www.thymeleaf.org/doc/tutorials/3.0/thymeleafspring.html)