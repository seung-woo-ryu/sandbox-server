package com.sandbox.commonsecurity.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Locale;

public enum UserRole {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private final String authorityName;
    private final GrantedAuthority authority;

    UserRole(String authorityName) {
        this.authorityName = authorityName;
        this.authority = new SimpleGrantedAuthority(authorityName);
    }

    public static UserRole from(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("role name must not be blank");
        }

        String normalized = name.trim();
        if (normalized.regionMatches(true, 0, "ROLE_", 0, "ROLE_".length())) {
            normalized = normalized.substring("ROLE_".length());
        }

        normalized = normalized.toUpperCase(Locale.ROOT);
        return UserRole.valueOf(normalized);
    }

    public String getAuthorityName() {
        return authorityName;
    }

    public GrantedAuthority getAuthority() {
        return authority;
    }
}
