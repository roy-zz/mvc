package com.example.springmvc.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class MappingController {

    @RequestMapping(value = {"/basic", "/basics" })
    public String basic() {
        log.info("basic");
        return "OK";
    }

    @RequestMapping(value = "/mapping-all-method")
    public String mappingAllMethod() {
        log.info("mappingAllMethod");
        return "OK";
    }

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

    @GetMapping("/mapped-by-value/{userId}")
    public String pathMappingByValue(@PathVariable("userId") String data) {
        log.info("pathMappingByValue: {}", data);
        return "OK";
    }

    @GetMapping("/mapped-by-variable-name/{userId}")
    public String pathMappingByVariableName(@PathVariable String userId) {
        log.info("pathMappingByVariableName: {}", userId);
        return "OK";
    }

    @GetMapping("/mapped-multiple-variable/{userId}/{userName}")
    public String mappedMultipleVariable(
            @PathVariable String userId,
            @PathVariable String userName
    ) {
        log.info("userId: {}", userId);
        log.info("userName: {}", userName);
        return "OK";
    }

    @GetMapping(value = "/specific-param", params = "X-API-VERSION")
    public String specificParamVersionNotNull() {
        log.info("versionNotNull");
        return "OK";
    }

    @GetMapping(value = "/specific-param", params = "!X-API-VERSION")
    public String specificParamVersionNull() {
        log.info("versionNull");
        return "OK";
    }

    @GetMapping(value = "/specific-param", params = "X-API-VERSION=1.0")
    public String specificParamVersionIsOne() {
        log.info("versionIsOne");
        return "OK";
    }

    @GetMapping(value = "/specific-param", params = "X-API-VERSION!=1.0")
    public String specificParamVersionIsNotOne() {
        log.info("versionIsNotOne");
        return "OK";
    }

    @GetMapping(value = "/specific-param", params = {"X-API-VERSION=1.0", "X-API-VERSION=2.0"})
    public String specificParamVersionIsOneOrTwo() {
        log.info("versionIsOneOrTwo");
        return "OK";
    }

    @GetMapping(value = "/specific-header", headers = "X-API-VERSION")
    public String specificHeaderVersionNotNull() {
        log.info("versionNotNull");
        return "OK";
    }

    @GetMapping(value = "/specific-header", headers = "!X-API-VERSION")
    public String specificHeaderVersionNull() {
        log.info("versionNull");
        return "OK";
    }

    @GetMapping(value = "/specific-header", headers = "X-API-VERSION=1.0")
    public String specificHeaderVersionIsOne() {
        log.info("versionIsOne");
        return "OK";
    }

    @GetMapping(value = "/specific-header", headers = "X-API-VERSION!=1.0")
    public String specificHeaderVersionIsNotOne() {
        log.info("versionIsNotOne");
        return "OK";
    }

    @GetMapping(value = "/specific-header", headers = {"X-API-VERSION=1.0", "X-API-VERSION=2.0"})
    public String specificHeaderVersionIsOneOrTwo() {
        log.info("versionIsOneOrTwo");
        return "OK";
    }

    @GetMapping(value = "/specific-content-type", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String specificContentTypeIsApplicationJson() {
        log.info("application/json");
        return "OK";
    }

    @GetMapping(value = "/specific-content-type", consumes = "!" + MediaType.APPLICATION_JSON_VALUE)
    public String specificContentTypeIsNotApplicationJson() {
        log.info("!application/json");
        return "OK";
    }

    @GetMapping(value = "/specific-accept", produces = MediaType.APPLICATION_JSON_VALUE)
    public String specificProduceIsApplicationJson() {
        log.info("application/json");
        return "OK";
    }

    @GetMapping(value = "/specific-accept", produces = "!" + MediaType.APPLICATION_JSON_VALUE)
    public String specificAcceptIsNotApplicationJson() {
        log.info("!application/json");
        return "OK";
    }

}
