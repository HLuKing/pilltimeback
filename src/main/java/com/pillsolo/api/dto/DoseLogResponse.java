package com.pillsolo.api.dto;

import com.pillsolo.api.domain.Pill;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoseLogResponse {
    private Long id;
    private Pill pill; // 추가됨
    private LocalDate doseDate;

    // 기존 생성자도 유지할 수 있음
    public DoseLogResponse(Long id, LocalDate doseDate) {
        this.id = id;
        this.doseDate = doseDate;
    }
}
