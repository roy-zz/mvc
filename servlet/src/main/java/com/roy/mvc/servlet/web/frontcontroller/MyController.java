package com.roy.mvc.servlet.web.frontcontroller;

import com.roy.mvc.servlet.web.servlet.domain.Member;
import com.roy.mvc.servlet.web.servlet.domain.MemberRepository;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface MyController {

    MemberRepository memberRepository = MemberRepository.getInstance();

    default void forward(String viewPath, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);
    }

    default void saveMemberAndSetAttribute(HttpServletRequest request) {
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));
        Member member = new Member(username, age);
        memberRepository.save(member);
        request.setAttribute("member", member);
    }

    default void getMembersAndSetAttribute(HttpServletRequest request) {
        List<Member> storedMembers = memberRepository.findAll();
        request.setAttribute("members", storedMembers);
    }

}
