package com.pillsolo.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MedicineDetailDto {
    private Long id;                // ✅ 추가
    private String name;
    private String doseTime;
    private int dosePeriod;
    private String description;     // ✅ 추가
    private String efficacy;
    private String warning;
    private String precaution;
    private String interaction;
    private String sideEffect;
    private String storage;
    private String imageUrl;
}
