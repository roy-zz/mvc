package com.roy.mvc.servlet.web.servlet;

import com.roy.mvc.servlet.web.servlet.domain.Member;
import com.roy.mvc.servlet.web.servlet.domain.MemberRepository;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "memberSaveServlet", urlPatterns = "/servlet/members/save")
public class MemberSaveServlet extends HttpServlet {

    private final MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));
        Member newMember = new Member(username, age);
        memberRepository.save(newMember);
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");
        PrintWriter writer = response.getWriter();
        writer.write("<html>\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "성공\n" +
                        "<ul>\n" +
                        "    <li>id=" + newMember.getId() + "</li>\n" +
                        "    <li>username=" + newMember.getUsername() + "</li>\n" +
                        "    <li>age=" + newMember.getAge() + "</li>\n" +
                        "</ul>\n" +
                        "<a href=\"/index.html\">메인</a>\n" +
                        "</body>\n" +
                        "</html>");
    }

}
