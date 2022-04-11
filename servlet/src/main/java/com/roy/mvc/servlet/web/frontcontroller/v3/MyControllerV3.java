package com.roy.mvc.servlet.web.frontcontroller.v3;

import com.roy.mvc.servlet.web.frontcontroller.ModelView;
import com.roy.mvc.servlet.web.frontcontroller.MyController;

import java.util.Map;

public interface MyControllerV3 extends MyController {
    ModelView process(Map<String, String> params);
}
