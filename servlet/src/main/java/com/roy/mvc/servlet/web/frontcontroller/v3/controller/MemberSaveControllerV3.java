package com.roy.mvc.servlet.web.frontcontroller.v3.controller;

import com.roy.mvc.servlet.web.frontcontroller.ModelView;
import com.roy.mvc.servlet.web.frontcontroller.v3.MyControllerV3;

import java.util.Map;

public class MemberSaveControllerV3 implements MyControllerV3 {
    @Override
    public ModelView process(Map<String, String> params) {
        return saveMemberAndSetViewModel(params, "save-result");
    }
}
