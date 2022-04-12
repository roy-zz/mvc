package com.roy.mvc.servlet.web.frontcontroller.v4.controller;

import com.roy.mvc.servlet.web.frontcontroller.v4.MyControllerV4;
import com.roy.mvc.servlet.web.servlet.domain.Member;

import java.util.Map;

public class MemberListControllerV4 implements MyControllerV4 {
    @Override
    public String process(Map<String, String> params, Map<String, Object> model) {
        Member newMember = saveMember(params.get("username"), Integer.parseInt(params.get("age")));
        model.put("member", newMember);
        return "save-result";
    }
}
