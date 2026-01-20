package com.sandbox.commonsecurity.oauth;

import com.sandbox.commonsecurity.user.UserRole;

import java.util.List;

/**
 * OAuth2 로그인 프로비저닝 결과.
 *
 * @param memberId 우리 서비스의 회원 식별자(PK)
 */
public record ProvisionMember(
        Long memberId,
        List<UserRole> userRoles
) {
}
