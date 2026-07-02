package com.example.ramenpicker.stats.repository;

import com.example.ramenpicker.stats.StatKind;
import com.example.ramenpicker.stats.entity.TrafficLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TrafficLogRepository extends JpaRepository<TrafficLog, Long> {

    long countByKind(StatKind kind);

    /** 경로별 집계: [kind, path, count] */
    @Query("SELECT t.kind, t.path, COUNT(t) FROM TrafficLog t " +
            "GROUP BY t.kind, t.path ORDER BY COUNT(t) DESC")
    List<Object[]> countByKindAndPath();

    /** 일자별 집계: [day, kind, count] */
    @Query("SELECT t.day, t.kind, COUNT(t) FROM TrafficLog t " +
            "WHERE t.day >= :from GROUP BY t.day, t.kind ORDER BY t.day")
    List<Object[]> countByDay(@Param("from") LocalDate from);

    /** 특정 일자의 경로별 집계 (배치 push 용): [serviceName, kind, path, count] */
    @Query("SELECT t.serviceName, t.kind, t.path, COUNT(t) FROM TrafficLog t " +
            "WHERE t.day = :day GROUP BY t.serviceName, t.kind, t.path")
    List<Object[]> aggregateForDay(@Param("day") LocalDate day);
}
