package com.sandbox.commonsecurity;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class JwtProviderTest {

	private static JwtProvider newProvider() {
		return new JwtProvider(
			"test-secret-1234567890",
			"sandbox",
			60,  // access ttl
			120  // refresh ttl
		);
	}

	@Test
	void createAccessToken_containsSubjectTypeAndRole() {
		JwtProvider provider = newProvider();
		String token = provider.createAccessToken("user-1", "ADMIN");

		assertTrue(provider.isValid(token));
		assertEquals("user-1", provider.getUserId(token));
		assertTrue(provider.isAccessToken(token));
		assertFalse(provider.isRefreshToken(token));
		assertEquals("ADMIN", provider.getRole(token).orElse(null));

		long exp = provider.getExpiresAtEpochSeconds(token);
		assertTrue(exp > 0);
		assertTrue(exp > Instant.now().getEpochSecond());
	}

	@Test
	void createRefreshToken_containsSubjectAndRefreshType_andHasNoRole() {
		JwtProvider provider = newProvider();
		String token = provider.createRefreshToken("user-1");

		assertTrue(provider.isValid(token));
		assertEquals("user-1", provider.getUserId(token));
		assertTrue(provider.isRefreshToken(token));
		assertFalse(provider.isAccessToken(token));
		assertTrue(provider.getRole(token).isEmpty());
	}

	@Test
	void verify_throwsForBlankToken() {
		JwtProvider provider = newProvider();

		assertThrows(IllegalArgumentException.class, () -> provider.verify(""));
		assertThrows(IllegalArgumentException.class, () -> provider.verify("   "));
		assertThrows(IllegalArgumentException.class, () -> provider.verify(null));
	}

	@Test
	void isValid_returnsFalseForTamperedToken() {
		JwtProvider provider = newProvider();
		String token = provider.createAccessToken("user-1", "USER");

		String tampered = token.substring(0, token.length() - 1) + (token.endsWith("a") ? "b" : "a");
		assertFalse(provider.isValid(tampered));
		assertThrows(JWTVerificationException.class, () -> provider.verify(tampered));
	}

	@Test
	void isValid_returnsFalseForWrongIssuer() {
		JwtProvider provider1 = newProvider();
		JwtProvider provider2 = new JwtProvider("test-secret-1234567890", "other-issuer", 60, 120);

		String token = provider1.createAccessToken("user-1", "USER");
		assertFalse(provider2.isValid(token));
		assertThrows(JWTVerificationException.class, () -> provider2.verify(token));
	}
}

