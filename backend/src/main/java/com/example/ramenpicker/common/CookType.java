package com.example.ramenpicker.common;

/**
 * 조리 방식.
 * ANY 는 사용자 필터(상관없음)에서만 사용한다.
 */
public enum CookType {
    CUP("컵라면"),
    BAG("봉지라면"),
    PPOGEULI("뽀글이"),
    POT("냄비 조리"),
    MICROWAVE("전자레인지 조리"),
    ANY("상관없음");

    private final String label;

    CookType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
