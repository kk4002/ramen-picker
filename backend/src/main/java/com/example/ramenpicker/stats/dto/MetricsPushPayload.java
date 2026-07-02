package com.example.ramenpicker.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 중앙 허브로 보내는 배치 push 페이로드.
 * 특정 일자의 경로별 집계치만 전송한다(원본 로그는 서비스에 잔류).
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricsPushPayload {

    private String serviceName;
    private String day;              // yyyy-MM-dd
    private long totalApiCalls;      // 해당 일자 API 합계
    private long totalPageViews;     // 해당 일자 PAGE 합계
    private List<Item> items;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        private String kind;   // API | PAGE
        private String path;
        private long count;
    }
}
