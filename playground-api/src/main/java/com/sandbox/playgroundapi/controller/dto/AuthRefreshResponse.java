package com.sandbox.playgroundapi.controller.dto;

public record AuthRefreshResponse(
	String accessToken,
	String tokenType
) {
	public static AuthRefreshResponse bearer(String accessToken) {
		return new AuthRefreshResponse(accessToken, "Bearer");
	}
}

