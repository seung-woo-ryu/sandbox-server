package com.sandbox.playgroundapi.security;

import com.sandbox.commonsecurity.oauth.OAuthUserProvisioningService;
import com.sandbox.commonsecurity.oauth.ProvisionMember;
import com.sandbox.commonsecurity.oauth.SocialAuth2User;
import com.sandbox.commonsecurity.user.UserRole;
import com.sandbox.playgroundmember.entity.Member;
import com.sandbox.playgroundmember.service.MemberService;
import com.sandbox.playgroundmember.service.NicknameGenerator;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlaygroundProvisioningService implements OAuthUserProvisioningService {
    private final MemberService memberService;
    private final NicknameGenerator nicknameGenerator;

	@Override
	public ProvisionMember provision(SocialAuth2User socialAuth2User) {
		String email = socialAuth2User.getOAuth2UserInfo().getEmail();

		if (StringUtils.isBlank(email)) {
			throw new IllegalArgumentException("Email cannot be blank");
		}

		Optional<Member> byEmail = memberService.findByEmail(email);

		if (byEmail.isEmpty()) {
			String nickname = nicknameGenerator.generate();
			Member member = memberService.createMember(email, nickname);
			return new ProvisionMember(member.getId(), List.of(UserRole.USER));
		}
		return new ProvisionMember(byEmail.get().getId(), List.of(UserRole.USER));
	}
}
