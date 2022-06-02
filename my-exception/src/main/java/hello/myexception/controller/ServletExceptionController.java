package hello.myexception.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ServletExceptionController {

    @GetMapping("/cause-exception")
    public void causeException() {
        throw new RuntimeException("강제로 발생시킨 예외");
    }

    @GetMapping("/cause-404-exception")
    public void cause404Exception(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value(), "404 메시지");
    }

    @GetMapping("/cause-500-exception")
    public void cause500Exception(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "500 메시지");
    }

}
