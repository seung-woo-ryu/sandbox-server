package com.sandbox.commonsecurity.oauth.jwt;

import com.sandbox.commonsecurity.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationTokenConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final String CLAIM_ROLE = "role";

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        String userId = jwt.getSubject();
        List<GrantedAuthority> authorities = jwt.getClaimAsStringList(CLAIM_ROLE)
            .stream()
            .map(role -> UserRole.from(role).getAuthority())
            .toList();

        Object principal = userId;

        return new JwtAuthenticationToken(principal, null, jwt, authorities);
    }
}
