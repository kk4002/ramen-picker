package com.example.ramenpicker.stats.controller;

import com.example.ramenpicker.stats.StatKind;
import com.example.ramenpicker.stats.dto.PageViewRequest;
import com.example.ramenpicker.stats.dto.StatsSummaryResponse;
import com.example.ramenpicker.stats.service.TrafficService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 지표(트래픽) 수집/조회 API.
 */
@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final TrafficService trafficService;

    /** 프론트 페이지 방문 기록 */
    @PostMapping("/page-view")
    public ResponseEntity<Void> pageView(@RequestBody PageViewRequest request) {
        String path = request.getPath() == null || request.getPath().isEmpty() ? "/" : request.getPath();
        trafficService.record(StatKind.PAGE, path, null);
        return ResponseEntity.noContent().build();
    }

    /** 지표 집계 조회 */
    @GetMapping("/summary")
    public StatsSummaryResponse summary(
            @RequestParam(required = false, defaultValue = "14") int days,
            @RequestParam(required = false, defaultValue = "20") int topPaths) {
        return trafficService.summary(days, topPaths);
    }
}
