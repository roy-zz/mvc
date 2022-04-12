package com.roy.mvc.servlet.web.frontcontroller;

import com.roy.mvc.servlet.web.servlet.domain.Member;
import com.roy.mvc.servlet.web.servlet.domain.MemberRepository;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface MyController {

    MemberRepository memberRepository = MemberRepository.getInstance();

    default void forward(String viewPath, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);
    }

    default void saveMemberAndSetAttribute(HttpServletRequest request) {
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));
        request.setAttribute("member", saveMember(username, age));
    }

    default void getMembersAndSetAttribute(HttpServletRequest request) {
        request.setAttribute("members", getMembers());
    }

    default ModelView saveMemberAndSetViewModel(Map<String, String> params, String viewName) {
        String username = params.get("username");
        int age = Integer.parseInt(params.get("age"));
        ModelView modelView = new ModelView(viewName);
        modelView.getMapOfModel().put("member", saveMember(username, age));
        return modelView;
    }

    default ModelView getMembersAndSetViewModel(String viewModel) {
        ModelView modelView = new ModelView(viewModel);
        modelView.getMapOfModel().put("members", getMembers());
        return modelView;
    }

    default Member saveMember(String username, int age) {
        Member member = new Member(username, age);
        memberRepository.save(member);
        return member;
    }

    default List<Member> getMembers() {
        return memberRepository.findAll();
    }

}
