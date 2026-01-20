package com.sandbox.commonsecurity.oauth;

import com.sandbox.commonsecurity.oauth.userinfo.OAuth2UserInfo;
import com.sandbox.commonsecurity.oauth.userinfo.OAuth2UserInfoFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Component
public class SocialOAuth2UserService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		// kakao, naaver
		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		// provider.${provider}.user-name-attribute 값
		String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

		// 외부(kakao, naver) 인증 객체
		OAuth2User oAuth2User = super.loadUser(userRequest);
		SocialType socialType = SocialType.from(registrationId);
		Collection<? extends GrantedAuthority> authorities = oAuth2User.getAuthorities();
		Map<String, Object> attributes = oAuth2User.getAttributes();

		OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(socialType, oAuth2User);

		return new SocialAuth2User(authorities, attributes, userNameAttributeName, oAuth2UserInfo);
    }
}
