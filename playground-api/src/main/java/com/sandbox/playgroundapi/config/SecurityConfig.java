package com.sandbox.playgroundapi.config;

import com.sandbox.playgroundsecurity.config.SecurityExcludePathContributor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Map;
import java.util.Set;

import static com.sandbox.playgroundapi.config.constant.ApiPrefixConstant.PUBLIC_API_V1;

@Configuration
public class SecurityConfig {
    public static final String OAUTH2_CODE_PATH = "/login/oauth2/code/**";

    @Bean
    public CorsConfiguration upperModuleConfiguration() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOriginPattern("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.setAllowCredentials(true);
        return corsConfiguration;
    }

    @Bean
    public SecurityExcludePathContributor securityExcludePathContributor() {
        return () -> Map.of(
            OAUTH2_CODE_PATH, Set.of(HttpMethod.POST, HttpMethod.GET),
            PUBLIC_API_V1+"/**", Set.of(HttpMethod.POST, HttpMethod.GET, HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.OPTIONS)
        );
    }
}
