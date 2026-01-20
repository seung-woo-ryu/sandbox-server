package com.sandbox.commonsecurity;

import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

@UtilityClass
public class CookieUtils {
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    private static final String SAME_SITE_POLICY = "Lax";

    // todo: 쿠키 정책들 외부 설정화 고려
    /**
     * @param secure HTTPS 환경에서만 쿠키를 전송할지 여부
     * @param domain 쿠키가 유효한 도메인(비워두면 host-only 쿠키로 생성)
     */
    public ResponseCookie createRefreshTokenCookie(String refreshToken, boolean secure, String domain, long maxAge) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
            .httpOnly(true)
            .secure(secure)
            .path("/")
            .maxAge(maxAge)
            .sameSite(SAME_SITE_POLICY);

        if (domain != null && !domain.isBlank()) {
            builder.domain(domain);
        }

        return builder.build();
    }
    public void addCookie(HttpServletResponse response, ResponseCookie cookie) {
        if (cookie == null) {
            return;
        }

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
