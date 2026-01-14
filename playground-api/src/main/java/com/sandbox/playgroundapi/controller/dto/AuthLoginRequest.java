package com.sandbox.playgroundapi.controller.dto;

public record AuthLoginRequest(
	String userId,
	String role
) {
}

