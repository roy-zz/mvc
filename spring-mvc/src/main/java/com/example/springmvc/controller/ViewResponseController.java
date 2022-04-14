package com.example.springmvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ViewResponseController {

    @RequestMapping(value = "/response-view", headers = "X-API-VERSION=1.0")
    public ModelAndView responseViewV1() {
        return new ModelAndView("response/default")
                .addObject("data", "hello");
    }

    @RequestMapping(value = "/response-view", headers = "X-API-VERSION=2.0")
    public String responseViewV2(Model model) {
        model.addAttribute("data", "hello");
        return "response/default";
    }

    @RequestMapping(value = "/response/default", headers = "X-API-VERSION=3.0")
    public void responseViewV3(Model model) {
        model.addAttribute("data", "hello");
    }

}
