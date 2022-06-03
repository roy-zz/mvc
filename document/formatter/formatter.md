[이전 장(링크)](https://imprint.tistory.com/275) 에서는 데이터의 타입을 변경시켜주는 타입 컨버터에 대해서 알아보았다.
이번 장에서는 컨버터의 특별한 버전인 포맷터(Formatter)에 대해서 알아본다.
모든 코드는 [깃허브(링크)](https://github.com/roy-zz/mvc) 에 올려두었다.

---

### Formatter

이전 장에서 알아본 `Converter`의 경우 입출력 타입에 제한이 없이 사용되는 범용적인 기능이었다.  
경우에 따라서 입출력값이 모든 형식을 지원해야 하는 것이 아니라 일정 타입으로 한정되는 경우가 많다.
  
예를 들어 숫자를 문자로 출력하는 경우 `1000` -> `"1,000"`과 같이 일반 숫자를 돈을 표시하는 형식으로 변경될 수 있다.  
문자를 날짜로 변경하는 경우도 `"2022-06-03 14:00:00"`과 같은 문자열을 날짜로 포맷으로 변경되어야 한다.  
예를 들어 살펴본 돈과 시간의 경우 현지화(Locale)된 정보가 사용되어야 한다.
  
이렇게 객체를 범용적이지 않은 특정한 형식에 맞추는 역할을 하거나 반대의 역할을 하는 것을 `Formatter` 라고 한다.  
`Formatter`는 특정 기능에 특화되어 있는 `Converter`라고 볼 수 있으며 특히 문자에 특화되어 있다.

#### Formatter 인터페이스

`Formatter` 인터페이스의 경우 `Parser`, `Printer` 인터페이스를 상속받고 있다.  
`Parser`는 문자열을 우리가 원하는 날짜 또는 돈과 같은 형태로 변경하는 기능을 의미하며 `Printer`는 반대의 역할을 하는 기능을 의미한다.  
  
```java
public interface Formatter<T> extends Printer<T>, Parser<T> {}

@FunctionalInterface
public interface Parser<T> {
	T parse(String text, Locale locale) throws ParseException;
}

@FunctionalInterface
public interface Printer<T> {
	String print(T object, Locale locale);
}
```

#### Formatter 구현체

`Formatter` 인터페이스를 구현하는 `MoneyFormatter` 클래스를 생성한다.  
돈을 세는 형태의 포맷의 문자열을 `Number` 타입으로 변경하거나 그 반대의 역할을 수행하는 역할을 한다.

```java
@Slf4j
public class MoneyFormatter implements Formatter<Number> {
    @Override
    public Number parse(String text, Locale locale) throws ParseException {
        log.info("Money Formatter parse text = {}, locale = {}", text, locale);
        NumberFormat format = NumberFormat.getInstance(locale);
        return format.parse(text);
    }
    @Override
    public String print(Number object, Locale locale) {
        log.info("Money Formatter print object = {}, locale = {}", object, locale);
        return NumberFormat.getInstance(locale).format(object);
    }
}
```

사용법은 아래와 같으며 변환을 원하는 데이터와 함께 `Locale` 정보를 전달해야 한다.

```java
class MoneyFormatterTest {
    private final MoneyFormatter formatter = new MoneyFormatter();
    @Test
    void parseTest() throws ParseException {
        Number result = formatter.parse("1,000", Locale.KOREA);
        assertThat(result).isEqualTo(1000L);
    }
    @Test
    void printTest() {
        String result = formatter.print(1000, Locale.KOREA);
        assertThat(result).isEqualTo("1,000");
    }
}
```

`Formatter`에서는 이미 많은 종류의 `Formatter`를 제공하고 있으므로 필요한 경우 [공식문서(링크)](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#format) 를 확인하도록 한다.

---

### Conversion Service

우리가 컨버터를 알아볼 때 사용하던 `DefaultConversionService`는 컨버터만 등록할 수 있기 때문에 포맷터는 등록할 수 없다.  
하지만 포맷터도 일종의 컨버터이기 때문에 포맷터를 지원하는 컨버전 서비스를 사용하면 포맷터를 추가할 수 있다. 컨버전 서비스 내부에서 어댑터 패턴을 사용하여 컨버터와 동일하게 동작하게 해주기 때문이다.  
`FormattingConversionService`는 포맷터를 지원하는 컨버전 서비스이며 `DefaultFormattingConversionService`는 `FormattingConversionService`에 기본적인 통화, 숫자와 관련된 몇가지 기본 포맷터 기능을 추가로 제공한다.
  
`FormattingConversionService`는 기본적으로 `ConversionService`를 상속받고 있기 때문에 컨버터와 포맷터 전부 등록이 가능하다.  
하지만 사용할 때는 컨버터와 포맷터 구분없이 사용할 수 있다. 스프링 부트의 경우 `DefaultFormattingConversionService`를 상속받은 `WebConversionService`를 사용한다.
  
`DefaultConversionService`의 사용법은 아래와 같다.  
우리가 이전 장에서 만들었던 컨버터와 이번 장에서 만든 포맷터를 같은 방식으로 사용할 수 있는 것을 알 수 있다.

```java
class FormattingConversionServiceTest {
    @Test
    void formattingConversionServiceTest() {
        DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService();

        conversionService.addConverter(new StringToMonitorSpecConverter());
        conversionService.addConverter(new MonitorSpecToStringConverter());

        conversionService.addFormatter(new MoneyFormatter());

        MonitorSpec monitorSpec = conversionService.convert("Samsung_27", MonitorSpec.class);
        assertThat(monitorSpec).isEqualTo(new MonitorSpec("Samsung", 27));

        assertThat(conversionService.convert(1000, String.class)).isEqualTo("1,000");
        assertThat(conversionService.convert("1,000", Long.class)).isEqualTo(1000L);
    }
}
```

### 스프링과 포맷터

스프링 내부에서 이미 `ConversionService`가 동작하고 있기 때문에 우리는 `WebMvcConfigurer` 인터페이스를 구현하여 포맷터를 등록해서 사용하면 된다.

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToMonitorSpecConverter());
        registry.addConverter(new MonitorSpecToStringConverter());
        
        registry.addFormatter(new MoneyFormatter());
    }
}
```

여기서 주의할 점은 `MoneyFormatter`의 경우 `문자 to 숫자`, `숫자 to 문자`의 역할을 한다.  
만약 같은 역할을 하는 컨버터가 있다면 우선순위가 포맷터보다 컨버터가 높기 때문에 새로 등록한 포맷터가 적용되지 않는다.

---

### 기본 제공 포맷터

스프링에서는 개발자가 직접 만들지 않아도 사용할 수 있는 포맷터를 제공하고 있다.  
하지만 포맷터는 기본 형식이 지정되어 있기 때문에, 필드마다 다른 형식으로 포맷을 지정하기는 어렵기 때문에 애노테이션 기반의 포맷터 두개를 제공한다.

- `@NumberFormat`: 숫자와 관련된 형식을 지정하는 포맷터
- `@DateTimeFormat`: 날짜와 관련된 형식을 지정하는 포맷터

사용법은 아래와 같이 포맷터가 적용되기를 원하는 필드에 애노테이션을 붙이고 원하는 포맷을 설정하면 된다.

```java
public class AnnotationFormat {
    @NumberFormat(pattern = "###,###")
    private Integer number;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime localDateTime;
}
```

`@NumberFormat`, `@DateTimeFormat`과 관련된 자세한 사용법은 필요한 시점에 [공식문서(링크)](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#format-CustomFormatAnnotations) 를 확인해서 사용하도록 한다.
  
여기서 주의할 점은 `HttpMessageConverter`에는 컨버전 서비스가 적용되지 않는다는 점이다.  
`HttpMessageConverter`는 HTTP 메시지 바디의 내용을 객체로 변환하거나 객체를 HTTP 메시지 바디에 입력하는 역할을 한다.  
이러한 기능은 메시지 컨버터 내부의 `Jackson` 라이브러리가 작동하기 때문에 우리가 생각하는 컨버터, 포맷터의 영향을 받지 않는다.  
`JSON` 결과로 만들어지는 숫자나 날짜 포맷을 변경하고 싶다면 이번 장에서 살펴본 포맷터 기능이 아니라 `Jackson` 라이브러리 사용법을 공부해야 한다.
  
정리하면 아래와 같다.

- 컨버터, 포맷터 사용: `@RequestParam`, `@ModelAttribute`, `@PathVariable`, `View Template`
- Jackson 라이브러리 옵션 사용: `@RequestBody`

---

**참고한 강의**:
- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%ED%95%B5%EC%8B%AC-%EC%9B%90%EB%A6%AC-%EA%B8%B0%EB%B3%B8%ED%8E%B8
- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-1
- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-2

**참고한 문서**:
- [Thymeleaf 기본 메뉴얼](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html)
- [Thymeleaf 스프링 통합 메뉴얼](https://www.thymeleaf.org/doc/tutorials/3.0/thymeleafspring.html)