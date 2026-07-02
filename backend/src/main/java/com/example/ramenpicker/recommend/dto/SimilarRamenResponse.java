package com.example.ramenpicker.recommend.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 비슷한 라면 조회 응답 DTO.
 */
@Getter
@Builder
public class SimilarRamenResponse {

    @Getter
    @Builder
    public static class BaseItem {
        private Long id;
        private String name;
    }

    @Getter
    @Builder
    public static class SimilarItem {
        private Long id;
        private String name;
        private String brand;
        private int similarityScore;
        private String reason;
        private String cookTypeLabel;
        private String ramenTypeLabel;
        private String spicyLabel;
    }

    private BaseItem baseItem;
    private List<SimilarItem> similarItems;
}
