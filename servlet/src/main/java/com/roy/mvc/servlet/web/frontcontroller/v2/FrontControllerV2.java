package com.roy.mvc.servlet.web.frontcontroller.v2;

import com.roy.mvc.servlet.web.frontcontroller.MyView;
import com.roy.mvc.servlet.web.frontcontroller.v2.controller.MemberFormControllerV2;
import com.roy.mvc.servlet.web.frontcontroller.v2.controller.MemberListControllerV2;
import com.roy.mvc.servlet.web.frontcontroller.v2.controller.MemberSaveControllerV2;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@WebServlet(name = "frontControllerServletV2", urlPatterns = "/front-controller/v2/*")
public class FrontControllerV2 extends HttpServlet {

    private final Map<String, MyControllerV2> controllerMap = new HashMap<>();

    @PostConstruct
    private void initialization() {
        controllerMap.put("/front-controller/v2/members/new-form", new MemberFormControllerV2());
        controllerMap.put("/front-controller/v2/members/save", new MemberSaveControllerV2());
        controllerMap.put("/front-controller/v2/members", new MemberListControllerV2());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        MyControllerV2 controller = controllerMap.get(requestURI);
        if (Objects.isNull(controller)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        MyView view = controller.process(request, response);
        view.render(request, response);
    }

}
