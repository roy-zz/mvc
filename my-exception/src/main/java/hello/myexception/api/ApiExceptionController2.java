package hello.myexception.api;

import hello.myexception.exception.ErrorResponse;
import hello.myexception.exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api-2")
public class ApiExceptionController2 {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse illegalExceptionHandle(IllegalArgumentException exception) {
        log.error("call illegal exception handle", exception);
        return new ErrorResponse("ILLEGAL_ARGUMENT_EXCEPTION", exception.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> userExceptionHandle(UserException exception) {
        log.error("call user exception handle", exception);
        ErrorResponse errorResponse = new ErrorResponse("USER_EXCEPTION", exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResponse exceptionHandle(Exception exception) {
        log.error("call exception handle", exception);
        return new ErrorResponse("EXCEPTION", exception.getMessage());
    }

}
