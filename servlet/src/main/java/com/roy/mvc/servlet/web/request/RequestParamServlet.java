package com.roy.mvc.servlet.web.request;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@WebServlet(name = "requestParamServlet", urlPatterns = "/request-param")
public class RequestParamServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("[모든 파라미터 조회 - START]");
        request.getParameterNames().asIterator()
                        .forEachRemaining(name -> System.out.printf("%s: %s%n", name, request.getParameter(name)));
        System.out.println("[모든 파라미터 조회 - END]");
        System.out.println();

        System.out.println("[파라미터 이름으로 조회]");
        String username = request.getParameter("username");
        System.out.printf("request.getParameter(username): %s%n", username);
        String age = request.getParameter("age");
        System.out.printf("request.getParameter(age): %s%n", age);
        System.out.println();

        System.out.println("[Key가 중복되는 파라미터 조회]");
        System.out.println("request.getParameterValues(username)");
        String[] usernames = request.getParameterValues("username");
        Arrays.stream(usernames).forEach(name -> {
            System.out.println("name = " + name);
        });

        response.getWriter().write("ok");
    }
}
