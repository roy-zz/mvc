package com.roy.mvc.servlet.web.servlet;

import com.roy.mvc.servlet.web.servlet.domain.Member;
import com.roy.mvc.servlet.web.servlet.domain.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MemberRepositoryTest {

    MemberRepository memberRepository = MemberRepository.getInstance();

    @AfterEach
    void afterEach() {
        memberRepository.clear();
    }

    @Test
    void saveTest() {
        Member member = new Member("Roy", 20);
        Member newMember = memberRepository.save(member);
        Member storedMember = memberRepository.findById(newMember.getId());
        assertEquals(newMember, storedMember);
    }

    @Test
    void findAll() {
        Member member1 = new Member("Roy", 20);
        Member member2 = new Member("Perry", 21);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findAll();
        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of(member1, member2)));
    }

}