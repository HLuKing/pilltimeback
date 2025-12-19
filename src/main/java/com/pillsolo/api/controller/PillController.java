package com.pillsolo.api.controller;

import com.pillsolo.api.domain.Pill;
import com.pillsolo.api.dto.MainDoseSummaryDto;
import com.pillsolo.api.dto.MedicineDetailDto;
import com.pillsolo.api.dto.PillCreateRequest;
import com.pillsolo.api.dto.PillDetailResponse;
import com.pillsolo.api.dto.api.DrugApiResponse;
import com.pillsolo.api.service.DoseLogService;
import com.pillsolo.api.service.ExternalDrugApiService;
import com.pillsolo.api.service.PillService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/medicines")
public class PillController {

    private final PillService pillService;
    private final ExternalDrugApiService externalDrugApiService;
    private final DoseLogService doseLogService;

    public PillController(PillService pillService, ExternalDrugApiService externalDrugApiService, DoseLogService doseLogService) {
        this.pillService = pillService;
        this.externalDrugApiService = externalDrugApiService;
        this.doseLogService = doseLogService;
    }

    // 💊 약 전체 조회
    @GetMapping
    public ResponseEntity<List<Pill>> getAllPills() {
        return ResponseEntity.ok(pillService.getAllPills());
    }

    // 💊 약 추가
    @PostMapping
    public ResponseEntity<Pill> addPill(@RequestBody PillCreateRequest request) {
        Pill saved = pillService.savePill(request);
        return ResponseEntity.ok(saved);
    }
    // 💊 약 이름으로 검색
    @GetMapping("/search")
    public ResponseEntity<List<Pill>> searchByName(@RequestParam String query) {
        return ResponseEntity.ok(pillService.searchByName(query));
    }

    // 🌐 외부 API 검색
    @GetMapping("/external/search")
    public List<DrugApiResponse.Item> externalSearch(@RequestParam String query) {
        DrugApiResponse response = externalDrugApiService.search(query);
        if (response == null || response.getBody() == null || response.getBody().getItems() == null) {
            return Collections.emptyList();
        }
        return response.getBody().getItems();
    }
    @GetMapping("/main")
    public ResponseEntity<List<MainDoseSummaryDto>> getMainDoseSummary() {
        return ResponseEntity.ok(pillService.getMainDoseSummary());
    }


    // 💊 약 상세 조회 by ID
    @GetMapping("/{id}")
    public ResponseEntity<Pill> getPillById(@PathVariable Long id) {
        return pillService.getPillById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 💊 약 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePill(@PathVariable Long id) {
        pillService.deletePill(id);
        return ResponseEntity.noContent().build();
    }


    // 🌐 외부 API에서 상세 정보 조회 (id 기반)
    @GetMapping("/{id}/details")
    public ResponseEntity<MedicineDetailDto> getPillDetails(@PathVariable Long id) {
        return pillService.getPillDetails(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PutMapping("/{id}")
    public ResponseEntity<Pill> updatePill(@PathVariable Long id, @RequestBody PillCreateRequest request) {
        Pill result = pillService.updatePill(id, request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/detail-view")
    public ResponseEntity<PillDetailResponse> getDetailView(@PathVariable Long id) {
        return ResponseEntity.ok(pillService.getPillDetailView(id));
    }

    @GetMapping("/{pillId}/dose-history")
    public ResponseEntity<List<LocalDate>> getDoseHistory(@PathVariable Long pillId) {
        List<LocalDate> doseDates = doseLogService.getDoseDatesByPillId(pillId);
        return ResponseEntity.ok(doseDates);
    }


}
