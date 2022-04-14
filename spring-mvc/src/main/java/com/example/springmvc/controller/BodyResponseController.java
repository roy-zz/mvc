package com.example.springmvc.controller;

import com.example.springmvc.domain.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Controller
public class BodyResponseController {

    @GetMapping(value = "/response-body-string", headers = "X-API-VERSION=1.0")
    public void responseBodyV1(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.getWriter().write("OK");
    }

    @GetMapping(value = "/response-body-string", headers = "X-API-VERSION=2.0")
    public ResponseEntity<String> responseBodyV2() {
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(value = "response-body-string", headers = "X-API-VERSION=3.0")
    public String responseBodyV3() {
        return "OK";
    }

    @GetMapping(value = "/response-body-json", headers = "X-API-VERSION=1.0")
    public ResponseEntity<ResponseDTO> responseBodyJsonV1() {
        ResponseDTO dto = new ResponseDTO();
        dto.setUsername("Roy");
        dto.setAge(20);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/response-body-json", headers = "X-API-VERSION=2.0")
    public ResponseDTO responseBodyJsonV2() {
        ResponseDTO dto = new ResponseDTO();
        dto.setUsername("Roy");
        dto.setAge(20);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());
        return dto;
    }

}
