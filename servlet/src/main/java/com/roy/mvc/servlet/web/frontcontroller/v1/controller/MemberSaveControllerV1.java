package com.roy.mvc.servlet.web.frontcontroller.v1.controller;

import com.roy.mvc.servlet.web.frontcontroller.v1.MyControllerV1;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MemberSaveControllerV1 implements MyControllerV1 {

    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        saveMemberAndSetAttribute(request);
        String viewPath = "/WEB-INF/views/save-result.jsp";
        forward(viewPath, request, response);
    }

}
