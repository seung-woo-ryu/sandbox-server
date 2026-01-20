package com.sandbox.playgroundmember.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * 형용사 + 명사 조합의 랜덤 닉네임 생성기.
 */
@Component
public class RandomKoreanAdjectiveNounNicknameGenerator implements NicknameGenerator {

    private static final String[] ADJECTIVES = {
            "차분한", "용감한", "즐거운", "똑똑한", "느긋한", "성실한", "상냥한", "신중한", "유쾌한", "조용한",
            "빛나는", "단단한", "빠른", "느린", "따뜻한", "차가운", "멋진", "귀여운", "강한", "부드러운"
    };

    private static final String[] NOUNS = {
            "자동차", "고래", "사자", "호랑이", "고양이", "강아지", "독수리", "거북이", "여우", "펭귄",
            "로봇", "개발자", "탐험가", "기사", "마법사", "요리사", "작가", "화가", "기차", "별"
    };

    private static final int DEFAULT_SUFFIX_BOUND = 10_000; // 0~9999

    private final SecureRandom random = new SecureRandom();

    @Override
    public String generate() {
        String adj = ADJECTIVES[random.nextInt(ADJECTIVES.length)];
        String noun = NOUNS[random.nextInt(NOUNS.length)];
        int suffix = random.nextInt(DEFAULT_SUFFIX_BOUND);
        return adj + " " + noun + String.format("%04d", suffix);
    }
}

