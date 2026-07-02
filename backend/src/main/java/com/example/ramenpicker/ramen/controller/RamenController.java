package com.example.ramenpicker.ramen.controller;

import com.example.ramenpicker.common.CookType;
import com.example.ramenpicker.common.RamenType;
import com.example.ramenpicker.ramen.dto.RamenDetailResponse;
import com.example.ramenpicker.ramen.service.RamenService;
import com.example.ramenpicker.recommend.dto.SimilarRamenResponse;
import com.example.ramenpicker.recommend.service.SimilarRamenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 라면 목록/상세/검색/비슷한 라면 API.
 */
@RestController
@RequestMapping("/api/ramen")
@RequiredArgsConstructor
public class RamenController {

    private final RamenService ramenService;
    private final SimilarRamenService similarRamenService;

    /** 라면 목록/검색 */
    @GetMapping
    public List<RamenDetailResponse> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) CookType cookType,
            @RequestParam(required = false) RamenType ramenType,
            @RequestParam(required = false) Integer spicyLevel,
            @RequestParam(required = false) String flavorTag,
            @RequestParam(required = false) String situationTag,
            @RequestParam(required = false) String purchaseTag) {
        return ramenService.list(keyword, cookType, ramenType, spicyLevel, flavorTag, situationTag, purchaseTag);
    }

    /** 라면 상세 */
    @GetMapping("/{id}")
    public RamenDetailResponse detail(@PathVariable Long id) {
        return ramenService.getDetail(id);
    }

    /** 비슷한 라면 */
    @GetMapping("/{id}/similar")
    public SimilarRamenResponse similar(@PathVariable Long id,
                                        @RequestParam(required = false, defaultValue = "5") int limit) {
        return similarRamenService.findSimilar(id, limit);
    }
}
