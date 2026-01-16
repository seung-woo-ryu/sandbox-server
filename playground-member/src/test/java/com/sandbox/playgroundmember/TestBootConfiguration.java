package com.sandbox.playgroundmember;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * playground-member는 실행 모듈이 아니므로 @SpringBootApplication이 없다.
 * @SpringBootTest가 컨텍스트를 만들 수 있도록 테스트 전용 부트 설정을 둔다.
 */
@SpringBootApplication(scanBasePackages = "com.sandbox.playgroundmember")
public class TestBootConfiguration {
}

