package com.roy.mvc.servlet.web.frontcontroller.v5;

import com.roy.mvc.servlet.web.frontcontroller.ModelView;
import com.roy.mvc.servlet.web.frontcontroller.MyController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public interface MyHandlerAdapter {
    <T extends MyController> boolean support(T handler);
    <T extends MyController> ModelView handle(
            HttpServletRequest request,
            HttpServletResponse response,
            T handler) throws ServletException, IOException;

    default Map<String, String> createParam(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        request.getParameterNames().asIterator()
                .forEachRemaining(paramName -> params.put(paramName, request.getParameter(paramName)));
        return params;
    }
}
