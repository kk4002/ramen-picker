package com.example.ramenpicker.stats.web;

import com.example.ramenpicker.stats.StatKind;
import com.example.ramenpicker.stats.service.TrafficService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * API 호출 트래픽을 기록하는 인터셉터.
 * - OPTIONS(프리플라이트) 및 지표 수집/조회 경로(/api/stats/**)는 자기잡음 방지를 위해 제외.
 */
@Component
@RequiredArgsConstructor
public class TrafficInterceptor implements HandlerInterceptor {

    private final TrafficService trafficService;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        String method = request.getMethod();
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return;
        }
        String uri = request.getRequestURI();
        if (uri == null || uri.startsWith("/api/stats")) {
            return;
        }
        if (uri.startsWith("/api/")) {
            trafficService.record(StatKind.API, uri, method);
        }
    }
}
