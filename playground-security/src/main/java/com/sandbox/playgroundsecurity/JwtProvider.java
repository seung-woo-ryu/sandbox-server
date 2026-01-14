package com.sandbox.playgroundsecurity;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sandbox.playgroundsecurity.config.JwtProperties;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

public class JwtProvider {

    public static final String CLAIM_ROLE = "role";
    public static final String CLAIM_TYPE = "type";
    public static final String TYPE_ACCESS = "access";
    public static final String TYPE_REFRESH = "refresh";

    private final Algorithm algorithm;
    private final JWTVerifier verifier;

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

        this.issuer = issuer;
        this.accessTokenTtlSeconds = accessTokenTtlSeconds;
        this.refreshTokenTtlSeconds = refreshTokenTtlSeconds;

        this.algorithm = Algorithm.HMAC256(secret);
        this.verifier = JWT.require(this.algorithm)
            .withIssuer(this.issuer)
            .build();
    }

    public String createAccessToken(String userId, String role) {
        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(role, "role");

        Instant now = Instant.now();
        Instant exp = now.plusSeconds(accessTokenTtlSeconds);

        return JWT.create()
            .withIssuer(issuer)
            .withSubject(userId)
            .withIssuedAt(Date.from(now))
            .withExpiresAt(Date.from(exp))
            .withClaim(CLAIM_TYPE, TYPE_ACCESS)
            .withClaim(CLAIM_ROLE, role)
            .sign(algorithm);
    }

    public String createRefreshToken(String userId) {
        Objects.requireNonNull(userId, "userId");

        Instant now = Instant.now();
        Instant exp = now.plusSeconds(refreshTokenTtlSeconds);

        return JWT.create()
            .withIssuer(issuer)
            .withSubject(userId)
            .withIssuedAt(Date.from(now))
            .withExpiresAt(Date.from(exp))
            .withClaim(CLAIM_TYPE, TYPE_REFRESH)
            .sign(algorithm);
    }

    public DecodedJWT verify(String token) throws JWTVerificationException {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("token must not be blank");
        }
        return verifier.verify(token);
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

    public Optional<String> getRole(String token) {
        DecodedJWT jwt = verify(token);
        String role = jwt.getClaim(CLAIM_ROLE).asString();
        return Optional.ofNullable(role);
    }

    public boolean isAccessToken(String token) {
        DecodedJWT jwt = verify(token);
        String type = jwt.getClaim(CLAIM_TYPE).asString();
        return TYPE_ACCESS.equals(type);
    }

    public boolean isRefreshToken(String token) {
        DecodedJWT jwt = verify(token);
        String type = jwt.getClaim(CLAIM_TYPE).asString();
        return TYPE_REFRESH.equals(type);
    }

    public long getExpiresAtEpochSeconds(String token) {
        Date exp = verify(token).getExpiresAt();
        if (exp == null) return 0L;
        return exp.toInstant().getEpochSecond();
    }
}