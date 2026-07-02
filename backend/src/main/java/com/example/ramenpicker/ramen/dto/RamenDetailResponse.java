package com.example.ramenpicker.ramen.dto;

import com.example.ramenpicker.ramen.entity.RamenItem;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 라면 상세/목록 응답 DTO.
 */
@Getter
@Builder
public class RamenDetailResponse {

    private Long id;
    private String name;
    private String brand;
    private String ramenType;
    private String ramenTypeLabel;
    private String cookType;
    private String cookTypeLabel;
    private Integer spicyLevel;
    private String spicyLabel;
    private List<String> flavorTags;
    private List<String> situationTags;
    private List<String> purchaseTags;
    private Integer cookTimeMinutes;
    private String description;
    private String imageUrl;

    public static RamenDetailResponse from(RamenItem item) {
        return RamenDetailResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .brand(item.getBrand())
                .ramenType(item.getRamenType().name())
                .ramenTypeLabel(item.getRamenType().getLabel())
                .cookType(item.getCookType().name())
                .cookTypeLabel(item.getCookType().getLabel())
                .spicyLevel(item.getSpicyLevel())
                .spicyLabel(item.getSpicyLabel())
                .flavorTags(item.getFlavorTags())
                .situationTags(item.getSituationTags())
                .purchaseTags(item.getPurchaseTags())
                .cookTimeMinutes(item.getCookTimeMinutes())
                .description(item.getDescription())
                .imageUrl(item.getImageUrl())
                .build();
    }
}
