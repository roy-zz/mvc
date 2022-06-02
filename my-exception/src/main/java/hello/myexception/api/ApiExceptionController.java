package hello.myexception.api;

import hello.myexception.exception.UserException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ApiExceptionController {

    @GetMapping("/api/members/{id}")
    public MemberDto getMember(@PathVariable("id") String id) {

        switch (id) {
            case "exception":
                throw new RuntimeException("잘못된 사용자 아이디");
            case "bad-request":
                throw new IllegalArgumentException("잘못된 요청");
            case "user-exception":
                throw new UserException("사용자 정의 예외 발생");
            default:
                return new MemberDto(id, "id: " + id);
        }
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String memberId;
        private String name;
    }

}
