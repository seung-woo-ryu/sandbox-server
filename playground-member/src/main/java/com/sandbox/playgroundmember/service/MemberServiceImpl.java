package com.sandbox.playgroundmember.service;

import com.sandbox.playgroundmember.entity.Member;
import com.sandbox.playgroundmember.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {
	private final MemberRepository memberRepository;

	@Override
	public Optional<Member> findByEmail(String email) {
		return memberRepository.findByEmail(email);
	}

	@Override
	public Member findById(Long id) {
		return memberRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("Member not found id=" + id));
	}

	@Override
	@Transactional
	public Member createMember(String email, String nickname) {
		if (memberRepository.existsByEmail(email)) {
			throw new IllegalArgumentException("Email already exists: " + email);
		}
		if (memberRepository.existsByNickname(nickname)) {
			throw new IllegalArgumentException("Nickname already exists: " + nickname);
		}
		Member member = Member.of(email, nickname);
		return memberRepository.save(member);
	}

}
