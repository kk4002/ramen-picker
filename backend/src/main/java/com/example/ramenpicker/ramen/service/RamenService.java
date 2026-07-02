package com.example.ramenpicker.ramen.service;

import com.example.ramenpicker.common.CookType;
import com.example.ramenpicker.common.RamenType;
import com.example.ramenpicker.ramen.dto.RamenDetailResponse;
import com.example.ramenpicker.ramen.dto.RamenUpsertRequest;
import com.example.ramenpicker.ramen.entity.RamenAlias;
import com.example.ramenpicker.ramen.entity.RamenItem;
import com.example.ramenpicker.ramen.repository.RamenAliasRepository;
import com.example.ramenpicker.ramen.repository.RamenItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 라면 기본 CRUD/조회/검색 서비스.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RamenService {

    private final RamenItemRepository ramenItemRepository;
    private final RamenAliasRepository ramenAliasRepository;

    /**
     * 목록/검색 조회. 조건은 모두 선택적.
     */
    public List<RamenDetailResponse> list(String keyword, CookType cookType, RamenType ramenType,
                                          Integer spicyLevel, String flavorTag,
                                          String situationTag, String purchaseTag) {
        List<RamenItem> items;

        if (keyword != null && !keyword.trim().isEmpty()) {
            // 이름/브랜드 + 별칭 검색
            Set<RamenItem> merged = new LinkedHashSet<>(ramenItemRepository.searchByKeyword(keyword.trim()));
            List<RamenAlias> aliases = ramenAliasRepository.findByAliasContainingIgnoreCase(keyword.trim());
            for (RamenAlias alias : aliases) {
                ramenItemRepository.findById(alias.getRamenItemId()).ifPresent(merged::add);
            }
            items = new java.util.ArrayList<>(merged);
        } else {
            items = ramenItemRepository.findAll();
        }

        return items.stream()
                .filter(i -> cookType == null || cookType == CookType.ANY || i.getCookType() == cookType)
                .filter(i -> ramenType == null || ramenType == RamenType.ANY || i.getRamenType() == ramenType)
                .filter(i -> spicyLevel == null || (i.getSpicyLevel() != null && i.getSpicyLevel().equals(spicyLevel)))
                .filter(i -> flavorTag == null || containsIgnoreSpace(i.getFlavorTags(), flavorTag))
                .filter(i -> situationTag == null || containsIgnoreSpace(i.getSituationTags(), situationTag))
                .filter(i -> purchaseTag == null || containsIgnoreSpace(i.getPurchaseTags(), purchaseTag))
                .map(RamenDetailResponse::from)
                .collect(Collectors.toList());
    }

    public RamenDetailResponse getDetail(Long id) {
        return RamenDetailResponse.from(getEntity(id));
    }

    public RamenItem getEntity(Long id) {
        return ramenItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("라면을 찾을 수 없습니다. id=" + id));
    }

    @Transactional
    public RamenDetailResponse create(RamenUpsertRequest request) {
        RamenItem saved = ramenItemRepository.save(request.toEntity());
        return RamenDetailResponse.from(saved);
    }

    @Transactional
    public RamenDetailResponse update(Long id, RamenUpsertRequest request) {
        RamenItem item = getEntity(id);
        request.applyTo(item);
        return RamenDetailResponse.from(ramenItemRepository.save(item));
    }

    @Transactional
    public void delete(Long id) {
        if (!ramenItemRepository.existsById(id)) {
            throw new EntityNotFoundException("라면을 찾을 수 없습니다. id=" + id);
        }
        ramenItemRepository.deleteById(id);
    }

    private boolean containsIgnoreSpace(List<String> tags, String wanted) {
        if (tags == null || wanted == null) {
            return false;
        }
        String w = wanted.replaceAll("\\s+", "").toLowerCase();
        return tags.stream()
                .map(t -> t.replaceAll("\\s+", "").toLowerCase())
                .anyMatch(t -> t.contains(w) || w.contains(t));
    }
}
