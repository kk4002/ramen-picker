package com.example.ramenpicker.recommend.scorer;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 점수 계산 상세 결과.
 * 추천 이유 생성기가 어떤 조건이 맞았는지 참조한다.
 */
@Getter
@Builder
public class ScoreBreakdown {

    private int rawScore;
    /** 조건 대비 만점 환산 0~100 */
    private int matchScore;

    private boolean cookTypeMatched;
    private boolean ramenTypeMatched;
    private boolean situationMatched;
    private int matchedFlavorCount;
    private boolean spicyMatched;
    private boolean spicyClose;
    private boolean purchaseMatched;
    private boolean cookTimeMatched;

    @Builder.Default
    private List<String> matchedFlavorTags = new ArrayList<>();
}
