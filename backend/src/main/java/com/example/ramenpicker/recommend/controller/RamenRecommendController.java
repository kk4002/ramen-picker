package com.example.ramenpicker.recommend.controller;

import com.example.ramenpicker.recommend.dto.RecommendRequest;
import com.example.ramenpicker.recommend.dto.RecommendResponse;
import com.example.ramenpicker.recommend.service.RamenRecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 라면 추천 API.
 */
@RestController
@RequestMapping("/api/ramen")
@RequiredArgsConstructor
public class RamenRecommendController {

    private final RamenRecommendService recommendService;

    /** 조건 기반 추천 */
    @PostMapping("/recommend")
    public RecommendResponse recommend(@RequestBody RecommendRequest request) {
        return recommendService.recommend(request);
    }

    /** 빠른 추천 (프리셋) */
    @GetMapping("/quick-recommend")
    public RecommendResponse quickRecommend(@RequestParam String type) {
        return recommendService.quickRecommend(type);
    }
}
