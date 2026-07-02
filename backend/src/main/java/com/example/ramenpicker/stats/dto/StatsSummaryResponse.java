package com.example.ramenpicker.stats.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 지표 집계 응답 DTO.
 */
@Getter
@Builder
public class StatsSummaryResponse {

    private String serviceName;
    private long totalApiCalls;
    private long totalPageViews;

    /** 경로별 집계 (호출/방문 많은 순) */
    private List<PathCount> byPath;

    /** 일자별 집계 */
    private List<DayCount> byDay;

    @Getter
    @Builder
    public static class PathCount {
        private String kind;   // API | PAGE
        private String path;
        private long count;
    }

    @Getter
    @Builder
    public static class DayCount {
        private String day;    // yyyy-MM-dd
        private long apiCount;
        private long pageCount;
    }
}
