package com.roy.mvc.servlet.web.servlet.domain;

import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class MemberRepository {

    private static final Map<Long, Member> REPOSITORY = new ConcurrentHashMap<>();
    private static final AtomicLong ID = new AtomicLong(0L);

    private static final MemberRepository INSTANCE = new MemberRepository();

    public static MemberRepository getInstance() {
        return INSTANCE;
    }

    public Member save(Member member) {
        member.setId(ID.updateAndGet((n) -> ++n));
        REPOSITORY.put(member.getId(), member);
        return member;
    }

    public Member findById(Long id) {
        return REPOSITORY.get(id);
    }

    public List<Member> findAll() {
        return new ArrayList<>(REPOSITORY.values());
    }

    public void clear() {
        REPOSITORY.clear();
    }

}
