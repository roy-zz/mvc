package com.roy.mvc.servlet.web.frontcontroller.v4;

import com.roy.mvc.servlet.web.frontcontroller.MyView;
import com.roy.mvc.servlet.web.frontcontroller.v4.controller.MemberFormControllerV4;
import com.roy.mvc.servlet.web.frontcontroller.v4.controller.MemberListControllerV4;
import com.roy.mvc.servlet.web.frontcontroller.v4.controller.MemberSaveControllerV4;

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

@WebServlet(name = "frontControllerServletV4", urlPatterns = "/front-controller/v4/*")
public class FrontControllerV4 extends HttpServlet {

    private final Map<String, MyControllerV4> controllerMap = new HashMap<>();

    @PostConstruct
    private void initialization() {
        controllerMap.put("/front-controller/v4/members/new-form", new MemberFormControllerV4());
        controllerMap.put("/front-controller/v4/members/save", new MemberSaveControllerV4());
        controllerMap.put("/front-controller/v4/members", new MemberListControllerV4());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        MyControllerV4 controller = controllerMap.get(requestURI);
        if (Objects.isNull(controller)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Map<String, String> params = createParams(request);
        Map<String, Object> model = new HashMap<>();

        String viewName = controller.process(params, model);

        MyView view = viewResolver(viewName);
        view.render(model, request, response);
    }

    private Map<String, String> createParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        request.getParameterNames().asIterator()
                .forEachRemaining(param -> params.put(param, request.getParameter(param)));
        return params;
    }

    private MyView viewResolver(String viewName) {
        return new MyView("/WEB-INF/views/" + viewName + ".jsp");
    }
}
