package com.roy.mvc.servlet.web.frontcontroller.v4.controller;

import com.roy.mvc.servlet.web.frontcontroller.v4.MyControllerV4;
import com.roy.mvc.servlet.web.servlet.domain.Member;

import java.util.List;
import java.util.Map;

public class MemberSaveControllerV4 implements MyControllerV4 {
    @Override
    public String process(Map<String, String> params, Map<String, Object> model) {
        List<Member> storedMembers = getMembers();
        model.put("members", storedMembers);
        return "members";
    }
}
