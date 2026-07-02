package com.example.ramenpicker.ramen.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 추천 요청 로그 엔티티.
 */
@Entity
@Table(name = "ramen_recommend_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RamenRecommendLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cook_type", length = 50)
    private String cookType;

    @Column(name = "ramen_type", length = 50)
    private String ramenType;

    @Column(length = 50)
    private String situation;

    @Column(name = "flavor_tags", columnDefinition = "TEXT")
    private String flavorTags;

    @Column(name = "spicy_preference", length = 50)
    private String spicyPreference;

    @Column(name = "purchase_place", length = 50)
    private String purchasePlace;

    /** 추천된 라면 id 목록 (콤마 구분) */
    @Column(name = "result_item_ids", columnDefinition = "TEXT")
    private String resultItemIds;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
