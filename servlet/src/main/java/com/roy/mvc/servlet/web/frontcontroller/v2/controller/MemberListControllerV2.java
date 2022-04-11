package com.roy.mvc.servlet.web.frontcontroller.v2.controller;

import com.roy.mvc.servlet.web.frontcontroller.MyView;
import com.roy.mvc.servlet.web.frontcontroller.v2.MyControllerV2;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MemberListControllerV2 implements MyControllerV2 {

    @Override
    public MyView process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        getMembersAndSetAttribute(request);
        return new MyView("/WEB-INF/views/members.jsp");
    }

}
