package com.example.ramenpicker.recommend.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * 라면 추천 응답 DTO.
 */
@Getter
@Builder
public class RecommendResponse {

    /** 요청에 사용된 조건 요약 (프론트 표시용) */
    private Map<String, Object> conditions;

    private List<RecommendationItem> recommendations;

    /** 뽀글이 모드 등 전체 결과에 적용되는 안내 문구 */
    private String notice;
}
