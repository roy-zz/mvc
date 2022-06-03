이번 장에서는 스프링의 타입 컨버터에 대해서 알아본다.
모든 코드는 [깃허브(링크)](https://github.com/roy-zz/mvc) 에 올려두었다.

---

### 개요

개발하다보면 문자 -> 숫자, 숫자 -> 문자 변환과 같은 타입을 변환시켜야 하는 경우가 많이 발생한다.  
특히 우리는 무의식중에 당연하게 사용하고 있지만 HTTP 요청 파라미터의 경우 전부 문자열로 서버에 전송된다.  
하지만 우리가 원하는 타입이 숫자라면 스프링이 중간에서 변환해주기 때문에 우리는 타입 변환없이 사용이 가능하다.  
단순 파라미터 뿐만 아니라 `@ModelAttribute`, `@PathVariable`, `@RequestBody`, `@Value`, `XML 파싱`등 많은 부분에서 스프링이 자동으로 타입을 변경시켜주고 있다.

---

### Converter 인터페이스

스프링에서 기본적으로 지원하는 타입 변환이 아니라 개발자가 직접 타입 변환을 구현하는 경우를 위해서 **컨버터 인터페이스**를 제공한다.  
  
```java
public interface Converter<S, T> {
      T convert(S source);
}
```
  
이 컨버터 인터페이스는 모든 타입에 적용할 수 있다.  
예를 들어 1(Integer)가 입력되면 True(Boolean) 타입으로 변경하고 싶으면 Integer -> Boolean 타입으로 변환되도록 컨버터 인터페이스를 만들어서 등록하면 된다.  
반대의 경우도 필요하다면 Boolean -> Integer 타입으로 변환되도록 컨버터를 추가로 만들어서 등록하면 된다.

```java
@Slf4j
public class IntegerToBooleanConverter implements Converter<Integer, Boolean> {
    @Override
    public Boolean convert(Integer source) {
        log.info("Integer To Boolean Source = {}", source);
        if (source > 1) {
            throw new IllegalArgumentException("1을 초과하는 숫자는 변경이 불가능합니다.");
        } else {
            return source == 1;
        }
    }
}
```

```java
@Slf4j
public class BooleanToIntegerConverter implements Converter<Boolean, Integer> {
    @Override
    public Integer convert(Boolean source) {
        log.info("Boolean To Integer Source = {}", source);
        return source == Boolean.TRUE ? 1 : 0;
    }
}
```

#### 사용자 정의 클래스 컨버터

제조사와 사이즈(인치) 필드를 가지고 있는 모니터(MonitorSpec) 클래스에 컨버터를 적용시켜본다.  

```java
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class MonitorSpec {
    private String manufacturer;
    private int inch;
}
```

`@EqualAndHashCode` 애노테이션을 사용하는 경우 `equals()`, `hashcode()`를 생성해주어 필드 값이 동등한 경우 `equals`의 결과가 `true`가 된다.  
클라이언트는 모니터 객체의 필드를 모르기때문에 단순 문자열인 `Samsung_27`, `LG_32`와 같은 형태로 입력하고 우리는 문자열을 제조사와 사이즈로 분리해야하는 상황이다.
  
문자열로 입력된 모니터 정보를 `MonitorSpec` 객체로 변경하는 컨버터 코드는 아래와 같다.

```java
@Slf4j
public class StringToMonitorSpecConverter implements Converter<String, MonitorSpec> {
    @Override
    public MonitorSpec convert(String source) {
        log.info("String To Monitor Spec Source = {}", source);
        String[] splits = source.split("_");
        String manufacturer = splits[0];
        int inch = Integer.parseInt(splits[1]);
        return new MonitorSpec(manufacturer, inch);
    }
}
```

`MonitorSpec`객체를 클라이언트로 전달하기 위해 문자열로 변경하는 컨버터 코드는 아래와 같다.

```java
@Slf4j
public class MonitorSpecToStringConverter implements Converter<MonitorSpec, String> {
    @Override
    public String convert(MonitorSpec source) {
        log.info("MonitorSpec To String source = {}", source);
        return String.format("%s_%s", source.getManufacturer(), source.getInch());
    }
}
```

스프링은 용도에 따라서 다양한 타입의 컨버터를 제공한다. [공식문서](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#core-convert)

- Converter: 기본 컨버터
- ConverterFactory: 전체 클래스의 계층형 구조가 필요한 경우
- GenericConverter: 정교한 구현체와 대상이 되는 필드의 애노테이션 정보를 사용 가능하다.
- ConditionalGenericConverter: 특정 조건을 충족시키는 경우에만 실행된다.

---

### Conversion Service

컨버터의 종류는 원하는 타입에 따라서 수없이 많은 컨버터가 생성될 수 있다.  
하지만 필요한 컨버터를 모두 만들고 필요한 경우에 찾아서 사용하는 것은 무리가 있다.  
개발자가 직접 컨버터를 찾을 필요없이 필요한 컨버터를 미리 등록시켜놓고 **사용하는 시점에 적절한 컨버터를 실행시켜주는 ConversionService**라는 기능이 있다.
  
```java
public interface ConversionService { 
    boolean canConvert(@Nullable Class<?> sourceType, Class<?> targetType);
    boolean canConvert(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType);
    <T> T convert(@Nullable Object source, Class<T> targetType);
    Object convert(@Nullable Object source, @Nullable TypeDescriptor sourceType, TypeDescriptor targetType);
}
```

우리가 위에서 만든 컨버터를 등록하고 사용하는 방법은 아래와 같다.

```java
public class ConversionServiceTest {
    @Test
    void conversionServiceTest() {
        // 등록
        DefaultConversionService conversionService = new DefaultConversionService();
        conversionService.addConverter(new IntegerToBooleanConverter());
        conversionService.addConverter(new BooleanToIntegerConverter());
        conversionService.addConverter(new MonitorSpecToStringConverter());
        conversionService.addConverter(new StringToMonitorSpecConverter());

        // 사용
        assertThat(conversionService.convert(1, Boolean.class)).isEqualTo(Boolean.TRUE);
        assertThat(conversionService.convert(Boolean.TRUE, Integer.class)).isEqualTo(1);
        MonitorSpec monitorSpec = conversionService.convert("Samsung_27", MonitorSpec.class);
        assertThat(monitorSpec).isEqualTo(new MonitorSpec("Samsung", 27));
        String monitorSpecString = conversionService.convert(new MonitorSpec("LG", 32), String.class);
        assertThat(monitorSpecString).isEqualTo("LG_32");
    }
}
```
  
`DefaultConversionService`를 사용하면 컨버터의 등록과 사용을 분리하게 된다.  
컨버터를 등록하는 시점에는 컨버터의 타입을 정확히 알아야 하지만 사용하는 시점에서는 컨버터를 전혀 몰라도 된다.  
전부 `ConversionService` 내부적으로 동작하여 필요한 컨버터를 제공하기 때문이다. 우리는 구현체가 아닌 `ConversionService`에만 의존하면 된다.  

#### 스프링과 Converter

스프링 내부에서 이미 `ConversionService`가 동작하고 있기 때문에 우리는 `WebMvcConfigurer` 인터페이스를 구현하여 컨버터를 등록해서 사용하면 된다.

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new IntegerToBooleanConverter());
        registry.addConverter(new BooleanToIntegerConverter());
        registry.addConverter(new StringToMonitorSpecConverter());
        registry.addConverter(new MonitorSpecToStringConverter());
    }
}
```

우리는 `Integer` 타입을 `Boolean` 타입으로 변환해주는 `IntegerToBooleanConverter`를 등록하였다.  
하지만 스프링은 이미 기본적인 타입들을 변환해주는 컨버터를 가지고 있기 때문에 같은 역할을 하는 컨버터가 중복되게 된다.  
이렇게 같은 기능을 하는 컨버터가 중복되는 경우 스프링은 개발자가 직접 등록한 컨버터가 우선적으로 사용되도록 한다.  
  
`@RequestParam`을 예로 들어보면 `@RequestParam`을 처리하는 `ArgumentResolver`중 `RequestParamMethodArgumentResolver`를 사용한다.  
`RequestParamMethodArgumentResolver`는 `ConversionService`를 사용하여 사용자로 부터 전달받은 데이터의 타입을 변환한다. 

---

### 타임리프 컨버터 적용

타임리프틑 렌더링 시점에 컨버터를 사용하여 렌더링 하는 방법을 지원한다.  
그렇기 때문에 우리가 객체를 전달하여도 타임리프틑 문자열로 화면에 표시하게 되는 것이다.

타임리프는 변수를 표현할 때 `${...}`와 같은 표현식을 사용하고 `ConversionService`를 적용할 때는 `${{...}}`과 같은 표현식을 사용한다.
  
- `MonitorSpec` 객체를 변수 표현식(`${...}`)으로 출력하면 아래와 같이 출력된다.
`hello.mytypeconverter.type.MonitorSpec@12@1032`
  
- `MonitorSpec` 객체에 컨버전 서비스를 적용하는 표현식(`${{...}}`)으로 출력하면 아래와 같이 우리가 원하는 결과가 출력된다.
`Samsung_27`
  

만약 `MonitorSpec` 객체를 `th:value`로 출력하면 객체의 해시값이 화면에 출력된다.  
반면 `th:field`의 경우 컨버전 서비스를 지원하기 때문에 우리가 원하는 결과물을 화면에 출력해준다.

---

**참고한 강의**:
- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%ED%95%B5%EC%8B%AC-%EC%9B%90%EB%A6%AC-%EA%B8%B0%EB%B3%B8%ED%8E%B8
- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-1
- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-2

**참고한 문서**:
- [Thymeleaf 기본 메뉴얼](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html)
- [Thymeleaf 스프링 통합 메뉴얼](https://www.thymeleaf.org/doc/tutorials/3.0/thymeleafspring.html)