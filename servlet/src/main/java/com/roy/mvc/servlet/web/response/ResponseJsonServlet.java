package com.roy.mvc.servlet.web.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roy.mvc.servlet.web.request.RequestBodyJsonServlet;
import lombok.Getter;
import lombok.Setter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "responseJsonServlet", urlPatterns = "/response-json")
public class ResponseJsonServlet extends HttpServlet {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("content-Type", "application/json");
        response.setCharacterEncoding("utf-8");
        DefaultData defaultData = new DefaultData();
        defaultData.setUsername("Roy");
        defaultData.setAge(20);
        String responseBody = objectMapper.writeValueAsString(defaultData);
        response.getWriter().write(responseBody);
    }

    @Getter @Setter
    public static class DefaultData {
        private String username;
        private int age;
    }

}
