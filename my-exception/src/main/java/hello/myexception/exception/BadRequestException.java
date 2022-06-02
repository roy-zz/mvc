package hello.myexception.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "message.bad-request")
// @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "클라이언트의 잘못된 요청")
public class BadRequestException extends RuntimeException {
}
