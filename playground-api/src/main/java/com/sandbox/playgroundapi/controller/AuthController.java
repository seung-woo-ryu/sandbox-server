package com.sandbox.playgroundapi.controller;

import com.sandbox.playgroundapi.controller.dto.AuthLoginRequest;
import com.sandbox.playgroundapi.controller.dto.AuthRefreshRequest;
import com.sandbox.playgroundapi.controller.dto.AuthRefreshResponse;
import com.sandbox.playgroundapi.controller.dto.AuthTokenResponse;
import com.sandbox.playgroundsecurity.JwtProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final JwtProvider jwtProvider;

	public AuthController(JwtProvider jwtProvider) {
		this.jwtProvider = jwtProvider;
	}

	@PostMapping("/login")
	public ResponseEntity<AuthTokenResponse> login(@RequestBody AuthLoginRequest request) {
		if (request == null || request.userId() == null || request.userId().isBlank()) {
			return ResponseEntity.badRequest().build();
		}
		String role = (request.role() == null || request.role().isBlank()) ? "USER" : request.role();

		String accessToken = jwtProvider.createAccessToken(request.userId(), role);
		String refreshToken = jwtProvider.createRefreshToken(request.userId());
		return ResponseEntity.ok(AuthTokenResponse.bearer(accessToken, refreshToken));
	}

	@PostMapping("/refresh")
	public ResponseEntity<AuthRefreshResponse> refresh(@RequestBody AuthRefreshRequest request) {
		if (request == null || request.refreshToken() == null || request.refreshToken().isBlank()) {
			return ResponseEntity.badRequest().build();
		}

		if (!jwtProvider.isValid(request.refreshToken()) || !jwtProvider.isRefreshToken(request.refreshToken())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		String userId = jwtProvider.getUserId(request.refreshToken());
		// refresh 토큰에는 role이 없으므로, 여기서는 단순 기본값을 사용(실서비스는 DB/Redis에서 role 조회 권장)
		String newAccessToken = jwtProvider.createAccessToken(userId, "USER");
		return ResponseEntity.ok(AuthRefreshResponse.bearer(newAccessToken));
	}
}
