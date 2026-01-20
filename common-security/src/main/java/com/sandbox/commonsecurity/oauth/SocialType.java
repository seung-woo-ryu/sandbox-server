package com.sandbox.commonsecurity.oauth;


import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SocialType {
    KAKAO("kakao"),
    NAVER("naver");

    private final String providerName;
    public static SocialType from(String type) {
        for (SocialType socialType : SocialType.values()) {
            if (socialType.providerName.equalsIgnoreCase(type)) {
                return socialType;
            }
        }
        throw new IllegalArgumentException("Unknown social type: " + type);
    }
}


