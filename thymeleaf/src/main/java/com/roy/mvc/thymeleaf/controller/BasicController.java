package com.roy.mvc.thymeleaf.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("SpringMVCViewInspection")
@Controller
@RequestMapping("/basic")
public class BasicController {

    @GetMapping("/text-basic")
    public String textBasic(Model model) {
        model.addAttribute("data", "Hello Spring!");
        return "basic/text-basic";
    }

    @GetMapping("/text-unescaped")
    public String textUnescaped(Model model) {
        model.addAttribute("data", "Hello <B>Spring!</b>");
        return "basic/text-unescaped";
    }

    @GetMapping("/variable")
    public String variable(Model model) {
        List<User> userList = List.of(
                new User("userA", 10),
                new User("userB", 20)
        );
        Map<String, User> userMap = new HashMap<>(
                Map.of("userA", userList.get(0), "userB", userList.get(1))
        );
        model.addAttribute("user", userList.get(0));
        model.addAttribute("users", userList);
        model.addAttribute("userMap", userMap);
        return "basic/variable";
    }

    @Data
    @AllArgsConstructor
    static class User {
        private String username;
        private int age;
    }

    @GetMapping("/basic-objects")
    public String basicObjects(HttpSession session) {
        session.setAttribute("sessionData", "Hello Session");
        return "basic/basic-objects";
    }

    @Component("royBean")
    static class RoyBean {
        public String roy(String data) {
            return String.format("Hello %s", data);
        }
    }

    @GetMapping("/date")
    public String date(Model model) {
        model.addAttribute("localDateTime", LocalDateTime.now());
        return "basic/date";
    }

}
