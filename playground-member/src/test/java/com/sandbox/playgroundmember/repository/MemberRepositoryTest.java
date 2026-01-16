package com.sandbox.playgroundmember.repository;

import com.sandbox.playgroundmember.TestBootConfiguration;
import com.sandbox.playgroundmember.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(classes = TestBootConfiguration.class)
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void saveAndFindByEmail() {
        Member m = Member.of("repo-test@example.com", "repoNick");
        memberRepository.save(m);

        Optional<Member> found = memberRepository.findByEmail("repo-test@example.com");
        assertTrue(found.isPresent());
        assertEquals("repoNick", found.get().getNickname());
    }

    @Test
    void existsChecks() {
        memberRepository.save(Member.of("exists-test@example.com", "existsNick"));

        assertTrue(memberRepository.existsByEmail("exists-test@example.com"));
        assertTrue(memberRepository.existsByNickname("existsNick"));
        assertFalse(memberRepository.existsByEmail("no-such@example.com"));
    }
}
