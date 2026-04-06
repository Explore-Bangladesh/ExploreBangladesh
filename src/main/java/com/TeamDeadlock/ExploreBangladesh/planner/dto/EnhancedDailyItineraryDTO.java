package com.TeamDeadlock.ExploreBangladesh.planner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnhancedDailyItineraryDTO {
    private Long id;
    private Integer dayNumber;
    private String date;
    private String theme; // Cultural Day, Beach Day, Adventure Day, etc.
    private String summary;
    private List<EnhancedItineraryActivityDTO> activities;
    private Integer totalCostBdt;
    private String weatherForecast;
    private String weatherIcon;
    private Integer temperature;
    private String advisories; // Travel tips for the day
    private String accommodation; // Hotel for this night
    private Integer accommodationCost;
}
