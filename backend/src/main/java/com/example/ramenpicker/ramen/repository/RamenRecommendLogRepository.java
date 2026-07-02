package com.example.ramenpicker.ramen.repository;

import com.example.ramenpicker.ramen.entity.RamenRecommendLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RamenRecommendLogRepository extends JpaRepository<RamenRecommendLog, Long> {
}
