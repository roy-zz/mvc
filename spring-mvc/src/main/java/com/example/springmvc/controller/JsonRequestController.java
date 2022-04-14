package com.example.springmvc.controller;

import com.example.springmvc.domain.RequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Controller
public class JsonRequestController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @ResponseBody
    @PostMapping(value = "/request-body-json", headers = "X-API-VERSION=1.0")
    public void requestBodyJsonV1(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServletInputStream inputStream = request.getInputStream();
        String messageBody = StreamUtils.copyToString(inputStream, UTF_8);
        log.info("messageBody: {}", messageBody);
        RequestDTO dto = objectMapper.readValue(messageBody, RequestDTO.class);
        log.info("username: {}, age: {}, fromAt: {}, toAt: {}",
                dto.getUsername(), dto.getAge(), dto.getFromAt(), dto.getToAt());
        response.getWriter().write("OK");
    }

    @ResponseBody
    @PostMapping(value = "/request-body-json", headers = "X-API-VERSION=2.0")
    public String requestBodyJsonV2(@RequestBody String messageBody) throws IOException {
        RequestDTO dto = objectMapper.readValue(messageBody, RequestDTO.class);
        log.info("username: {}, age: {}, fromAt: {}, toAt: {}",
                dto.getUsername(), dto.getAge(), dto.getFromAt(), dto.getToAt());
        return "OK";
    }

    @ResponseBody
    @PostMapping(value = "/request-body-json", headers = "X-API-VERSION=3.0")
    public String requestBodyJsonV3(@RequestBody RequestDTO dto) {
        log.info("username: {}, age: {}, fromAt: {}, toAt: {}",
                dto.getUsername(), dto.getAge(), dto.getFromAt(), dto.getToAt());
        return "OK";
    }

    @ResponseBody
    @PostMapping(value = "/request-body-json", headers = "X-API-VERSION=4.0")
    public String requestBodyJsonV4(@RequestBody HttpEntity<RequestDTO> httpEntity) {
        RequestDTO dto = httpEntity.getBody();
        log.info("username: {}, age: {}, fromAt: {}, toAt: {}",
                dto.getUsername(), dto.getAge(), dto.getFromAt(), dto.getToAt());
        return "OK";
    }

    @ResponseBody
    @PostMapping(value = "/request-body-json", headers = "X-API-VERSION=5.0")
    public RequestDTO requestBodyJsonV5(@RequestBody RequestDTO dto) {
        log.info("username: {}, age: {}, fromAt: {}, toAt: {}",
                dto.getUsername(), dto.getAge(), dto.getFromAt(), dto.getToAt());
        return dto;
    }

}
