package com.example.ramenpicker.common;

/**
 * 매운 정도 선호.
 * 내부적으로는 spicyLevel(1~10) 범위로 변환하여 매칭한다.
 */
public enum SpicyPreference {
    NONE("안 매운맛", 1, 2),
    MILD("살짝 매운맛", 3, 4),
    SHIN_RAMEN_LEVEL("신라면급", 5, 6),
    BULDAK_LEVEL("불닭급", 7, 8),
    CHALLENGE("도전급", 9, 10),
    ANY("상관없음", 0, 0);

    private final String label;
    private final int minLevel;
    private final int maxLevel;

    SpicyPreference(String label, int minLevel, int maxLevel) {
        this.label = label;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
    }

    public String getLabel() {
        return label;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public boolean isAny() {
        return this == ANY;
    }
}
