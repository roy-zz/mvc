package hello.myexception.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ServletExceptionController {

    @GetMapping("/cause-exception")
    public void causeException() {
        throw new RuntimeException("강제로 발생시킨 예외");
    }

}
