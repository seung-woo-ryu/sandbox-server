package com.sandbox.commonsecurity.oauth.userinfo;

import com.sandbox.commonsecurity.oauth.SocialType;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

public final class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(SocialType socialType, OAuth2User user) {
        Map<String, Object> attributes = user.getAttributes();
        if (socialType == SocialType.KAKAO) {
            return new KakaoOAuth2UserInfo(attributes);
        } else if(socialType == SocialType.NAVER){
            return new NaverOAuth2UserInfo(attributes);
        }

        throw new IllegalArgumentException("Unsupported social type: " + socialType);
    }

}
