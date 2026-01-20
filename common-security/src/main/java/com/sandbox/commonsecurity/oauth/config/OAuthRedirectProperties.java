package com.sandbox.commonsecurity.oauth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * OAuth2 로그인 완료 후 리다이렉트 대상(프론트 URL 등) 설정.
 *
 * <p>값은 최종 애플리케이션(playground-api 등)의 application-*.properties에서 주입받습니다.
 */
@ConfigurationProperties(prefix = "app")
public record OAuthRedirectProperties(
        String frontendUrl,
        String oauth2SuccessRedirectPath
) {
    public String resolveRedirectUrl() {
        String base = frontendUrl == null ? "" : frontendUrl.trim();
        while (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }

        String path = (oauth2SuccessRedirectPath == null || oauth2SuccessRedirectPath.isBlank())
                ? "/"
                : oauth2SuccessRedirectPath.trim();

        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        return base + path;
    }
}
