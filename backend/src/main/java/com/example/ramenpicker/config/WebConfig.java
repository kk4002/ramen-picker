package com.example.ramenpicker.config;

import com.example.ramenpicker.stats.web.TrafficInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS 설정 + 트래픽 인터셉터 등록.
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.cors.allowed-origins}")
    private String[] allowedOrigins;

    private final TrafficInterceptor trafficInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // allowedOriginPatterns 를 사용해 로컬 개발 오리진뿐 아니라
        // 터널/배포 도메인(Origin != host 인 경우)도 허용한다.
        // 쿠키/인증정보를 쓰지 않으므로 와일드카드 패턴 사용이 안전하다.
        registry.addMapping("/api/**")
                .allowedOriginPatterns(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // API 호출 트래픽 기록 (정적 리소스/페이지 요청은 제외)
        registry.addInterceptor(trafficInterceptor)
                .addPathPatterns("/api/**");
    }
}
