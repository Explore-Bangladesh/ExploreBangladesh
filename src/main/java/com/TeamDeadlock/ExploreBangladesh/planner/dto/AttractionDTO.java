package com.TeamDeadlock.ExploreBangladesh.planner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttractionDTO {
    private Long id;
    private String name;
    private String description;
    private String category;
    private Double latitude;
    private Double longitude;
    private Integer estimatedDurationHours;
    private Integer entryFeeBdt;
    private String bestTimeToVisit;
    private Double rating;
    private String travelStyle;
    private String difficultyLevel;
}
