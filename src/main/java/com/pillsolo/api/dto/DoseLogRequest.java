package com.pillsolo.api.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DoseLogRequest {
    private Long pillId;
    private LocalDate doseDate;
}
