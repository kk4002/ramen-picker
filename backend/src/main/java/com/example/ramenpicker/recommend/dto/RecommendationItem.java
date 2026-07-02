package com.example.ramenpicker.recommend.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 추천 결과 항목 DTO.
 */
@Getter
@Builder
public class RecommendationItem {

    private Long id;
    private int rank;
    private String name;
    private String brand;
    /** 0~100 환산 적합도 */
    private int matchScore;
    /** 원점수 (룰 기반 합산) */
    private int rawScore;
    private String reason;
    private String cookType;
    private String cookTypeLabel;
    private String ramenType;
    private String ramenTypeLabel;
    private Integer spicyLevel;
    private String spicyLabel;
    private List<String> tags;
    private List<String> flavorTags;
    private List<String> situationTags;
    private List<String> purchaseTags;
    private Integer cookTimeMinutes;
    /** 뽀글이 모드 등에서 노출할 주의 문구 */
    private String warning;
}
