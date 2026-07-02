package com.example.ramenpicker.ramen.entity;

import com.example.ramenpicker.common.CookType;
import com.example.ramenpicker.common.RamenType;
import com.example.ramenpicker.common.StringListConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 라면 아이템 엔티티.
 * 태그(맛/상황/구매처)는 콤마 구분 TEXT로 저장한다.
 */
@Entity
@Table(name = "ramen_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RamenItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 100)
    private String brand;

    @Enumerated(EnumType.STRING)
    @Column(name = "ramen_type", nullable = false, length = 50)
    private RamenType ramenType;

    @Enumerated(EnumType.STRING)
    @Column(name = "cook_type", nullable = false, length = 50)
    private CookType cookType;

    /** 1~10 */
    @Column(name = "spicy_level")
    private Integer spicyLevel;

    @Column(name = "spicy_label", length = 50)
    private String spicyLabel;

    @Convert(converter = StringListConverter.class)
    @Column(name = "flavor_tags", columnDefinition = "TEXT")
    @Builder.Default
    private List<String> flavorTags = new ArrayList<>();

    @Convert(converter = StringListConverter.class)
    @Column(name = "situation_tags", columnDefinition = "TEXT")
    @Builder.Default
    private List<String> situationTags = new ArrayList<>();

    @Convert(converter = StringListConverter.class)
    @Column(name = "purchase_tags", columnDefinition = "TEXT")
    @Builder.Default
    private List<String> purchaseTags = new ArrayList<>();

    @Column(name = "cook_time_minutes")
    private Integer cookTimeMinutes;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
