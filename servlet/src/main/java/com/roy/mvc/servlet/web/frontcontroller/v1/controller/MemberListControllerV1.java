package com.roy.mvc.servlet.web.frontcontroller.v1.controller;

import com.roy.mvc.servlet.web.frontcontroller.v1.MyControllerV1;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MemberListControllerV1 implements MyControllerV1 {

    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        getMembersAndSetAttribute(request);
        String viewPath = "/WEB-INF/views/members.jsp";
        forward(viewPath, request, response);
    }

}
