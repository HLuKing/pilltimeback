package com.pillsolo.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MedicineDetailDto {
    private Long id;
    private String name;
    private String doseTime;
    private int dosePeriod;
    private String description;

    private String efficacy;        // 효능
    private String warning;         // 경고
    private String precaution;      // 주의사항
    private String interaction;     // 상호작용
    private String sideEffect;      // 부작용
    private String storage;         // 보관법
    private String imageUrl;        // 이미지
    private String manufacturer;    // 제조사
    private String usage;           // 용법 (userMethodQesitm)
}
