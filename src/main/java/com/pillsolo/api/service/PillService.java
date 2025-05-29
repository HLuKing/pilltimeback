package com.pillsolo.api.service;

import com.pillsolo.api.domain.DoseLog;
import com.pillsolo.api.domain.Pill;
import com.pillsolo.api.dto.MainDoseSummaryDto;
import com.pillsolo.api.dto.MedicineDetailDto;
import com.pillsolo.api.dto.api.DrugApiResponse;
import com.pillsolo.api.repository.DoseLogRepository;
import com.pillsolo.api.repository.PillRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.pillsolo.api.dto.PillDetailResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PillService {

    private final PillRepository pillRepository;
    private final ExternalDrugApiService externalDrugApiService;
    private final DoseLogRepository doseLogRepository;


    public Pill savePill(Pill pill) {
        return pillRepository.save(pill);
    }

    public List<Pill> getAllPills() {
        return pillRepository.findAll();
    }

    public Optional<Pill> getPillById(Long id) {
        return pillRepository.findById(id);
    }

    @Transactional
    public void deletePill(Long id) {
        // 1. 복용 기록 먼저 삭제
        doseLogRepository.deleteAll(doseLogRepository.findByPillId(id));

        // 2. 그 후 약 삭제
        pillRepository.deleteById(id);
    }

    public List<Pill> searchByName(String query) {
        return pillRepository.findByNameContainingIgnoreCase(query);
    }

    public Optional<MedicineDetailDto> getPillDetails(Long id) {
        return pillRepository.findById(id).map(pill -> {
            DrugApiResponse apiResponse = externalDrugApiService.searchByItemSeq(pill.getExternalId());
            if (apiResponse != null &&
                    apiResponse.getBody() != null &&
                    apiResponse.getBody().getItems() != null &&
                    !apiResponse.getBody().getItems().isEmpty()) {

                DrugApiResponse.Item item = apiResponse.getBody().getItems().get(0);

                return new MedicineDetailDto(
                        pill.getId(),                             // ✅ ID 추가
                        pill.getName(),
                        pill.getDoseTime(),
                        pill.getDosePeriod(),
                        pill.getDescription(),                    // ✅ 복용 설명 추가
                        item.getEfcyQesitm(),
                        item.getAtpnWarnQesitm(),
                        item.getAtpnQesitm(),
                        item.getIntrcQesitm(),
                        item.getSeQesitm(),
                        item.getDepositMethodQesitm(),
                        item.getItemImage()
                );
            } else {
                return null;
            }
        });
    }


    @Transactional
    public Pill updatePill(Long id, Pill updatedPill) {
        return pillRepository.findById(id)
                .map(existing -> {
                    existing.setName(updatedPill.getName());
                    existing.setDescription(updatedPill.getDescription());
                    existing.setDoseTime(updatedPill.getDoseTime());
                    existing.setDosePeriod(updatedPill.getDosePeriod());
                    existing.setExternalId(updatedPill.getExternalId());
                    existing.setExternal(updatedPill.isExternal());
                    return pillRepository.save(existing);
                })
                .orElseThrow(() -> new NoSuchElementException("약을 찾을 수 없습니다. ID: " + id));
    }

    public List<MainDoseSummaryDto> getMainDoseSummary() {
        List<Pill> pills = pillRepository.findAll();

        return pills.stream().map(pill -> {
            int takenCount = doseLogRepository.countByPillId(pill.getId());
            int dosePeriod = pill.getDosePeriod();
            int percent = (int) ((double) takenCount / dosePeriod * 100);

            return new MainDoseSummaryDto(
                    pill.getId(),
                    pill.getName(),
                    pill.getImageUrl(),
                    pill.getManufacturer(),
                    pill.getDoseTime(),
                    dosePeriod,
                    takenCount,
                    percent,
                    pill.getDescription()
            );
        }).collect(Collectors.toList());
    }


    public PillDetailResponse getPillDetailView(Long id) {
        Pill pill = pillRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 약을 찾을 수 없습니다: " + id));

        List<LocalDate> takenDates = doseLogRepository.findByPillId(id)
                .stream()
                .map(DoseLog::getDoseDate)
                .toList();

        return new PillDetailResponse(
                pill.getId(),
                pill.getName(),
                pill.getImageUrl(),
                pill.getManufacturer(),
                pill.getDoseTime(),
                pill.getDosePeriod(),
                pill.getDescription(),
                pill.getWarning(),
                pill.getUsageInfo(),
                takenDates
        );
    }


}
