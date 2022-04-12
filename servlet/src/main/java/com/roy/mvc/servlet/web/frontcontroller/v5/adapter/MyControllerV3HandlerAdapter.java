package com.roy.mvc.servlet.web.frontcontroller.v5.adapter;

import com.roy.mvc.servlet.web.frontcontroller.ModelView;
import com.roy.mvc.servlet.web.frontcontroller.MyController;
import com.roy.mvc.servlet.web.frontcontroller.v3.MyControllerV3;
import com.roy.mvc.servlet.web.frontcontroller.v5.MyHandlerAdapter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class MyControllerV3HandlerAdapter implements MyHandlerAdapter {
    @Override
    public <T extends MyController> boolean support(T handler) {
        return handler instanceof MyControllerV3;
    }

    @Override
    public <T extends MyController> ModelView handle(HttpServletRequest request, HttpServletResponse response, T handler) throws ServletException, IOException {
        MyControllerV3 controller = (MyControllerV3) handler;
        Map<String, String> params = createParam(request);
        return controller.process(params);
    }
}
