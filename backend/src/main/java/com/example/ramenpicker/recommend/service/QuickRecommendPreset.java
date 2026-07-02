package com.example.ramenpicker.recommend.service;

import com.example.ramenpicker.common.CookType;
import com.example.ramenpicker.common.RamenType;
import com.example.ramenpicker.common.SpicyPreference;
import com.example.ramenpicker.recommend.dto.RecommendRequest;

import java.util.Arrays;

/**
 * 빠른 추천 프리셋.
 * 메인 화면 빠른 추천 카드가 호출하는 미리 정의된 조건들.
 */
public final class QuickRecommendPreset {

    private QuickRecommendPreset() {
    }

    public static RecommendRequest build(String type) {
        RecommendRequest req = new RecommendRequest();
        req.setLimit(5);
        if (type == null) {
            return req;
        }
        switch (type.toLowerCase()) {
            case "hangover": // 해장 라면
                req.setRamenType(RamenType.SOUP);
                req.setSituation("해장");
                req.setFlavorTags(Arrays.asList("얼큰", "깔끔"));
                break;
            case "night_snack": // 야식 라면
                req.setSituation("야식");
                req.setFlavorTags(Arrays.asList("얼큰", "진한국물"));
                break;
            case "convenience_cup": // 편의점 컵라면
                req.setCookType(CookType.CUP);
                req.setPurchasePlace("편의점");
                req.setSituation("편의점 한 끼");
                break;
            case "ppogeuli": // 뽀글이 추천
                req.setCookType(CookType.PPOGEULI);
                break;
            case "mild": // 안 매운 라면
                req.setSpicyPreference(SpicyPreference.NONE);
                req.setFlavorTags(Arrays.asList("깔끔", "담백"));
                break;
            case "spicy_soup": // 매운 국물라면
                req.setRamenType(RamenType.SOUP);
                req.setSpicyPreference(SpicyPreference.BULDAK_LEVEL);
                req.setFlavorTags(Arrays.asList("얼큰"));
                break;
            case "stir_fried": // 볶음면
                req.setRamenType(RamenType.STIR_FRIED);
                break;
            case "heavy_meal": // 든든한 라면
                req.setSituation("든든함");
                req.setFlavorTags(Arrays.asList("진한국물"));
                break;
            default:
                // 알 수 없는 타입은 조건 없이 전체 추천
                break;
        }
        return req;
    }
}
