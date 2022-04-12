package com.roy.mvc.servlet.web.frontcontroller.v4.controller;

import com.roy.mvc.servlet.web.frontcontroller.v4.MyControllerV4;

import java.util.Map;

public class MemberFormControllerV4 implements MyControllerV4 {
    @Override
    public String process(Map<String, String> params, Map<String, Object> model) {
        return "new-form";
    }
}
