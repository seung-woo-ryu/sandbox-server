package com.sandbox.commonsecurity.oauth;

import com.sandbox.commonsecurity.oauth.userinfo.OAuth2UserInfo;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.Map;

@Getter
@Validated
public class SocialAuth2User extends DefaultOAuth2User {
    @NotNull
    private final OAuth2UserInfo oAuth2UserInfo;

    public SocialAuth2User(
            Collection<? extends GrantedAuthority> authorities,
            Map<String, Object> attributes,
            String nameAttributeKey,
            OAuth2UserInfo oAuth2UserInfo
    ) {
        super(authorities, attributes, nameAttributeKey);
        this.oAuth2UserInfo = oAuth2UserInfo;
    }
}
