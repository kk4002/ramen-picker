package com.example.ramenpicker.recommend.service;

import com.example.ramenpicker.ramen.entity.RamenItem;
import com.example.ramenpicker.ramen.repository.RamenItemRepository;
import com.example.ramenpicker.recommend.dto.SimilarRamenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 비슷한 라면 추천 서비스.
 * 라면 타입/조리 방식/맛 태그/매운 정도/상황 태그 유사도로 점수화한다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SimilarRamenService {

    private final RamenItemRepository ramenItemRepository;
    private final RamenReasonGenerator reasonGenerator;

    private static final int SCORE_RAMEN_TYPE = 30;
    private static final int SCORE_COOK_TYPE = 25;
    private static final int SCORE_FLAVOR_PER_TAG = 8;
    private static final int SCORE_FLAVOR_MAX = 24;
    private static final int SCORE_SITUATION_PER_TAG = 5;
    private static final int SCORE_SITUATION_MAX = 15;
    private static final int SCORE_SPICY_MAX = 15;

    public SimilarRamenResponse findSimilar(Long baseId, int limit) {
        RamenItem base = ramenItemRepository.findById(baseId)
                .orElseThrow(() -> new EntityNotFoundException("라면을 찾을 수 없습니다. id=" + baseId));

        List<SimilarRamenResponse.SimilarItem> similars = ramenItemRepository.findAll().stream()
                .filter(other -> !other.getId().equals(baseId))
                .map(other -> {
                    int score = similarityScore(base, other);
                    return new ScoredSimilar(other, score);
                })
                .sorted(Comparator.comparingInt((ScoredSimilar s) -> s.score).reversed())
                .limit(limit <= 0 ? 5 : limit)
                .map(s -> SimilarRamenResponse.SimilarItem.builder()
                        .id(s.item.getId())
                        .name(s.item.getName())
                        .brand(s.item.getBrand())
                        .similarityScore(Math.min(s.score, 100))
                        .reason(reasonGenerator.similarReason(base, s.item, s.score))
                        .cookTypeLabel(s.item.getCookType().getLabel())
                        .ramenTypeLabel(s.item.getRamenType().getLabel())
                        .spicyLabel(s.item.getSpicyLabel())
                        .build())
                .collect(Collectors.toList());

        return SimilarRamenResponse.builder()
                .baseItem(SimilarRamenResponse.BaseItem.builder()
                        .id(base.getId())
                        .name(base.getName())
                        .build())
                .similarItems(similars)
                .build();
    }

    private int similarityScore(RamenItem base, RamenItem other) {
        int score = 0;

        if (base.getRamenType() == other.getRamenType()) {
            score += SCORE_RAMEN_TYPE;
        }
        if (base.getCookType() == other.getCookType()) {
            score += SCORE_COOK_TYPE;
        }

        long flavorMatches = base.getFlavorTags().stream()
                .filter(t -> other.getFlavorTags().stream().anyMatch(o -> equalsNorm(o, t)))
                .count();
        score += Math.min((int) flavorMatches * SCORE_FLAVOR_PER_TAG, SCORE_FLAVOR_MAX);

        long situationMatches = base.getSituationTags().stream()
                .filter(t -> other.getSituationTags().stream().anyMatch(o -> equalsNorm(o, t)))
                .count();
        score += Math.min((int) situationMatches * SCORE_SITUATION_PER_TAG, SCORE_SITUATION_MAX);

        if (base.getSpicyLevel() != null && other.getSpicyLevel() != null) {
            int diff = Math.abs(base.getSpicyLevel() - other.getSpicyLevel());
            if (diff == 0) {
                score += SCORE_SPICY_MAX;
            } else if (diff == 1) {
                score += SCORE_SPICY_MAX * 2 / 3;
            } else if (diff == 2) {
                score += SCORE_SPICY_MAX / 3;
            }
        }

        return score;
    }

    private boolean equalsNorm(String a, String b) {
        return a.replaceAll("\\s+", "").equalsIgnoreCase(b.replaceAll("\\s+", ""));
    }

    private static class ScoredSimilar {
        final RamenItem item;
        final int score;

        ScoredSimilar(RamenItem item, int score) {
            this.item = item;
            this.score = score;
        }
    }
}
