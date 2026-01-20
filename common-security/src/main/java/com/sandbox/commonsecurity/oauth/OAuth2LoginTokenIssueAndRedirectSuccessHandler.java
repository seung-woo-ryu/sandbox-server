package com.sandbox.commonsecurity.oauth;

import com.sandbox.common.util.UrlUtils;
import com.sandbox.commonsecurity.CookieUtils;
import com.sandbox.commonsecurity.oauth.config.OAuthRedirectProperties;
import com.sandbox.commonsecurity.oauth.jwt.JwtProperties;
import com.sandbox.commonsecurity.oauth.jwt.JwtProvider;
import com.sandbox.commonsecurity.user.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OAuth2LoginTokenIssueAndRedirectSuccessHandler implements AuthenticationSuccessHandler {
	private final OAuthUserProvisioningService oAuthUserProvisioningService;
	private final OAuthRedirectProperties redirectProperties;
	private final JwtProvider jwtProvider;
	private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	private final JwtProperties jwtProperties;

	@Value("${app.cookie.secure:true}")
	private boolean cookieSecure;
	@Value("${app.frontend-url:}")
	private String feDomain;


	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException {
		if (!(authentication instanceof OAuth2AuthenticationToken oAuth2AuthenticationToken)) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		if (!(oAuth2AuthenticationToken.getPrincipal() instanceof SocialAuth2User principal)) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		ProvisionMember provisioned = oAuthUserProvisioningService.provision(principal);
		Long memberId = provisioned.memberId();
		List<String> userRoles = provisioned.userRoles()
			.stream()
			.map(UserRole::getAuthorityName)
			.toList();

		String accessToken = jwtProvider.createAccessToken(String.valueOf(memberId), userRoles);
		String refreshToken = jwtProvider.createRefreshToken(String.valueOf(memberId));
		String host = UrlUtils.extractHost(feDomain);
		// Refresh Token -> HttpOnly 쿠키로 설정 (local(http)에서는 secure=false 필요)
		ResponseCookie refreshCookie = CookieUtils.createRefreshTokenCookie(refreshToken, cookieSecure, host, jwtProperties.refreshTokenTtlSeconds());
		CookieUtils.addCookie(response, refreshCookie);

		String redirectUrl = redirectProperties.resolveRedirectUrl()
			+ "?accessToken=" + accessToken;
		redirectStrategy.sendRedirect(request, response, redirectUrl);
	}
}
