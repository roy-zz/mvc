package com.example.springmvc.controller;

import com.example.springmvc.domain.RequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping(value = "/basic-request")
public class BasicRequestController {

    @GetMapping(value = "/header")
    public String headers(
            HttpServletRequest request, HttpServletResponse response,
            HttpMethod httpMethod, Locale locale,
            @RequestHeader MultiValueMap<String, String> headerMap,
            @RequestHeader("host") String host,
            @CookieValue(value = "cookieRun", required = false) String cookie
    ) {
        log.info("request: {}", request);
        log.info("response: {}", response);
        log.info("httpMethod: {}", httpMethod);
        log.info("locale: {}", locale);
        log.info("header: {}", headerMap);
        log.info("host: {}", host);
        log.info("cookieRun: {}", cookie);
        return "OK";
    }

    // 반환 타입이 없으면서 response에 값을 직접 넣어주면 View를 조회하지 않는다.
    @GetMapping(value = "/request-param", headers = "X-API-VERSION=1.0")
    public void requestParamV1(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));
        log.info("username: {}, age: {}", username, age);
        response.getWriter().write("OK");
    }

    // @RequestParam를 사용하여 파라미터 이름으로 바인딩
    // @ResponseBody를 추가하여 View 조회를 무시하고 HTTP Message Body에 직접 내용을 입력
    @ResponseBody
    @RequestMapping(value = "/request-param", headers = "X-API-VERSION=2.0")
    public String requestParamV2(
            @RequestParam("username") String memberName,
            @RequestParam("age") int memberAge
    ) {
        log.info("username: {}, age: {}", memberName, memberAge);
        return "OK";
    }

    // @RequestParam을 사용하고 HTTP 파라미터 이름의 변수와 자바 변수의 이름을 맞추어 @RequestParam의 name 생략
    @ResponseBody
    @RequestMapping(value = "/request-param", headers = "X-API-VERSION=3.0")
    public String requestParamV3(
            @RequestParam String username,
            @RequestParam int age
    ) {
        log.info("username: {}, age: {}", username, age);
        return "OK";
    }

    // Primitive 타입이나, Primitive 타입을 감싸고 있는 Wrapper클래스라면 @RequestParam을 생략해도 변수명만 동일하다면 정상 작동한다.
    @ResponseBody
    @RequestMapping(value = "/request-param", headers = "X-API-VERSION=4.0")
    public String requestParamV4(String username, int age) {
        log.info("username: {}, age: {}", username, age);
        return "OK";
    }

    @ResponseBody
    @RequestMapping(value = "/request-param-required")
    public String requestParamRequired(
        @RequestParam(required = true, defaultValue = "Roy") String username,
        @RequestParam(required = false, defaultValue = "0") Integer age
    ) {
        log.info("username: {}, age: {}", username, age);
        return "OK";
    }

    @ResponseBody
    @RequestMapping(value = "/not-duplicated-param-map")
    public String notDuplicatedParamMap(@RequestParam Map<String, Object> params) {
        log.info("username: {}, age: {}", params.get("username"), params.get("age"));
        return "OK";
    }

    @ResponseBody
    @RequestMapping(value = "/duplicated-param-map")
    public String duplicatedParamMap(@RequestParam MultiValueMap<String, Object> params) {
        log.info("username: {}, age: {}", params.get("username"), params.get("age"));
        return "OK";
    }

    @ResponseBody
    @RequestMapping(value = "/not-use-model-attribute")
    public String notUseModelAttribute(
            @RequestParam("username") String username,
            @RequestParam("age") int age,
            @RequestParam("fromAt") LocalDateTime fromAt,
            @RequestParam("toAt") LocalDateTime toAt
    ) {
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setUsername(username);
        requestDTO.setAge(age);
        requestDTO.setFromAt(fromAt);
        requestDTO.setToAt(toAt);
        return "OK";
    }

    @ResponseBody
    @RequestMapping(value = "/model-attribute", headers = "X-API-VERSION=1.0")
    public String modelAttributeV1(@ModelAttribute RequestDTO requestDTO) {
        log.info("username: {}, age: {}, fromAt: {}, toAt: {}",
                requestDTO.getUsername(), requestDTO.getAge(), requestDTO.getFromAt(), requestDTO.getToAt());
        return "OK";
    }

    @ResponseBody
    @RequestMapping(value = "/model-attribute", headers = "X-API-VERSION=2.0")
    public String modelAttributeV2(RequestDTO requestDTO) {
        log.info("username: {}, age: {}, fromAt: {}, toAt: {}",
                requestDTO.getUsername(), requestDTO.getAge(), requestDTO.getFromAt(), requestDTO.getToAt());
        return "OK";
    }

}
