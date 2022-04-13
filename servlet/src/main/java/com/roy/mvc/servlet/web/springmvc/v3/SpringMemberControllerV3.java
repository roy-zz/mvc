package com.roy.mvc.servlet.web.springmvc.v3;

import com.roy.mvc.servlet.web.servlet.domain.Member;
import com.roy.mvc.servlet.web.servlet.domain.MemberRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/springmvc/v3/members")
public class SpringMemberControllerV3 {
    private final MemberRepository repository = MemberRepository.getInstance();

    @RequestMapping(value = "/new-form", method = RequestMethod.GET)
    public String newForm() {
        return "new-form";
    }

    @PostMapping("/save")
    public String save(
            @RequestParam("username") String username,
            @RequestParam("age") int age,
            Model model
    ) {
        Member newMember = new Member(username, age);
        repository.save(newMember);
        model.addAttribute("member", newMember);
        return "save-result";
    }

    @GetMapping
    public String members(Model model) {
        List<Member> storedMembers = repository.findAll();
        model.addAttribute("members", storedMembers);
        return "members";
    }

}
