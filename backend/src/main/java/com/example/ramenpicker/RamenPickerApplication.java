package com.example.ramenpicker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 라면픽 애플리케이션 진입점.
 * 조건 기반(룰) 라면 추천 서비스.
 */
@SpringBootApplication
public class RamenPickerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RamenPickerApplication.class, args);
    }
}
