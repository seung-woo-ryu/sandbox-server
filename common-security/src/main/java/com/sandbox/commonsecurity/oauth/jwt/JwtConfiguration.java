package com.sandbox.commonsecurity.oauth.jwt;

import com.nimbusds.jose.JWSAlgorithm;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class JwtConfiguration {
    public static final MacAlgorithm macAlgorithm = MacAlgorithm.HS256;
    public static final JWSAlgorithm jwsAlgorithm = JWSAlgorithm.HS256;

    @Bean
    public JwtProvider jwtProvider(JwtProperties jwtProperties) {
        return new JwtProvider(jwtProperties);
    }

    @Bean
    public JwtDecoder jwtDecoder(JwtProperties jwtProperties) {
        SecretKey key = new SecretKeySpec(
                jwtProperties.secret().getBytes(StandardCharsets.UTF_8),
                macAlgorithm.getName()
        );

        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(key)
            .macAlgorithm(macAlgorithm)
            .build();
        // issuer 검증(iss)
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(jwtProperties.issuer()));

        return decoder;
    }
}
