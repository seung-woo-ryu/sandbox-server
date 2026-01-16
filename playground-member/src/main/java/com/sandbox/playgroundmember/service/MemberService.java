package com.sandbox.playgroundmember.service;

import com.sandbox.playgroundmember.entity.Member;

import java.util.Optional;

public interface MemberService {
    Optional<Member> findByEmail(String email);
    Member findById(Long id);
    Member createMember(String email, String nickname);
}
