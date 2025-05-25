package com.pillsolo.api.service;

import com.pillsolo.api.domain.DoseLog;
import com.pillsolo.api.domain.Pill;
import com.pillsolo.api.dto.DoseLogRequest;
import com.pillsolo.api.dto.DoseLogResponse;
import com.pillsolo.api.repository.DoseLogRepository;
import com.pillsolo.api.repository.PillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoseLogService {

    private final DoseLogRepository doseLogRepository;
    private final PillRepository pillRepository;

    public DoseLogResponse logDose(DoseLogRequest request) {
        Pill pill = pillRepository.findById(request.getPillId())
                .orElseThrow(() -> new IllegalArgumentException("해당 약을 찾을 수 없습니다"));

        DoseLog log = DoseLog.builder()
                .pill(pill)
                .doseDate(request.getDoseDate())
                .build();

        DoseLog saved = doseLogRepository.save(log);
        return new DoseLogResponse(saved.getId(), saved.getPill(), saved.getDoseDate());
    }

    public List<DoseLogResponse> getLogsByDate(LocalDate date) {
        return doseLogRepository.findAll().stream()
                .filter(log -> log.getDoseDate().equals(date))
                .map(log -> new DoseLogResponse(log.getId(), log.getPill(), log.getDoseDate()))
                .collect(Collectors.toList());
    }

    public boolean hasTakenToday(Long pillId) {
        LocalDate today = LocalDate.now();
        return doseLogRepository.existsByPillIdAndDoseDate(pillId, today);
    }

    public List<LocalDate> getDoseDatesByPillId(Long pillId) {
        return doseLogRepository.findByPillId(pillId).stream()
                .map(DoseLog::getDoseDate)
                .collect(Collectors.toList());
    }
}
