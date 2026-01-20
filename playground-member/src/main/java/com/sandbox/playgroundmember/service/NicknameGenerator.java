package com.sandbox.playgroundmember.service;

/**
 * 닉네임 자동 생성기.
 */
public interface NicknameGenerator {

    /**
     * 랜덤 닉네임을 생성합니다.
     *
     * @return 예: "차분한 자동차"
     */
    String generate();
}

