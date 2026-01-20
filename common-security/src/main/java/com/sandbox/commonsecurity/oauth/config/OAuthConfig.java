package com.sandbox.commonsecurity.oauth.config;

import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.sandbox.commonsecurity.SecurityExcludePathProvider;
import com.sandbox.commonsecurity.oauth.SocialOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPattern;

import java.io.PrintWriter;
import java.util.List;

@Configuration
public class OAuthConfig {
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OAuthConfig.class);
	private final SocialOAuth2UserService socialOAuth2UserService;

	public OAuthConfig(SocialOAuth2UserService socialOAuth2UserService) {
		this.socialOAuth2UserService = socialOAuth2UserService;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, List<Customizer<HttpSecurity>> customizers,
												   SecurityExcludePathProvider upperModuleExcludePathProvider,
												   AuthenticationSuccessHandler authenticationSuccessHandler,
												   JwtDecoder jwtDecoder,
												   Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationTokenConverter) throws Exception {
		// 애플리케이션 부팅 시 exclude 목록을 한번 출력
		upperModuleExcludePathProvider.getExcludePatterns().forEach((pattern, methods) ->
			log.info("[SECURITY][EXCLUDE] pattern='{}' methods={}", pattern.getPatternString(), methods)
		);

		for (Customizer<HttpSecurity> customizer : customizers) {
			customizer.customize(http);
		}

		AuthorizationManager<RequestAuthorizationContext> logAndAuthorize = (authentication, context) -> {
			var request = context.getRequest();
			String uri = request.getRequestURI();
			String methodStr = request.getMethod();
			HttpMethod method = HttpMethod.valueOf(methodStr);

			boolean excluded = false;
			PathPattern matchedPattern = null;
			if (method != null) {
				var path = org.springframework.http.server.PathContainer.parsePath(uri);
				for (var entry : upperModuleExcludePathProvider.getExcludePatterns().entrySet()) {
					if (entry.getKey().matches(path) && entry.getValue().contains(method)) {
						excluded = true;
						matchedPattern = entry.getKey();
						break;
					}
				}
			}

			log.info("[SECURITY][REQ] {} {} excluded={} matchedPattern={}", methodStr, uri, excluded, matchedPattern == null ? null : matchedPattern.getPatternString());

			if (excluded) {
				return new AuthorizationDecision(true);
			}
			// 나머지는 기존 정책(anyRequest().authenticated())과 동일하게 "인증되어 있으면 허용"
			boolean authenticated = authentication.get() != null && authentication.get().isAuthenticated();
			return new AuthorizationDecision(authenticated);
		};

		http
			.csrf(csrf -> csrf.disable())
			.cors(Customizer.withDefaults())
			.authorizeHttpRequests(registry -> registry
				.anyRequest().access(logAndAuthorize)
			)
			.oauth2Login(oauth2 -> oauth2
				.successHandler(authenticationSuccessHandler)
				.failureHandler(failureHandler())
				.userInfoEndpoint(userInfo -> userInfo.userService(socialOAuth2UserService))
			)
			.oauth2ResourceServer(oauth2 -> oauth2
				.jwt( jwt -> jwt
					.decoder(new NimbusJwtDecoder(new DefaultJWTProcessor<>()))
					.jwtAuthenticationConverter(jwtAuthenticationTokenConverter)))
			.sessionManagement(session-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.exceptionHandling(v -> v.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));


		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource(CorsConfiguration upperModuleConfiguration) {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", upperModuleConfiguration);
		return source;
	}

	@Bean
	public AuthenticationFailureHandler failureHandler() {
		return (request, response, e) -> {
			response.setCharacterEncoding("UTF-8");
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);

			try (PrintWriter writer = response.getWriter()) {
				writer.println("failur!!!!");
				writer.flush();
			}
		};
	}
}
