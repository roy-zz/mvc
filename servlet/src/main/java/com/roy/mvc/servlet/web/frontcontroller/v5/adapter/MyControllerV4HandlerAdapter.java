package com.roy.mvc.servlet.web.frontcontroller.v5.adapter;

import com.roy.mvc.servlet.web.frontcontroller.ModelView;
import com.roy.mvc.servlet.web.frontcontroller.MyController;
import com.roy.mvc.servlet.web.frontcontroller.v4.MyControllerV4;
import com.roy.mvc.servlet.web.frontcontroller.v5.MyHandlerAdapter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MyControllerV4HandlerAdapter implements MyHandlerAdapter {
    @Override
    public <T extends MyController> boolean support(T handler) {
        return handler instanceof MyControllerV4;
    }

    @Override
    public <T extends MyController> ModelView handle(HttpServletRequest request, HttpServletResponse response, T handler) throws ServletException, IOException {
        MyControllerV4 controller = (MyControllerV4) handler;
        Map<String, String> params = createParam(request);
        Map<String, Object> model = new HashMap<>();
        String viewName = controller.process(params, model);

        ModelView modelView = new ModelView(viewName);
        modelView.setMapOfModel(model);
        return modelView;
    }
}
