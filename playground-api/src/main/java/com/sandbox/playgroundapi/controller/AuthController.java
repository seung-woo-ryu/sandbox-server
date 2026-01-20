package com.sandbox.playgroundapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/auth")
public class AuthController {
	/**
	 * 일반적인 패턴: 프론트는 "보안 프레임워크 기본 URL"을 몰라도 되게,
	 * 앱 전용 엔드포인트(/auth/oauth2/kakao)로만 로그인 시작을 트리거하고,
	 * 서버가 Spring Security의 표준 엔드포인트(/oauth2/authorization/{registrationId})로 리다이렉트한다.
	 *
	 * 주의: axios/fetch(XHR)로 호출하면 브라우저 화면 이동이 자동으로 되지 않으므로,
	 * 이 엔드포인트는 window.location / <a href> 같은 "네비게이션"으로 호출해야 한다.
	 */
	@GetMapping("/oauth2/kakao")
	public ResponseEntity<Void> oauth2Kakao() {
		return ResponseEntity.status(HttpStatus.FOUND)
			.location(URI.create("/oauth2/authorization/kakao"))
			.build();
	}
}
