package com.pillsolo.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MainDoseSummaryDto {
    private Long id;
    private String name;
    private String imageUrl;
    private String manufacturer;
    private String doseTime;     // 예: 아침
    private int dosePeriod;      // 총 복용일
    private int takenDays;       // 복용 완료된 일 수
    private int takenPercent;    // 퍼센트 (takenDays * 100 / dosePeriod)
    private String description;
}
