package com.pillsolo.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PillCreateRequest {

    private Long id;
    private String name;
    private int dosePeriod;
    private String description;
    private boolean external;
    private Long externalId;
    private String imageUrl;
    private String manufacturer;
    private String color;

    private List<LocalTime> doseTimes;
}
