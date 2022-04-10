package com.roy.mvc.servlet.web.request;

import org.springframework.util.StreamUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet(name = "requestBodyTextServlet", urlPatterns = "/request-body-text")
public class RequestBodyTextServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletInputStream inputStream = request.getInputStream();
        String body = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        System.out.println("body = " + body);
        response.getWriter().write("OK");
    }

}
