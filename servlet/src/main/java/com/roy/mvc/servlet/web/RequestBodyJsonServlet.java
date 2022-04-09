package com.roy.mvc.servlet.web;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.util.StreamUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet(name = "requestBodyJsonServlet", urlPatterns = "/request-body-json")
public class RequestBodyJsonServlet extends HttpServlet {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletInputStream inputStream = request.getInputStream();
        String body = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        System.out.println("body = " + body);
        DefaultData defaultData = objectMapper.readValue(body, DefaultData.class);
        System.out.println("defaultData. = " + defaultData.getUsername());
        System.out.println("defaultData.getAge() = " + defaultData.getAge());
        response.getWriter().write("OK");
    }

    @Getter
    static class DefaultData {
        private String username;
        private int age;
    }
}
