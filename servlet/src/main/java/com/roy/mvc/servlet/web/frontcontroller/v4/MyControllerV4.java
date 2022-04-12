package com.roy.mvc.servlet.web.frontcontroller.v4;

import com.roy.mvc.servlet.web.frontcontroller.MyController;

import java.util.Map;

public interface MyControllerV4 extends MyController {
    String process(Map<String, String> params, Map<String, Object> model);
}
