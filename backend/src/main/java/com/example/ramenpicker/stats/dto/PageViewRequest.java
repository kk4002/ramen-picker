package com.example.ramenpicker.stats.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 프론트 페이지(메뉴) 방문 기록 요청 DTO.
 */
@Getter
@Setter
public class PageViewRequest {
    /** 방문한 프론트 경로 (예: /recommend) */
    private String path;
}
