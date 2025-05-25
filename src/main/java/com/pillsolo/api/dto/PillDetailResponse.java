package com.pillsolo.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class PillDetailResponse {
    private Long id;
    private String name;
    private String imageUrl;
    private String manufacturer;
    private String doseTime;
    private int dosePeriod;
    private String description;
    private String warning;
    private String usage;
    private List<LocalDate> takenDates;
}
