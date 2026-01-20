package com.sandbox.commonsecurity.oauth.config;

import com.sandbox.commonsecurity.oauth.jwt.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({JwtProperties.class, OAuthRedirectProperties.class})
public class PropertiesConfig {
}
