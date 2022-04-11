package com.roy.mvc.servlet.web.frontcontroller.v2;

import com.roy.mvc.servlet.web.frontcontroller.MyController;
import com.roy.mvc.servlet.web.frontcontroller.MyView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface MyControllerV2 extends MyController {
    MyView process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}
