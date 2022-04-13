package com.roy.mvc.servlet.web.springmvc.v1;

import com.roy.mvc.servlet.web.servlet.domain.Member;
import com.roy.mvc.servlet.web.servlet.domain.MemberRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class SpringMemberListControllerV1 {

    private final MemberRepository repository = MemberRepository.getInstance();

    @RequestMapping("/springmvc/v1/members")
    public ModelAndView process() {
        List<Member> storedMembers = repository.findAll();
        ModelAndView mv = new ModelAndView("members");
        mv.addObject("members", storedMembers);
        return mv;
    }

}
