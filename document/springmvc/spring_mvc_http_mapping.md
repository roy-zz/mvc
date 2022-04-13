이번 장에서는 스프링 MVC의 HTTP Request를 매핑하는 방법에 대해서 알아본다.
글의 하단부에 참고한 강의와 공식문서의 경로를 첨부하였으므로 자세한 내용은 강의나 공식문서에서 확인한다.
모든 코드는 [깃허브(링크)](https://github.com/roy-zz/mvc)에 올려두었다.

---

### Request Mapping(요청 매핑)

```java
@Slf4j
@RestController
public class MappingController {
    @RequestMapping(value = {"/basic", "/basics" })
    public String basic() {
        log.info("basic");
        return "OK";
    }
}
```

@RestController이 사용되면 반환된 문자열로 View를 찾는 것이 아니라 HTTP 메시지 바디에 문자열을 바로 입력한다.
@Controller이 사용되면 반환된 문자열에 맞는 View를 찾는다.
정확히는 @ResponseBody의 유무 때문에 Controller가 다르게 작동하는 것이다.
요청 ULR 또한 예시에서 사용된 것처럼 복수로 등록이 가능하다.

#### HTTP 메서드 매핑

특별한 제약없이 아래와 같이 @RequestMapping으로만 선언하면 모든 HTTP Method를 허용하게 된다.

```java
@Slf4j
@RestController
public class MappingController {
    @RequestMapping(value = "/mapping-all-method")
    public String mappingAllMethod() {
        log.info("mappingAllMethod");
        return "OK";
    }
}
```

만약 HTTP Method 중 GET만 허용하고 싶다면 아래 두 개의 예시처럼 사용해야한다.
개인적으로 더 직관적이고 간결한 @GetMapping을 사용한 방법이 더 좋아보인다.

```java
@Slf4j
@RestController
public class MappingController {
    @RequestMapping(value = "/mapping-get-method1", method = RequestMethod.GET)
    public String mappingGetMethod1() {
        log.info("mappingGetMethod1");
        return "OK";
    }
    @GetMapping(value = "/mapping-get-method2")
    public String mappingGetMethod2() {
        log.info("mappingGetMethod2");
        return "OK";
    }
}
```

만약 이렇게 특정 HTTP Method만 호출 가능하도록 설정해놓은 상태에서 다른 메서드로 호출하면 스프링 MVC는 HTTP 405상태(Method Not Allowed)를 반환한다.

---

#### PathVariable 사용

아래와 같이 @PathVariable을 사용하여 URL 경로의 변수를 받을 수 있다.

```java
@Slf4j
@RestController
public class MappingController {
    @GetMapping("/mapped-by-value/{userId}")
    public String pathMappingByValue(@PathVariable("userId") String data) {
        log.info("pathMappingByValue: {}", data);
        return "OK";
    }
}
```

만약 경로에 선언한 변수명과 파라미터로 받는 변수명이 같다면 생략 가능하다.

```java
@Slf4j
@RestController
public class MappingController {
    @GetMapping("/mapped-by-variable-name/{userId}")
    public String pathMappingByVariableName(@PathVariable String userId) {
        log.info("pathMappingByVariableName: {}", userId);
        return "OK";
    }
}
```

여러개의 PathVariable이 있다면 아래와 같이 받을 수 있다.

```java
@Slf4j
@RestController
public class MappingController {
    @GetMapping("/mapped-multiple-variable/{userId}/{userName}")
    public String mappedMultipleVariable(
            @PathVariable String userId,
            @PathVariable String userName
    ) {
        log.info("userId: {}", userId);
        log.info("userName: {}", userName);
        return "OK";
    }
}
```

---

#### 특정 파라미터 조건 매핑

파라미터의 특정 값을 이용하여 매핑이 가능하다.
예를 들어 API 버전 관리를 파라미터의 값으로 한다면 아래와 같이 사용 가능하다.


```java
@Slf4j
@RestController
public class MappingController {
    // X-API-VERSION == null 인 경우 매핑
    @GetMapping(value = "/specific-param", params = "X-API-VERSION")
    public String specificParamVersionNotNull() {
        log.info("versionNotNull");
        return "OK";
    }
    
    // X-API-VERSION != null 인 경우 매핑
    @GetMapping(value = "/specific-param", params = "!X-API-VERSION")
    public String specificParamVersionNull() {
        log.info("versionNull");
        return "OK";
    }

    // X-API-VERSION = 1.0 인 경우 매핑
    @GetMapping(value = "/specific-param", params = "X-API-VERSION=1.0")
    public String specificParamVersionIsOne() {
        log.info("versionIsOne");
        return "OK";
    }

    // X-API-VERSION != 1.0 인 경우 매핑
    @GetMapping(value = "/specific-param", params = "X-API-VERSION!=1.0")
    public String specificParamVersionIsNotOne() {
        log.info("versionIsNotOne");
        return "OK";
    }

    // X-API-VERSION IN [1.0, 2.0] 인 경우 매핑
    @GetMapping(value = "/specific-param", params = {"X-API-VERSION=1.0", "X-API-VERSION=2.0"})
    public String specificParamVersionIsOneOrTwo() {
        log.info("versionIsOneOrTwo");
        return "OK";
    }
}
```

---

#### 특정 헤더 조건 매핑

특정 파라미터 매핑과 동일하다.
파라미터에서 버전관리를 파라미터의 값으로 한다고 예를 들었는데 헤더로 관리하는 것이 더 좋을듯하다.

```java
@Slf4j
@RestController
public class MappingController {
    // X-API-VERSION == null 인 경우 매핑
    @GetMapping(value = "/specific-header", headers = "X-API-VERSION")
    public String specificHeaderVersionNotNull() {
        log.info("versionNotNull");
        return "OK";
    }

    // X-API-VERSION != null 인 경우 매핑
    @GetMapping(value = "/specific-header", headers = "!X-API-VERSION")
    public String specificHeaderVersionNull() {
        log.info("versionNull");
        return "OK";
    }

    // X-API-VERSION = 1.0 인 경우 매핑
    @GetMapping(value = "/specific-header", headers = "X-API-VERSION=1.0")
    public String specificHeaderVersionIsOne() {
        log.info("versionIsOne");
        return "OK";
    }

    // X-API-VERSION != 1.0 인 경우 매핑
    @GetMapping(value = "/specific-header", headers = "X-API-VERSION!=1.0")
    public String specificHeaderVersionIsNotOne() {
        log.info("versionIsNotOne");
        return "OK";
    }

    // X-API-VERSION IN [1.0, 2.0] 인 경우 매핑
    @GetMapping(value = "/specific-header", headers = {"X-API-VERSION=1.0", "X-API-VERSION=2.0"})
    public String specificHeaderVersionIsOneOrTwo() {
        log.info("versionIsOneOrTwo");
        return "OK";
    }
}
```

---

### 미디어 타입 조건 매핑 (Content-Type, consume)

Content-Type을 기준으로 매핑한다.
Content-Type은 클라이언트가 제공하는 미디어 타입이며 스프링 MVC 입장에서는 소비하는 것이므로 consume 키워드가 사용된다.

```java
@Slf4j
@RestController
public class MappingController {
    // Content-Type == "application/json" 인 경우
    @GetMapping(value = "/specific-content-type", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String specificContentTypeIsApplicationJson() {
        log.info("application/json");
        return "OK";
    }

    // Content-Type != "application/json" 인 경우
    @GetMapping(value = "/specific-content-type", consumes = "!" + MediaType.APPLICATION_JSON_VALUE)
    public String specificContentTypeIsNotApplicationJson() {
        log.info("!application/json");
        return "OK";
    }
}
```

만약 Content-Type이 맞지 않으면 HTTP 415 상태코드(Unsupported Media Type)을 반환한다.

---

#### 미디어 타입 조건 매핑 (Accept, produce)

Accept를 기준으로 매핑한다.
Accept는 클라이언트 기준에서는 서버에서 제공하는 값을 받아들이는 것이며 스프링 입장에서는 데이터를 제공하는 것이므로 produce 키워드가 사용된다.

```java
@Slf4j
@RestController
public class MappingController {
    // Accept == application/json 인 경우
    @GetMapping(value = "/specific-accept", produces = MediaType.APPLICATION_JSON_VALUE)
    public String specificProduceIsApplicationJson() {
        log.info("application/json");
        return "OK";
    }

    // Accept != application/json 인 경우
    @GetMapping(value = "/specific-accept", produces = "!" + MediaType.APPLICATION_JSON_VALUE)
    public String specificAcceptIsNotApplicationJson() {
        log.info("!application/json");
        return "OK";
    }
}
```

만약 Accept 요청이 맞지 않으면 HTTP 406 상태코드(Not Acceptable)을 반환한다.

---

### API 예시

RESTful한 방식으로 회원 관리를 HTTP API로 만들어본다.
단순히 컨트롤러만 예를 들어 진행한다.

```java
@RestController
@RequestMapping(value = "/member")
public class MemberController {
    // 특정 회원 조회
    @GetMapping(value = "/{memberId}")
    public String findMember(@PathVariable Long memberId) {
        return "findMember";
    }

    // 모든 회원 조회
    @GetMapping
    public String findAllMembers() {
        return "findAllMember";
    }

    // 회원 등록
    @PostMapping
    public String saveMember() {
        return "saveMember";
    }

    // 회원 수정 (Put)
    @PutMapping("/{memberId}")
    public String putMember(
            @PathVariable Long memberId,
            @RequestBody MemberUpdateDTO dto) {
        return "putMember";
    }

    // 회원 수정 (Patch)
    @PatchMapping("/{memberId}")
    public String patchMember(
            @PathVariable Long memberId,
            @RequestBody MemberUpdateDTO dto) {
        return "patchMember";
    }

    // 회원 삭제
    @DeleteMapping("/{memberId}")
    public String deleteMember(@PathVariable Long memberId) {
        return "deleteMember";
    }
}
```

정말 깔끔한 RESTful API가 완성되었다.
회원 수정은 Put과 Patch가 사용되었는데 둘의 차이는 구글에서 검색해서 한 번 알아보고 넘어가도록 한다.
~~(개발자 지인들에게 물어본 결과 Put과 Patch를 구분해서 사용하는 회사는 아직까지는 없었다. 필자 또한 재직 중인 회사에 도입을 요청하였으나 거절되었다.)~~

---

**참고한 강의**:

- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%ED%95%B5%EC%8B%AC-%EC%9B%90%EB%A6%AC-%EA%B8%B0%EB%B3%B8%ED%8E%B8

- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-1

**Spring 공식문서**:

- https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#spring-web