package com.example.springmvc.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

@Slf4j
@Controller
public class ConverterController {

    @ResponseBody
    @RequestMapping("/byte")
    void byteController(@RequestBody byte[] data) {
        log.info("data: {}", data);
    }

    @RequestMapping("/string")
    void stringController(@RequestBody String data) {
        log.info("data: {}", data);
    }

    @RequestMapping("/json")
    void jsonController(@RequestBody HashMap<String, String> data) {
        log.info("data: {}", data);
    }

}
