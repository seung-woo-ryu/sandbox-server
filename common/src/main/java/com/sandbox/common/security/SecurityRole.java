package com.sandbox.common.security;

public enum SecurityRole {
    USER,
    ADMIN;

    public static final  String ROLE_PREFIX = "ROLE_";

    public String getRoleName() {
        return ROLE_PREFIX + this.name();
    }
}
