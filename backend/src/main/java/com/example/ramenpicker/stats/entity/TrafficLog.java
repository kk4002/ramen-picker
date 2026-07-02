package com.example.ramenpicker.stats.entity;

import com.example.ramenpicker.stats.StatKind;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 트래픽 로그. API 호출/페이지 방문을 한 건씩 적층한다(원본 데이터).
 * serviceName 을 함께 저장해 향후 중앙 허브 이관/구분이 쉽도록 한다.
 */
@Entity
@Table(name = "traffic_log", indexes = {
        @Index(name = "idx_traffic_day", columnList = "log_day"),
        @Index(name = "idx_traffic_kind_path", columnList = "kind,path")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrafficLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 서비스 식별자 (예: ramen-picker) */
    @Column(name = "service_name", length = 100, nullable = false)
    private String serviceName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private StatKind kind;

    /** API 엔드포인트 경로 또는 프론트 페이지 경로 */
    @Column(nullable = false, length = 300)
    private String path;

    /** HTTP 메서드 (PAGE 방문은 null) */
    @Column(length = 10)
    private String method;

    /** 집계 편의를 위한 일자 (day 는 예약어라 컬럼명은 log_day) */
    @Column(name = "log_day", nullable = false)
    private LocalDate day;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;
}
