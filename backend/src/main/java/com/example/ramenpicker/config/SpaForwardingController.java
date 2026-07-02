package com.example.ramenpicker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

/**
 * SPA(React Router) 정적 서빙 + 폴백.
 * 실제 정적 파일이 있으면 그대로, 없으면(클라이언트 라우트) index.html 을 반환한다.
 * /api 요청은 이 핸들러가 아니라 컨트롤러가 먼저 처리하므로 영향 없음.
 */
@Configuration
public class SpaForwardingController implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource requested = location.createRelative(resourcePath);
                        if (requested.exists() && requested.isReadable()) {
                            return requested;
                        }
                        // API 경로는 정적 폴백 대상에서 제외 (컨트롤러가 처리)
                        if (resourcePath.startsWith("api/")) {
                            return null;
                        }
                        // 그 외 경로는 SPA 진입점으로 폴백
                        ClassPathResource index = new ClassPathResource("/static/index.html");
                        return index.exists() ? index : null;
                    }
                });
    }
}
