package com.pillsolo.api.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean external;

    @Column(name = "image_url")
    private String imageUrl;

    @Column
    private String manufacturer;

    private String name;
    private String description;

    @OneToMany(mappedBy = "pill", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PillSchedule> schedules = new ArrayList<>();

    @Column(nullable = false)
    private int dosePeriod;

    private Long externalId;

    @Column
    private String warning;

    @Column(name = "usage_info")
    private String usageInfo;

    private String color;
}
