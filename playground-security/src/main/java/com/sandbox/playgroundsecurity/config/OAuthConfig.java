package com.sandbox.playgroundsecurity.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPattern;

import java.io.PrintWriter;
import java.util.List;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class OAuthConfig {
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OAuthConfig.class);
	private final CustomOAuth2UserService customOAuth2UserService;

	public OAuthConfig(CustomOAuth2UserService customOAuth2UserService) {
		this.customOAuth2UserService = customOAuth2UserService;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, List<Customizer<HttpSecurity>> customizers, SecurityExcludePathProvider upperModuleExcludePathProvider) throws Exception {
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
			.oauth2Login(oauth -> oauth
				.successHandler(successHandler())
				.failureHandler(failureHandler())
				.userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
			)
			.httpBasic(Customizer.withDefaults());

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource(CorsConfiguration upperModuleConfiguration) {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", upperModuleConfiguration);
		return source;
	}


	@Bean
	public AuthenticationSuccessHandler successHandler() {
		return (request, response, authentication) -> {
			Object principal = authentication.getPrincipal();
			if (!(principal instanceof OAuth2User oAuth2User)) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}

			Object idAttr = oAuth2User.getAttributes().get("id");
			String id = idAttr == null ? null : idAttr.toString();

			String body = (id == null)
				? "{\"id\":null}"
				: "{\"id\":\"" + id.replace("\"", "\\\"") + "\"}";

			response.setCharacterEncoding("UTF-8");
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);

			try (PrintWriter writer = response.getWriter()) {
				writer.println(body);
				writer.flush();
			}
		};
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
