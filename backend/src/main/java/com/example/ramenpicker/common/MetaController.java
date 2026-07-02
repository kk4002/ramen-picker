package com.example.ramenpicker.common;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 프론트 셀렉터/필터 구성을 위한 메타데이터 API.
 * enum 라벨과 대표 태그 목록을 제공한다.
 */
@RestController
@RequestMapping("/api/meta")
public class MetaController {

    @GetMapping
    public Map<String, Object> meta() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("cookTypes", enumOptions(CookType.values(), c -> c.name(), c -> c.getLabel()));
        result.put("ramenTypes", enumOptions(RamenType.values(), r -> r.name(), r -> r.getLabel()));
        result.put("spicyPreferences", spicyOptions());
        result.put("flavorTags", flavorTags());
        result.put("situationTags", situationTags());
        result.put("purchaseTags", purchaseTags());
        result.put("quickRecommends", quickRecommends());
        return result;
    }

    private <T> List<Map<String, String>> enumOptions(T[] values,
                                                       java.util.function.Function<T, String> valueFn,
                                                       java.util.function.Function<T, String> labelFn) {
        List<Map<String, String>> list = new ArrayList<>();
        for (T v : values) {
            Map<String, String> m = new LinkedHashMap<>();
            m.put("value", valueFn.apply(v));
            m.put("label", labelFn.apply(v));
            list.add(m);
        }
        return list;
    }

    private List<Map<String, Object>> spicyOptions() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (SpicyPreference s : SpicyPreference.values()) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("value", s.name());
            m.put("label", s.getLabel());
            m.put("minLevel", s.getMinLevel());
            m.put("maxLevel", s.getMaxLevel());
            list.add(m);
        }
        return list;
    }

    private List<String> flavorTags() {
        return Arrays.asList("얼큰", "깔끔", "진한국물", "고소", "매콤달달", "짭짤",
                "해물맛", "소고기맛", "마늘맛", "마라맛", "치즈", "크림", "불맛", "단짠", "담백");
    }

    private List<String> situationTags() {
        return Arrays.asList("해장", "야식", "점심", "간식", "술안주", "스트레스 해소",
                "가볍게", "든든함", "빠르게", "편의점 한 끼", "도전");
    }

    private List<String> purchaseTags() {
        return Arrays.asList("편의점", "마트", "온라인", "일반");
    }

    private List<Map<String, String>> quickRecommends() {
        String[][] presets = {
                {"hangover", "해장 라면"},
                {"night_snack", "야식 라면"},
                {"convenience_cup", "편의점 컵라면"},
                {"ppogeuli", "뽀글이 추천"},
                {"mild", "안 매운 라면"},
                {"spicy_soup", "매운 국물라면"},
                {"stir_fried", "볶음면"},
                {"heavy_meal", "든든한 라면"},
        };
        List<Map<String, String>> list = new ArrayList<>();
        for (String[] p : presets) {
            Map<String, String> m = new LinkedHashMap<>();
            m.put("type", p[0]);
            m.put("label", p[1]);
            list.add(m);
        }
        return list;
    }
}
