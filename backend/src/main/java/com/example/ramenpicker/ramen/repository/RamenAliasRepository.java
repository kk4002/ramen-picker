package com.example.ramenpicker.ramen.repository;

import com.example.ramenpicker.ramen.entity.RamenAlias;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RamenAliasRepository extends JpaRepository<RamenAlias, Long> {

    List<RamenAlias> findByAliasContainingIgnoreCase(String alias);

    List<RamenAlias> findByRamenItemId(Long ramenItemId);
}
