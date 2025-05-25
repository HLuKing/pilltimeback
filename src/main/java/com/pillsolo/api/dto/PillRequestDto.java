package com.pillsolo.api.dto;

import lombok.Data;

@Data
public class PillRequestDto {
    private String name;
    private String description;
    private String doseTime;
    private String dosePeriod;
    private Long externalId; // 외부 약 ID
}
