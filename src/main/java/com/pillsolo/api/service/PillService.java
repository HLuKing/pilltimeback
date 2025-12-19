package com.pillsolo.api.service;

import com.pillsolo.api.domain.DoseLog;
import com.pillsolo.api.domain.Pill;
import com.pillsolo.api.domain.PillSchedule;
import com.pillsolo.api.dto.PillCreateRequest;
import com.pillsolo.api.dto.MainDoseSummaryDto;
import com.pillsolo.api.dto.MedicineDetailDto;
import com.pillsolo.api.dto.PillDetailResponse;
import com.pillsolo.api.dto.api.DrugApiResponse;
import com.pillsolo.api.repository.DoseLogRepository;
import com.pillsolo.api.repository.PillRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
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

    @Transactional
    public Pill savePill(PillCreateRequest request) {
        Pill pill = Pill.builder()
                .name(request.getName())
                .description(request.getDescription())
                .dosePeriod(request.getDosePeriod())
                .external(request.isExternal())
                .externalId(request.getExternalId())
                .imageUrl(request.getImageUrl())
                .manufacturer(request.getManufacturer())
                .color(request.getColor())
                .build();

        if (request.getDoseTimes() != null) {
            for (LocalTime time : request.getDoseTimes()) {
                PillSchedule schedule = PillSchedule.builder()
                        .pill(pill)
                        .doseTime(time)
                        .doseSlot(determineDoseSlot(time))
                        .build();
                pill.getSchedules().add(schedule);
            }
        }

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
            DrugApiResponse apiResponse = null;
            if (pill.getExternalId() != null) {
                apiResponse = externalDrugApiService.searchByItemSeq(pill.getExternalId());
            }

            DrugApiResponse.Item item = (apiResponse != null &&
                                        apiResponse.getBody() != null &&
                                        apiResponse.getBody().getItems() != null &&
                                        !apiResponse.getBody().getItems().isEmpty())
                                        ? apiResponse.getBody().getItems().get(0)
                                        : new DrugApiResponse.Item();

            String doseTimeStr = pill.getSchedules().stream()
                    .map(s -> s.getDoseTime().toString())
                    .collect(Collectors.joining(", "));

            return new MedicineDetailDto(
                    pill.getId(),
                    pill.getName(),
                    doseTimeStr,
                    pill.getDosePeriod(),
                    pill.getDescription(),
                    item.getEfcyQesitm(),
                    item.getAtpnWarnQesitm(),
                    item.getAtpnQesitm(),
                    item.getIntrcQesitm(),
                    item.getSeQesitm(),
                    item.getDepositMethodQesitm(),
                    pill.getImageUrl() != null ? pill.getImageUrl() : item.getItemImage(),
                    pill.getManufacturer(),
                    item.getUseMethodQesitm()
            );
        });
    }


    @Transactional
    public Pill updatePill(Long id, PillCreateRequest request) {
        return pillRepository.findById(id)
                .map(existing -> {
                    existing.setName(request.getName());
                    existing.setDescription(request.getDescription());
                    existing.setDosePeriod(request.getDosePeriod());
                    existing.setExternal(request.isExternal());
                    existing.setImageUrl(request.getImageUrl());
                    existing.setManufacturer(request.getManufacturer());
                    existing.setColor(request.getColor());

                    if (request.getExternalId() != null) {
                        existing.setExternalId(request.getExternalId());
                    }

                    existing.getSchedules().clear();

                    if (request.getDoseTimes() != null) {
                        for (LocalTime time : request.getDoseTimes()) {
                            PillSchedule schedule = PillSchedule.builder()
                                    .pill(existing)
                                    .doseTime(time)
                                    .doseSlot(determineDoseSlot(time))
                                    .build();
                            existing.getSchedules().add(schedule);
                        }
                    }
                    return pillRepository.save(existing);
                })
                .orElseThrow(() -> new NoSuchElementException("약을 찾을 수 없습니다. ID: " + id));
    }

    public List<MainDoseSummaryDto> getMainDoseSummary() {
        List<Pill> pills = pillRepository.findAll();

        return pills.stream().map(pill -> {
            int takenCount = doseLogRepository.countByPillId(pill.getId());
            int dosePeriod = pill.getDosePeriod();
            int percent = (dosePeriod > 0) ? (int) ((double) takenCount / dosePeriod * 100) : 0;

            String doseTimeStr = pill.getSchedules().stream()
                    .map(s -> s.getDoseTime().toString())
                    .collect(Collectors.joining(", "));

            return new MainDoseSummaryDto(
                    pill.getId(),
                    pill.getName(),
                    pill.getImageUrl(),
                    pill.getManufacturer(),
                    doseTimeStr,
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

        String doseTimeStr = pill.getSchedules().stream()
                .map(s -> s.getDoseTime().toString())
                .collect(Collectors.joining(", "));

        return new PillDetailResponse(
                pill.getId(),
                pill.getName(),
                pill.getImageUrl(),
                pill.getManufacturer(),
                doseTimeStr,
                pill.getDosePeriod(),
                pill.getDescription(),
                pill.getWarning(),
                pill.getUsageInfo(),
                takenDates
        );
    }

    private String determineDoseSlot(LocalTime time) {
        int hour = time.getHour();
        if (hour >= 6 && hour < 12) return "아침";
        if (hour >= 12 && hour < 18) return "점심";
        if (hour >= 18 && hour < 22) return "저녁";
        return "취침";
    }
}
