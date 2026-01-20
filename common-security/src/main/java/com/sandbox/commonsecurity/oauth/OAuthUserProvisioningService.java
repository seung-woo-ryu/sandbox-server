package com.sandbox.commonsecurity.oauth;

/**
 * OAuth2 로그인 시 외부 사용자 정보를 바탕으로 "우리 서비스 사용자"를 생성/연동(provision)하는 포트.
 *
 * 구현체는 각 웹앱(각 모듈)에서 제공해야 합니다.
 */
public interface OAuthUserProvisioningService {
	ProvisionMember provision(SocialAuth2User socialAuth2User);
}

