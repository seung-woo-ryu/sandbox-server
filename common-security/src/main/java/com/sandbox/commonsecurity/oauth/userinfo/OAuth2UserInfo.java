package com.sandbox.commonsecurity.oauth.userinfo;

import com.sandbox.commonsecurity.oauth.SocialType;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public abstract class OAuth2UserInfo {
    protected Map<String, Object> attributes;
    protected SocialType socialType;

    public abstract String getId(); // 소셜 식별 값: 구글 sub, 카카오 id, 네이버 id 등
    public abstract @Nullable String getEmail();
    public abstract SocialType getSocialType();
}
