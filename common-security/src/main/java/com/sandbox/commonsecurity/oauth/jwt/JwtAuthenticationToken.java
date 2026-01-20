package com.sandbox.commonsecurity.oauth.jwt;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;

/**
 * Bearer JWT 기반 인증을 표현하는 Authentication.
 *
 * <p>권장 사용:
 * <ul>
 *   <li>{@code principal}: 도메인 사용자(UserDetails/CustomPrincipal 등)</li>
 *   <li>{@code credentials}: 원본 access token 문자열(보안상 인증 완료 후 null 처리도 가능)</li>
 *   <li>{@code jwt}: 디코딩된 {@link Jwt} (claim 접근용)</li>
 * </ul>
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    @Nullable
    private final String credentials;

    @Nullable
    private final Jwt jwt;

    public JwtAuthenticationToken(
            Object principal,
            @Nullable String credentials,
            @Nullable Jwt jwt,
            Collection<? extends GrantedAuthority> authorities,
            boolean authenticated
    ) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        this.jwt = jwt;
        setAuthenticated(authenticated);
    }

    public JwtAuthenticationToken(
            Object principal,
            @Nullable String credentials,
            @Nullable Jwt jwt,
            Collection<? extends GrantedAuthority> authorities
    ) {
        this(principal, credentials, jwt, authorities, true);
    }

    /**
     * 인증이 완료되지 않은 상태(인증 이전) 토큰.
     */
    public static JwtAuthenticationToken unauthenticated(String rawToken) {
        return new JwtAuthenticationToken("anonymous", rawToken, null, List.of(), false);
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    public @Nullable Jwt getJwt() {
        return jwt;
    }
}
