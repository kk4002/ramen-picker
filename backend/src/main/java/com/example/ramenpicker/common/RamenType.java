package com.example.ramenpicker.common;

/**
 * 라면 타입.
 * ANY 는 사용자 필터(상관없음)에서만 사용하며, 실제 라면 데이터에는 구체 타입만 부여한다.
 */
public enum RamenType {
    SOUP("국물라면"),
    STIR_FRIED("볶음면"),
    BIBIM("비빔면"),
    JJAJANG("짜장라면"),
    JJAMPPONG("짬뽕라면"),
    UDON("우동/칼국수류"),
    CREAM("마라/탄탄/크림류"),
    ETC("기타"),
    ANY("상관없음");

    private final String label;

    RamenType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
