package com.example.ramenpicker.stats.service;

import com.example.ramenpicker.stats.StatKind;
import com.example.ramenpicker.stats.dto.MetricsPushPayload;
import com.example.ramenpicker.stats.repository.TrafficLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 중앙 지표 허브로 오늘자 집계치를 주기적으로 push 하는 배치 작업.
 *
 * 원칙: best-effort ("없으면 말고").
 * - enabled=false 이거나 url 이 비어 있으면 아무 것도 하지 않는다.
 * - 허브가 죽어있어도 예외를 삼켜 서비스 동작에 영향을 주지 않는다.
 * - 원본 로그는 각 서비스 DB 에 그대로 남으므로 나중에 이관/재전송 가능.
 */
@Slf4j
@Component
public class MetricsPushJob {

    private final TrafficLogRepository repository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.service-name:ramen-picker}")
    private String serviceName;

    @Value("${app.metrics-hub.url:}")
    private String hubUrl;

    @Value("${app.metrics-hub.enabled:false}")
    private boolean enabled;

    private static final DateTimeFormatter DAY_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public MetricsPushJob(TrafficLogRepository repository) {
        this.repository = repository;
    }

    @Scheduled(cron = "${app.metrics-hub.cron:0 */10 * * * *}")
    public void push() {
        // 비활성/미설정이면 조용히 skip
        if (!enabled || hubUrl == null || hubUrl.trim().isEmpty()) {
            return;
        }
        try {
            LocalDate today = LocalDate.now();
            List<MetricsPushPayload.Item> items = new ArrayList<>();
            long api = 0;
            long page = 0;
            for (Object[] row : repository.aggregateForDay(today)) {
                StatKind kind = (StatKind) row[1];
                long count = ((Number) row[3]).longValue();
                items.add(MetricsPushPayload.Item.builder()
                        .kind(kind.name())
                        .path((String) row[2])
                        .count(count)
                        .build());
                if (kind == StatKind.API) {
                    api += count;
                } else {
                    page += count;
                }
            }

            MetricsPushPayload payload = MetricsPushPayload.builder()
                    .serviceName(serviceName)
                    .day(today.format(DAY_FMT))
                    .totalApiCalls(api)
                    .totalPageViews(page)
                    .items(items)
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String endpoint = hubUrl.replaceAll("/+$", "") + "/api/hub/ingest";
            restTemplate.postForEntity(endpoint, new HttpEntity<>(payload, headers), Void.class);
            log.debug("지표 허브 push 완료: {} ({}건)", endpoint, items.size());
        } catch (Exception e) {
            // 허브 미가동/네트워크 오류 등은 무시 (best-effort)
            log.debug("지표 허브 push 건너뜀: {}", e.getMessage());
        }
    }
}
