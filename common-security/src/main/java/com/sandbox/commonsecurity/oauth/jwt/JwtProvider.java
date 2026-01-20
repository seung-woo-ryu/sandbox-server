package com.sandbox.commonsecurity.oauth.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

/**
 * JWT 발급/검증 유틸리티.
 *
 * <p>Spring Security Resource Server가 기본적으로 사용하는 Nimbus(JOSE + JWT) 라이브러리만 사용해
 * HS256(HMAC-SHA256) 기반 토큰을 발급/검증합니다.
 */
public class JwtProvider {

    /** Roles(권한) 클레임 키 */
    public static final String CLAIM_ROLES = "roles";
    /** 토큰 타입(Access/Refresh) 클레임 키 */
    public static final String CLAIM_TYPE = "type";

    public static final String TYPE_ACCESS = "access";
    public static final String TYPE_REFRESH = "refresh";

    private final String secret;
    private final String issuer;
    private final long accessTokenTtlSeconds;
    private final long refreshTokenTtlSeconds;

    public JwtProvider(JwtProperties props) {
        this(
                props.secret(),
                props.issuer(),
                props.accessTokenTtlSeconds(),
                props.refreshTokenTtlSeconds()
        );
    }

    public JwtProvider(String secret,
                       String issuer,
                       long accessTokenTtlSeconds,
                       long refreshTokenTtlSeconds) {

        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("secret must not be blank");
        }
        if (issuer == null || issuer.isBlank()) {
            throw new IllegalArgumentException("issuer must not be blank");
        }
        if (accessTokenTtlSeconds <= 0) {
            throw new IllegalArgumentException("accessTokenTtlSeconds must be positive");
        }
        if (refreshTokenTtlSeconds <= 0) {
            throw new IllegalArgumentException("refreshTokenTtlSeconds must be positive");
        }

        this.secret = secret;
        this.issuer = issuer;
        this.accessTokenTtlSeconds = accessTokenTtlSeconds;
        this.refreshTokenTtlSeconds = refreshTokenTtlSeconds;

        // 참고: HS256은 최소 256-bit(32 bytes) 이상의 secret을 권장
        if (secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException("secret must be at least 32 bytes for HS256");
        }
    }

    public String createAccessToken(String userId, List<String> roles) {
        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(roles, "role");

        Instant now = Instant.now();
        Instant exp = now.plusSeconds(accessTokenTtlSeconds);

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer(issuer)
                .subject(userId)
                .issueTime(Date.from(now))
                .expirationTime(Date.from(exp))
                .jwtID(UUID.randomUUID().toString())
                .claim(CLAIM_TYPE, TYPE_ACCESS)
                .claim(CLAIM_ROLES, roles)
                .build();

        return sign(claims);
    }

    public String createRefreshToken(String userId) {
        Objects.requireNonNull(userId, "userId");

        Instant now = Instant.now();
        Instant exp = now.plusSeconds(refreshTokenTtlSeconds);

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer(issuer)
                .subject(userId)
                .issueTime(Date.from(now))
                .expirationTime(Date.from(exp))
                .jwtID(UUID.randomUUID().toString())
                .claim(CLAIM_TYPE, TYPE_REFRESH)
                .build();

        return sign(claims);
    }

    /**
     * Nimbus SignedJWT로 서명(HS256)하고 compact serialization 문자열을 반환합니다.
     */
    private String sign(JWTClaimsSet claims) {
        try {
            SignedJWT jwt = new SignedJWT(new JWSHeader(JwtConfiguration.jwsAlgorithm), claims);
            jwt.sign(new MACSigner(secret.getBytes(StandardCharsets.UTF_8)));
            return jwt.serialize();
        } catch (JOSEException e) {
            throw new IllegalStateException("Failed to sign JWT", e);
        }
    }

    /**
     * 토큰 서명/issuer/exp/iat/jti 존재 여부를 검증하고 claim set을 반환합니다.
     */
    public JWTClaimsSet verify(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("token must not be blank");
        }

        try {
            SignedJWT jwt = SignedJWT.parse(token);
            boolean ok = jwt.verify(new MACVerifier(secret.getBytes(StandardCharsets.UTF_8)));
            if (!ok) {
                throw new IllegalArgumentException("Invalid JWT signature");
            }

            JWTClaimsSet claims = jwt.getJWTClaimsSet();

            if (!issuer.equals(claims.getIssuer())) {
                throw new IllegalArgumentException("Invalid issuer");
            }
            if (claims.getIssueTime() == null) {
                throw new IllegalArgumentException("Missing claim: iat");
            }
            if (claims.getExpirationTime() == null) {
                throw new IllegalArgumentException("Missing claim: exp");
            }
            if (claims.getJWTID() == null || claims.getJWTID().isBlank()) {
                throw new IllegalArgumentException("Missing claim: jti");
            }
            if (claims.getExpirationTime().toInstant().isBefore(Instant.now())) {
                throw new IllegalArgumentException("Token expired");
            }

            return claims;
        } catch (Exception e) {
            // 소비측에서 401 처리하기 쉽도록 unchecked로 래핑(프로젝트 정책에 맞게 커스터마이징 가능)
            throw (e instanceof IllegalArgumentException) ? (IllegalArgumentException) e : new IllegalArgumentException("Invalid token", e);
        }
    }

    public boolean isValid(String token) {
        try {
            verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUserId(String token) {
        return verify(token).getSubject();
    }

    public String getJti(String token) {
        return verify(token).getJWTID();
    }

    public Optional<String> getRole(String token) {
        Object role = verify(token).getClaim(CLAIM_ROLES);
        return Optional.ofNullable(role == null ? null : String.valueOf(role));
    }

    public boolean isAccessToken(String token) {
        Object type = verify(token).getClaim(CLAIM_TYPE);
        return TYPE_ACCESS.equals(type);
    }

    public boolean isRefreshToken(String token) {
        Object type = verify(token).getClaim(CLAIM_TYPE);
        return TYPE_REFRESH.equals(type);
    }

    public long getExpiresAtEpochSeconds(String token) {
        Date exp = verify(token).getExpirationTime();
        if (exp == null) return 0L;
        return exp.toInstant().getEpochSecond();
    }
}
