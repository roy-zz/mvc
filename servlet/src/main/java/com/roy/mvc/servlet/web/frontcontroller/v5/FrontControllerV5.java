package com.roy.mvc.servlet.web.frontcontroller.v5;

import com.roy.mvc.servlet.web.frontcontroller.ModelView;
import com.roy.mvc.servlet.web.frontcontroller.MyController;
import com.roy.mvc.servlet.web.frontcontroller.MyView;
import com.roy.mvc.servlet.web.frontcontroller.v3.controller.MemberFormControllerV3;
import com.roy.mvc.servlet.web.frontcontroller.v3.controller.MemberListControllerV3;
import com.roy.mvc.servlet.web.frontcontroller.v3.controller.MemberSaveControllerV3;
import com.roy.mvc.servlet.web.frontcontroller.v4.controller.MemberFormControllerV4;
import com.roy.mvc.servlet.web.frontcontroller.v4.controller.MemberListControllerV4;
import com.roy.mvc.servlet.web.frontcontroller.v4.controller.MemberSaveControllerV4;
import com.roy.mvc.servlet.web.frontcontroller.v5.adapter.MyControllerV3HandlerAdapter;
import com.roy.mvc.servlet.web.frontcontroller.v5.adapter.MyControllerV4HandlerAdapter;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@WebServlet(name = "frontControllerServletV5", urlPatterns = "/front-controller/v5/*")
public class FrontControllerV5 extends HttpServlet {

    private final Map<String, MyController> handlerMappingMap = new HashMap<>();
    private final List<MyHandlerAdapter> handlerAdapters = new ArrayList<>();

    @PostConstruct
    private void initHandlerMappingMap() {
        handlerMappingMap.put("/front-controller/v5/v3/members/new-form", new MemberFormControllerV3());
        handlerMappingMap.put("/front-controller/v5/v3/members/save", new MemberSaveControllerV3());
        handlerMappingMap.put("/front-controller/v5/v3/members", new MemberListControllerV3());
        handlerMappingMap.put("/front-controller/v5/v4/members/new-form", new MemberFormControllerV4());
        handlerMappingMap.put("/front-controller/v5/v4/members/save", new MemberSaveControllerV4());
        handlerMappingMap.put("/front-controller/v5/v4/members", new MemberListControllerV4());
    }

    @PostConstruct
    private void initHandlerAdapters() {
        handlerAdapters.add(new MyControllerV3HandlerAdapter());
        handlerAdapters.add(new MyControllerV4HandlerAdapter());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        MyController handler = getHandler(request);
        if (Objects.isNull(handler)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        MyHandlerAdapter adapter = getHandlerAdapter(handler);
        ModelView modelView = adapter.handle(request, response, handler);

        MyView view = viewResolver(modelView.getViewName());
        view.render(modelView.getMapOfModel(), request, response);
    }

    private MyController getHandler(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return handlerMappingMap.get(requestURI);
    }

    private MyHandlerAdapter getHandlerAdapter(MyController handler) {
        return handlerAdapters.stream()
                .filter(adapter -> adapter.support(handler))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Not Found Adapter"));
    }

    private MyView viewResolver(String viewName) {
        return new MyView("/WEB-INF/views/" + viewName + ".jsp");
    }
}
