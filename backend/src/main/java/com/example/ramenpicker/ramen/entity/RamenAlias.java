package com.example.ramenpicker.ramen.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * 라면 별칭 엔티티. 검색 편의를 위한 별칭 저장.
 */
@Entity
@Table(name = "ramen_alias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RamenAlias {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ramen_item_id", nullable = false)
    private Long ramenItemId;

    @Column(nullable = false, length = 200)
    private String alias;
}
