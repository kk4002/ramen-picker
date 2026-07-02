package com.example.ramenpicker.recommend.service;

import com.example.ramenpicker.ramen.entity.RamenItem;
import com.example.ramenpicker.recommend.dto.RecommendRequest;
import com.example.ramenpicker.recommend.scorer.ScoreBreakdown;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 추천 이유 템플릿 생성기 (AI 미사용, 룰/템플릿 기반).
 */
@Component
public class RamenReasonGenerator {

    public String generate(RecommendRequest req, RamenItem item, ScoreBreakdown score) {
        List<String> matchedParts = new ArrayList<>();

        if (score.isCookTypeMatched()) {
            matchedParts.add(item.getCookType().getLabel() + " 조건");
        }
        if (score.isRamenTypeMatched()) {
            matchedParts.add(item.getRamenType().getLabel());
        }
        if (score.isSituationMatched() && req.getSituation() != null) {
            matchedParts.add(req.getSituation() + " 상황");
        }
        if (score.getMatchedFlavorCount() > 0) {
            matchedParts.add(String.join(", ", score.getMatchedFlavorTags()) + " 맛");
        }

        StringBuilder sb = new StringBuilder();
        sb.append(item.getName());

        if (!matchedParts.isEmpty()) {
            sb.append("은(는) ")
              .append(String.join(", ", matchedParts))
              .append("에 잘 맞는 추천입니다.");
        } else {
            sb.append("은(는) 조건과 부분적으로 맞는 추천입니다.");
        }

        // 매운 정도 안내
        if (item.getSpicyLabel() != null && !item.getSpicyLabel().isEmpty()) {
            if (score.isSpicyMatched()) {
                sb.append(" 매운 정도는 ").append(item.getSpicyLabel()).append("으로 원하시는 수준에 맞습니다.");
            } else if (score.isSpicyClose()) {
                sb.append(" 매운 정도는 ").append(item.getSpicyLabel()).append("으로 원하시는 수준과 비슷합니다.");
            } else {
                sb.append(" 매운 정도는 ").append(item.getSpicyLabel()).append("입니다.");
            }
        }

        return sb.toString();
    }

    /**
     * 비슷한 라면 사유 문구.
     */
    public String similarReason(RamenItem base, RamenItem other, int similarityScore) {
        List<String> parts = new ArrayList<>();
        if (base.getCookType() == other.getCookType()) {
            parts.add(other.getCookType().getLabel());
        }
        if (base.getRamenType() == other.getRamenType()) {
            parts.add(other.getRamenType().getLabel());
        }
        if (base.getSpicyLevel() != null && other.getSpicyLevel() != null
                && Math.abs(base.getSpicyLevel() - other.getSpicyLevel()) <= 1) {
            parts.add("매운맛 수준");
        }
        if (parts.isEmpty()) {
            return "맛과 상황 태그가 유사합니다.";
        }
        return String.join(", ", parts) + "이(가) 비슷합니다.";
    }
}
