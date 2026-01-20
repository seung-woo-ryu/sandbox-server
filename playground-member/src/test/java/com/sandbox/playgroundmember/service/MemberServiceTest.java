package com.sandbox.playgroundmember.service;

import com.sandbox.common.security.SecurityRole;
import com.sandbox.playgroundmember.TestBootConfiguration;
import com.sandbox.playgroundmember.entity.Member;
import com.sandbox.playgroundmember.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TestBootConfiguration.class)
@Transactional
public class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void cleanup() {
        memberRepository.deleteAll();
    }

    @Test
    void createMember_success() {
        Member saved = memberService.createMember("svc-test@example.com", "svcNick");
        assertNotNull(saved.getId());

        Member found = memberService.findById(saved.getId());
        assertEquals("svc-test@example.com", found.getEmail());
        assertEquals("svcNick", found.getNickname());
    }

    @Test
    void createMember_duplicateEmail_throws() {
        memberRepository.save(Member.createWithRole("dup@example.com", "dup1", SecurityRole.USER));

        assertThrows(IllegalArgumentException.class, () ->
                memberService.createMember("dup@example.com", "dup2")
        );
    }

}
