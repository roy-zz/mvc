package com.roy.mvc.servlet.web.frontcontroller.v1;

import com.roy.mvc.servlet.web.frontcontroller.MyController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface MyControllerV1 extends MyController {
    void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}
