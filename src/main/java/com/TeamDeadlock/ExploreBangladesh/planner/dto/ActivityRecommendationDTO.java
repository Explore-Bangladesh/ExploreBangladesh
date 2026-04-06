package com.TeamDeadlock.ExploreBangladesh.planner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityRecommendationDTO {
    private Long id;
    private Long attractionId;
    private String travelStyle;
    private Integer durationHours;
    private String recommendedTimeSlot;
    private Integer costEstimationBdt;
    private String suitableForAgeGroup;
}
