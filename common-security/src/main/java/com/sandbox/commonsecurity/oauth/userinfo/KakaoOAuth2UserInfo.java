package com.sandbox.commonsecurity.oauth.userinfo;

import com.sandbox.commonsecurity.oauth.SocialType;

import java.util.Map;

public class KakaoOAuth2UserInfo extends OAuth2UserInfo {
    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes, SocialType.KAKAO);
    }

    @Override
    public SocialType getSocialType() {
        return this.socialType;
    }

    @Override
    public String getId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getEmail() {
        Object accountObj = attributes.get("kakao_account");
        if (!(accountObj instanceof Map<?, ?> account)) {
            return null;
        }

        Object emailObj = account.get("email");
        if (!(emailObj instanceof String email) || email.isBlank()) {
            return null;
        }

        return (String) emailObj;
    }
}