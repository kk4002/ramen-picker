package com.example.ramenpicker.config;

import com.example.ramenpicker.common.CookType;
import com.example.ramenpicker.common.RamenType;
import com.example.ramenpicker.ramen.entity.RamenAlias;
import com.example.ramenpicker.ramen.entity.RamenItem;
import com.example.ramenpicker.ramen.repository.RamenAliasRepository;
import com.example.ramenpicker.ramen.repository.RamenItemRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 애플리케이션 기동 시 seed-ramen.json 을 읽어 초기 데이터를 적재한다.
 * 이미 데이터가 있으면 건너뛴다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RamenItemRepository ramenItemRepository;
    private final RamenAliasRepository ramenAliasRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        if (ramenItemRepository.count() > 0) {
            log.info("라면 데이터가 이미 존재하여 seed 적재를 건너뜁니다.");
            return;
        }

        ClassPathResource resource = new ClassPathResource("seed-ramen.json");
        try (InputStream is = resource.getInputStream()) {
            List<SeedRamen> seeds = objectMapper.readValue(is,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, SeedRamen.class));

            for (SeedRamen seed : seeds) {
                RamenItem item = RamenItem.builder()
                        .name(seed.name)
                        .brand(seed.brand)
                        .ramenType(RamenType.valueOf(seed.ramenType))
                        .cookType(CookType.valueOf(seed.cookType))
                        .spicyLevel(seed.spicyLevel)
                        .spicyLabel(seed.spicyLabel)
                        .flavorTags(seed.flavorTags == null ? new ArrayList<>() : seed.flavorTags)
                        .situationTags(seed.situationTags == null ? new ArrayList<>() : seed.situationTags)
                        .purchaseTags(seed.purchaseTags == null ? new ArrayList<>() : seed.purchaseTags)
                        .cookTimeMinutes(seed.cookTimeMinutes)
                        .description(seed.description)
                        .imageUrl(seed.imageUrl)
                        .build();
                RamenItem saved = ramenItemRepository.save(item);

                if (seed.aliases != null) {
                    for (String alias : seed.aliases) {
                        ramenAliasRepository.save(RamenAlias.builder()
                                .ramenItemId(saved.getId())
                                .alias(alias)
                                .build());
                    }
                }
            }
            log.info("seed 라면 {}건 적재 완료", seeds.size());
        }
    }

    /** seed JSON 매핑용 내부 클래스 */
    static class SeedRamen {
        public String name;
        public String brand;
        public String ramenType;
        public String cookType;
        public Integer spicyLevel;
        public String spicyLabel;
        public List<String> flavorTags;
        public List<String> situationTags;
        public List<String> purchaseTags;
        public Integer cookTimeMinutes;
        public String description;
        public String imageUrl;
        public List<String> aliases;
    }
}
