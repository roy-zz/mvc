package com.roy.mvc.servlet.web.servlet.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static lombok.AccessLevel.PRIVATE;

@Getter @Setter
@NoArgsConstructor(access = PRIVATE)
public class Member {

    private Long id;
    private String username;
    private int age;

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }

}
