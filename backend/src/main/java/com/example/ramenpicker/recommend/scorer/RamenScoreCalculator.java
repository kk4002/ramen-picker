package com.example.ramenpicker.recommend.scorer;

import com.example.ramenpicker.common.CookType;
import com.example.ramenpicker.common.RamenType;
import com.example.ramenpicker.common.SpicyPreference;
import com.example.ramenpicker.ramen.entity.RamenItem;
import com.example.ramenpicker.recommend.dto.RecommendRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 룰 기반 추천 점수 계산기.
 *
 * 배점:
 *   조리 방식 일치      +40
 *   라면 타입 일치      +25
 *   상황 태그 일치      +20
 *   맛 태그 일치        태그당 +10, 최대 +30
 *   매운 정도 근접      범위 내 +15 / 1차이 +8 / 2이상 +0
 *   구매처 일치         +10
 *   조리 시간 조건 일치 +5
 *
 * matchScore(%)는 "요청에 실제로 지정된 조건의 만점 합" 대비 획득 점수로 환산한다.
 * (조건을 적게 걸수록 만점 기준도 낮아져 적합도가 과소평가되지 않음)
 */
@Component
public class RamenScoreCalculator {

    public static final int SCORE_COOK_TYPE = 40;
    public static final int SCORE_RAMEN_TYPE = 25;
    public static final int SCORE_SITUATION = 20;
    public static final int SCORE_FLAVOR_PER_TAG = 10;
    public static final int SCORE_FLAVOR_MAX = 30;
    public static final int SCORE_SPICY_IN_RANGE = 15;
    public static final int SCORE_SPICY_CLOSE = 8;
    public static final int SCORE_PURCHASE = 10;
    public static final int SCORE_COOK_TIME = 5;

    public ScoreBreakdown score(RecommendRequest req, RamenItem item) {
        int raw = 0;
        int maxPossible = 0;

        // 1) 조리 방식
        boolean cookTypeMatched = false;
        if (isSpecified(req.getCookType())) {
            maxPossible += SCORE_COOK_TYPE;
            if (isCookTypeMatch(req.getCookType(), item.getCookType())) {
                raw += SCORE_COOK_TYPE;
                cookTypeMatched = true;
            }
        }

        // 2) 라면 타입
        boolean ramenTypeMatched = false;
        if (isSpecified(req.getRamenType())) {
            maxPossible += SCORE_RAMEN_TYPE;
            if (req.getRamenType() == item.getRamenType()) {
                raw += SCORE_RAMEN_TYPE;
                ramenTypeMatched = true;
            }
        }

        // 3) 상황 태그
        boolean situationMatched = false;
        if (hasText(req.getSituation())) {
            maxPossible += SCORE_SITUATION;
            if (containsTag(item.getSituationTags(), req.getSituation())) {
                raw += SCORE_SITUATION;
                situationMatched = true;
            }
        }

        // 4) 맛 태그 (복수)
        List<String> matchedFlavors = new ArrayList<>();
        if (req.getFlavorTags() != null && !req.getFlavorTags().isEmpty()) {
            maxPossible += SCORE_FLAVOR_MAX;
            for (String wanted : req.getFlavorTags()) {
                if (containsTag(item.getFlavorTags(), wanted)) {
                    matchedFlavors.add(wanted);
                }
            }
            int flavorScore = Math.min(matchedFlavors.size() * SCORE_FLAVOR_PER_TAG, SCORE_FLAVOR_MAX);
            raw += flavorScore;
        }

        // 5) 매운 정도 근접
        boolean spicyMatched = false;
        boolean spicyClose = false;
        if (isSpecifiedSpicy(req.getSpicyPreference()) && item.getSpicyLevel() != null) {
            maxPossible += SCORE_SPICY_IN_RANGE;
            SpicyPreference pref = req.getSpicyPreference();
            int level = item.getSpicyLevel();
            if (level >= pref.getMinLevel() && level <= pref.getMaxLevel()) {
                raw += SCORE_SPICY_IN_RANGE;
                spicyMatched = true;
            } else {
                int distance = Math.min(
                        Math.abs(level - pref.getMinLevel()),
                        Math.abs(level - pref.getMaxLevel()));
                if (distance == 1) {
                    raw += SCORE_SPICY_CLOSE;
                    spicyClose = true;
                }
            }
        }

        // 6) 구매처
        boolean purchaseMatched = false;
        if (hasText(req.getPurchasePlace())) {
            maxPossible += SCORE_PURCHASE;
            if (containsTag(item.getPurchaseTags(), req.getPurchasePlace())) {
                raw += SCORE_PURCHASE;
                purchaseMatched = true;
            }
        }

        // 7) 조리 시간
        boolean cookTimeMatched = false;
        if (req.getMaxCookTime() != null && item.getCookTimeMinutes() != null) {
            maxPossible += SCORE_COOK_TIME;
            if (item.getCookTimeMinutes() <= req.getMaxCookTime()) {
                raw += SCORE_COOK_TIME;
                cookTimeMatched = true;
            }
        }

        int matchScore = maxPossible == 0 ? 0 : (int) Math.round(raw * 100.0 / maxPossible);

        return ScoreBreakdown.builder()
                .rawScore(raw)
                .matchScore(matchScore)
                .cookTypeMatched(cookTypeMatched)
                .ramenTypeMatched(ramenTypeMatched)
                .situationMatched(situationMatched)
                .matchedFlavorCount(matchedFlavors.size())
                .matchedFlavorTags(matchedFlavors)
                .spicyMatched(spicyMatched)
                .spicyClose(spicyClose)
                .purchaseMatched(purchaseMatched)
                .cookTimeMatched(cookTimeMatched)
                .build();
    }

    /**
     * 조리 방식 매칭 여부.
     * 뽀글이 요청은 뽀글이/봉지라면을 매칭으로 인정한다(봉지 형태가 뽀글이에 적합).
     */
    private boolean isCookTypeMatch(CookType want, CookType itemCookType) {
        if (want == itemCookType) {
            return true;
        }
        return want == CookType.PPOGEULI
                && (itemCookType == CookType.PPOGEULI || itemCookType == CookType.BAG);
    }

    private boolean isSpecified(CookType c) {
        return c != null && c != CookType.ANY;
    }

    private boolean isSpecified(RamenType r) {
        return r != null && r != RamenType.ANY;
    }

    private boolean isSpecifiedSpicy(SpicyPreference s) {
        return s != null && !s.isAny();
    }

    private boolean hasText(String s) {
        return s != null && !s.trim().isEmpty();
    }

    /**
     * 태그 목록에 원하는 태그가 포함되는지 (공백 무시, 부분 일치 허용).
     * 예: "편의점 한 끼" 저장값과 "편의점" 요청이 부분 매칭되도록 한다.
     */
    private boolean containsTag(List<String> tags, String wanted) {
        if (tags == null || wanted == null) {
            return false;
        }
        String w = normalize(wanted);
        if (w.isEmpty()) {
            return false;
        }
        for (String tag : tags) {
            String t = normalize(tag);
            if (t.equals(w) || t.contains(w) || w.contains(t)) {
                return true;
            }
        }
        return false;
    }

    private String normalize(String s) {
        return s == null ? "" : s.replaceAll("\\s+", "").toLowerCase();
    }
}
