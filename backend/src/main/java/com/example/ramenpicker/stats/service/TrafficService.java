package com.example.ramenpicker.stats.service;

import com.example.ramenpicker.stats.StatKind;
import com.example.ramenpicker.stats.dto.StatsSummaryResponse;
import com.example.ramenpicker.stats.entity.TrafficLog;
import com.example.ramenpicker.stats.repository.TrafficLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 트래픽 로그 적층/집계 서비스.
 * 기록은 best-effort 로, 실패해도 본 요청 흐름에 영향을 주지 않는다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TrafficService {

    private final TrafficLogRepository repository;

    @Value("${app.service-name:ramen-picker}")
    private String serviceName;

    private static final DateTimeFormatter DAY_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 트래픽 1건 기록. 예외는 삼켜서 서비스 동작을 막지 않는다.
     */
    @Transactional
    public void record(StatKind kind, String path, String method) {
        try {
            LocalDateTime now = LocalDateTime.now();
            TrafficLog logEntry = TrafficLog.builder()
                    .serviceName(serviceName)
                    .kind(kind)
                    .path(path)
                    .method(method)
                    .day(now.toLocalDate())
                    .occurredAt(now)
                    .build();
            repository.save(logEntry);
        } catch (Exception e) {
            log.warn("트래픽 기록 실패 ({} {}): {}", kind, path, e.getMessage());
        }
    }

    /**
     * 지표 집계. 최근 daysWindow 일간의 일자별 추이 포함.
     */
    @Transactional(readOnly = true)
    public StatsSummaryResponse summary(int daysWindow, int topPaths) {
        long api = repository.countByKind(StatKind.API);
        long page = repository.countByKind(StatKind.PAGE);

        // 경로별 집계 (상위 topPaths 개)
        List<StatsSummaryResponse.PathCount> byPath = new ArrayList<>();
        for (Object[] row : repository.countByKindAndPath()) {
            byPath.add(StatsSummaryResponse.PathCount.builder()
                    .kind(((StatKind) row[0]).name())
                    .path((String) row[1])
                    .count(((Number) row[2]).longValue())
                    .build());
            if (byPath.size() >= topPaths) {
                break;
            }
        }

        // 일자별 집계 (api/page 병합)
        LocalDate from = LocalDate.now().minusDays(daysWindow - 1L);
        Map<String, long[]> dayMap = new LinkedHashMap<>();
        // 창(window) 내 모든 날짜를 0으로 초기화해 빈 날도 표시
        for (int i = 0; i < daysWindow; i++) {
            dayMap.put(from.plusDays(i).format(DAY_FMT), new long[]{0, 0});
        }
        for (Object[] row : repository.countByDay(from)) {
            String d = ((LocalDate) row[0]).format(DAY_FMT);
            StatKind k = (StatKind) row[1];
            long c = ((Number) row[2]).longValue();
            long[] arr = dayMap.computeIfAbsent(d, x -> new long[]{0, 0});
            if (k == StatKind.API) {
                arr[0] = c;
            } else {
                arr[1] = c;
            }
        }
        List<StatsSummaryResponse.DayCount> byDay = new ArrayList<>();
        dayMap.forEach((d, arr) -> byDay.add(StatsSummaryResponse.DayCount.builder()
                .day(d).apiCount(arr[0]).pageCount(arr[1]).build()));

        return StatsSummaryResponse.builder()
                .serviceName(serviceName)
                .totalApiCalls(api)
                .totalPageViews(page)
                .byPath(byPath)
                .byDay(byDay)
                .build();
    }
}
