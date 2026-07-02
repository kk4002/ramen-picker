package com.example.ramenpicker.ramen.repository;

import com.example.ramenpicker.common.CookType;
import com.example.ramenpicker.common.RamenType;
import com.example.ramenpicker.ramen.entity.RamenItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RamenItemRepository extends JpaRepository<RamenItem, Long> {

    List<RamenItem> findByCookType(CookType cookType);

    List<RamenItem> findByRamenType(RamenType ramenType);

    /**
     * 키워드로 이름/브랜드 검색 (대소문자 무시).
     */
    @Query("SELECT r FROM RamenItem r " +
            "WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(r.brand) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<RamenItem> searchByKeyword(@Param("keyword") String keyword);
}
