package com.sandbox.common.util;

import lombok.experimental.UtilityClass;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * URL/URI 문자열을 안전하게 파싱하기 위한 유틸.
 * <p>
 * 쿠키 domain에는 스킴/포트가 포함되면 안 되므로(예: http://, :8080) host만 추출할 때 사용한다.
 */
@UtilityClass
public class UrlUtils {

	/**
	 * 입력 문자열에서 host만 추출한다.
	 * <p>
	 * 지원 예:
	 * <ul>
	 *   <li>http://localhost:5173 -> localhost</li>
	 *   <li>https://api.example.com -> api.example.com</li>
	 *   <li>localhost:8080 -> localhost</li>
	 *   <li>localhost -> localhost</li>
	 * </ul>
	 *
	 * @param value URL/호스트/호스트:포트 문자열
	 * @return host (추출 불가 시 null)
	 */
	public static String extractHost(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		if (trimmed.isEmpty()) {
			return null;
		}

		// 1) URI로 먼저 시도 (스킴이 있는 경우)
		try {
			URI uri = new URI(trimmed);
			String host = uri.getHost();
			if (host != null && !host.isBlank()) {
				return host;
			}
		} catch (URISyntaxException ignored) {
			// fall through
		}

		// 2) 스킴이 없는 host:port 케이스는 임의로 스킴을 붙여서 파싱
		try {
			URI uri = new URI("http://" + trimmed);
			String host = uri.getHost();
			if (host != null && !host.isBlank()) {
				return host;
			}
		} catch (URISyntaxException ignored) {
			// fall through
		}

		return null;
	}
}

