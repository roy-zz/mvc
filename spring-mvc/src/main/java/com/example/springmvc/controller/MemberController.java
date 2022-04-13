package com.example.springmvc.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/member")
public class MemberController {

    // 특정 회원 조회
    @GetMapping(value = "/{memberId}")
    public String findMember(@PathVariable Long memberId) {
        return "findMember";
    }

    // 모든 회원 조회
    @GetMapping
    public String findAllMembers() {
        return "findAllMember";
    }

    // 회원 등록
    @PostMapping
    public String saveMember() {
        return "saveMember";
    }

    // 회원 수정 (Put)
    @PutMapping("/{memberId}")
    public String putMember(
            @PathVariable Long memberId,
            @RequestBody MemberUpdateDTO dto) {
        return "putMember";
    }

    // 회원 수정 (Patch)
    @PatchMapping("/{memberId}")
    public String patchMember(
            @PathVariable Long memberId,
            @RequestBody MemberUpdateDTO dto) {
        return "patchMember";
    }

    // 회원 삭제
    @DeleteMapping("/{memberId}")
    public String deleteMember(@PathVariable Long memberId) {
        return "deleteMember";
    }

    static class MemberUpdateDTO {

    }

}
