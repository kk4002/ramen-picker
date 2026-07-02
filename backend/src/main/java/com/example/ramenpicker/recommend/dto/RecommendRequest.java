package com.example.ramenpicker.recommend.dto;

import com.example.ramenpicker.common.CookType;
import com.example.ramenpicker.common.RamenType;
import com.example.ramenpicker.common.SpicyPreference;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 라면 추천 요청 DTO.
 * 모든 조건은 선택적이며, 미지정(null) 또는 ANY 는 해당 조건을 점수에서 제외한다.
 */
@Getter
@Setter
public class RecommendRequest {

    private CookType cookType;
    private RamenType ramenType;

    /** 상황 태그 (예: 해장, 야식). 자유 문자열 */
    private String situation;

    /** 맛 취향 복수 선택 */
    private List<String> flavorTags = new ArrayList<>();

    private SpicyPreference spicyPreference;

    /** 구매처 (예: 편의점, 마트, 온라인) */
    private String purchasePlace;

    /** 조리 시간 상한(분). null 이면 미고려 */
    private Integer maxCookTime;

    /** 반환 개수. 기본 3 */
    private Integer limit;

    public int resolveLimit() {
        if (limit == null || limit <= 0) {
            return 3;
        }
        return Math.min(limit, 20);
    }
}
