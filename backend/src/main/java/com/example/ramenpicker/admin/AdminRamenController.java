package com.example.ramenpicker.admin;

import com.example.ramenpicker.ramen.dto.RamenDetailResponse;
import com.example.ramenpicker.ramen.dto.RamenUpsertRequest;
import com.example.ramenpicker.ramen.service.RamenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 관리자용 라면 등록/수정/삭제 API.
 * (초기 MVP: 인증은 미구현, 이후 단계에서 보강)
 */
@RestController
@RequestMapping("/api/admin/ramen")
@RequiredArgsConstructor
public class AdminRamenController {

    private final RamenService ramenService;

    @PostMapping
    public ResponseEntity<RamenDetailResponse> create(@Valid @RequestBody RamenUpsertRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ramenService.create(request));
    }

    @PutMapping("/{id}")
    public RamenDetailResponse update(@PathVariable Long id, @Valid @RequestBody RamenUpsertRequest request) {
        return ramenService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ramenService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
