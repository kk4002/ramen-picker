package com.example.ramenpicker.ramen.dto;

import com.example.ramenpicker.common.CookType;
import com.example.ramenpicker.common.RamenType;
import com.example.ramenpicker.ramen.entity.RamenItem;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * 관리자용 라면 등록/수정 요청 DTO.
 */
@Getter
@Setter
public class RamenUpsertRequest {

    @NotBlank(message = "라면명은 필수입니다.")
    private String name;

    private String brand;

    @NotNull(message = "라면 타입은 필수입니다.")
    private RamenType ramenType;

    @NotNull(message = "조리 방식은 필수입니다.")
    private CookType cookType;

    @Min(value = 1, message = "매운 정도는 1~10 사이여야 합니다.")
    @Max(value = 10, message = "매운 정도는 1~10 사이여야 합니다.")
    private Integer spicyLevel;

    private String spicyLabel;
    private List<String> flavorTags = new ArrayList<>();
    private List<String> situationTags = new ArrayList<>();
    private List<String> purchaseTags = new ArrayList<>();
    private Integer cookTimeMinutes;
    private String description;
    private String imageUrl;

    /** 신규 엔티티 생성 */
    public RamenItem toEntity() {
        return RamenItem.builder()
                .name(name)
                .brand(brand)
                .ramenType(ramenType)
                .cookType(cookType)
                .spicyLevel(spicyLevel)
                .spicyLabel(spicyLabel)
                .flavorTags(flavorTags == null ? new ArrayList<>() : flavorTags)
                .situationTags(situationTags == null ? new ArrayList<>() : situationTags)
                .purchaseTags(purchaseTags == null ? new ArrayList<>() : purchaseTags)
                .cookTimeMinutes(cookTimeMinutes)
                .description(description)
                .imageUrl(imageUrl)
                .build();
    }

    /** 기존 엔티티에 값 반영 */
    public void applyTo(RamenItem item) {
        item.setName(name);
        item.setBrand(brand);
        item.setRamenType(ramenType);
        item.setCookType(cookType);
        item.setSpicyLevel(spicyLevel);
        item.setSpicyLabel(spicyLabel);
        item.setFlavorTags(flavorTags == null ? new ArrayList<>() : flavorTags);
        item.setSituationTags(situationTags == null ? new ArrayList<>() : situationTags);
        item.setPurchaseTags(purchaseTags == null ? new ArrayList<>() : purchaseTags);
        item.setCookTimeMinutes(cookTimeMinutes);
        item.setDescription(description);
        item.setImageUrl(imageUrl);
    }
}
