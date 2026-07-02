package com.example.ramenpicker.recommend.service;

import com.example.ramenpicker.common.CookType;
import com.example.ramenpicker.common.Messages;
import com.example.ramenpicker.common.RamenType;
import com.example.ramenpicker.common.SpicyPreference;
import com.example.ramenpicker.ramen.entity.RamenItem;
import com.example.ramenpicker.ramen.entity.RamenRecommendLog;
import com.example.ramenpicker.ramen.repository.RamenItemRepository;
import com.example.ramenpicker.ramen.repository.RamenRecommendLogRepository;
import com.example.ramenpicker.recommend.dto.RecommendRequest;
import com.example.ramenpicker.recommend.dto.RecommendResponse;
import com.example.ramenpicker.recommend.dto.RecommendationItem;
import com.example.ramenpicker.recommend.scorer.RamenScoreCalculator;
import com.example.ramenpicker.recommend.scorer.ScoreBreakdown;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 룰 기반 라면 추천 서비스.
 * 조건 → 태그 매칭 → 점수 계산 → 정렬 → TOP N.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RamenRecommendService {

    private final RamenItemRepository ramenItemRepository;
    private final RamenRecommendLogRepository logRepository;
    private final RamenScoreCalculator scoreCalculator;
    private final RamenReasonGenerator reasonGenerator;

    @Transactional
    public RecommendResponse recommend(RecommendRequest req) {
        List<RamenItem> all = ramenItemRepository.findAll();

        // 조리 방식이 명시됐다면 우선 후보를 필터 (뽀글이/컵/봉지 등 강한 조건)
        List<RamenItem> candidates = all.stream()
                .filter(item -> matchesCookTypeHardFilter(req.getCookType(), item.getCookType()))
                .collect(Collectors.toList());

        // 후보가 없으면 전체로 폴백 (추천이 비어버리는 것 방지)
        if (candidates.isEmpty()) {
            candidates = all;
        }

        List<Scored> scoredList = new ArrayList<>();
        for (RamenItem item : candidates) {
            ScoreBreakdown breakdown = scoreCalculator.score(req, item);
            scoredList.add(new Scored(item, breakdown));
        }

        // 점수 내림차순, 동점이면 매운맛 근접/조리시간 짧은 순
        scoredList.sort(Comparator
                .comparingInt((Scored s) -> s.breakdown.getRawScore()).reversed()
                .thenComparing(s -> nullSafeCookTime(s.item)));

        int limit = req.resolveLimit();
        List<RecommendationItem> recommendations = new ArrayList<>();
        int rank = 1;
        for (Scored s : scoredList) {
            if (rank > limit) {
                break;
            }
            recommendations.add(toRecommendationItem(req, s, rank));
            rank++;
        }

        saveLog(req, recommendations);

        String notice = req.getCookType() == CookType.PPOGEULI ? Messages.PPOGEULI_NOTICE : null;

        return RecommendResponse.builder()
                .conditions(buildConditions(req))
                .recommendations(recommendations)
                .notice(notice)
                .build();
    }

    private RecommendationItem toRecommendationItem(RecommendRequest req, Scored s, int rank) {
        RamenItem item = s.item;
        ScoreBreakdown b = s.breakdown;

        List<String> tags = new ArrayList<>();
        tags.addAll(item.getFlavorTags());
        tags.addAll(item.getSituationTags());

        String warning = item.getCookType() == CookType.PPOGEULI ? Messages.PPOGEULI_NOTICE : null;

        return RecommendationItem.builder()
                .id(item.getId())
                .rank(rank)
                .name(item.getName())
                .brand(item.getBrand())
                .matchScore(b.getMatchScore())
                .rawScore(b.getRawScore())
                .reason(reasonGenerator.generate(req, item, b))
                .cookType(item.getCookType().name())
                .cookTypeLabel(item.getCookType().getLabel())
                .ramenType(item.getRamenType().name())
                .ramenTypeLabel(item.getRamenType().getLabel())
                .spicyLevel(item.getSpicyLevel())
                .spicyLabel(item.getSpicyLabel())
                .tags(tags)
                .flavorTags(item.getFlavorTags())
                .situationTags(item.getSituationTags())
                .purchaseTags(item.getPurchaseTags())
                .cookTimeMinutes(item.getCookTimeMinutes())
                .warning(warning)
                .build();
    }

    /**
     * 조리 방식 하드 필터.
     * - ANY/null: 전체 허용
     * - 뽀글이: 뽀글이로 태깅된 라면 (없으면 봉지라면도 뽀글이 후보로 간주)
     * - 그 외 구체 방식: 해당 방식 또는 ANY(제약 없음)로 저장된 라면
     */
    private boolean matchesCookTypeHardFilter(CookType want, CookType itemCookType) {
        if (want == null || want == CookType.ANY) {
            return true;
        }
        if (want == CookType.PPOGEULI) {
            // 뽀글이는 봉지 형태가 적합 → 뽀글이 또는 봉지라면 허용
            return itemCookType == CookType.PPOGEULI || itemCookType == CookType.BAG;
        }
        return itemCookType == want || itemCookType == CookType.ANY;
    }

    private int nullSafeCookTime(RamenItem item) {
        return item.getCookTimeMinutes() == null ? Integer.MAX_VALUE : item.getCookTimeMinutes();
    }

    private Map<String, Object> buildConditions(RecommendRequest req) {
        Map<String, Object> c = new LinkedHashMap<>();
        c.put("cookType", req.getCookType() == null ? null : req.getCookType().name());
        c.put("cookTypeLabel", req.getCookType() == null ? null : req.getCookType().getLabel());
        c.put("ramenType", req.getRamenType() == null ? null : req.getRamenType().name());
        c.put("ramenTypeLabel", req.getRamenType() == null ? null : req.getRamenType().getLabel());
        c.put("situation", req.getSituation());
        c.put("flavorTags", req.getFlavorTags());
        c.put("spicyPreference", req.getSpicyPreference() == null ? null : req.getSpicyPreference().name());
        c.put("spicyPreferenceLabel", req.getSpicyPreference() == null ? null : req.getSpicyPreference().getLabel());
        c.put("purchasePlace", req.getPurchasePlace());
        return c;
    }

    private void saveLog(RecommendRequest req, List<RecommendationItem> recommendations) {
        try {
            String ids = recommendations.stream()
                    .map(r -> String.valueOf(r.getId()))
                    .collect(Collectors.joining(","));
            RamenRecommendLog logEntity = RamenRecommendLog.builder()
                    .cookType(req.getCookType() == null ? null : req.getCookType().name())
                    .ramenType(req.getRamenType() == null ? null : req.getRamenType().name())
                    .situation(req.getSituation())
                    .flavorTags(req.getFlavorTags() == null ? null : String.join(",", req.getFlavorTags()))
                    .spicyPreference(req.getSpicyPreference() == null ? null : req.getSpicyPreference().name())
                    .purchasePlace(req.getPurchasePlace())
                    .resultItemIds(ids)
                    .build();
            logRepository.save(logEntity);
        } catch (Exception e) {
            // 로그 저장 실패가 추천 응답을 막지 않도록 방어
            log.warn("추천 로그 저장 실패: {}", e.getMessage());
        }
    }

    /**
     * 빠른 추천: 미리 정의된 조건 프리셋으로 추천.
     */
    public RecommendResponse quickRecommend(String type) {
        RecommendRequest req = QuickRecommendPreset.build(type);
        return recommend(req);
    }

    private static class Scored {
        final RamenItem item;
        final ScoreBreakdown breakdown;

        Scored(RamenItem item, ScoreBreakdown breakdown) {
            this.item = item;
            this.breakdown = breakdown;
        }
    }
}
