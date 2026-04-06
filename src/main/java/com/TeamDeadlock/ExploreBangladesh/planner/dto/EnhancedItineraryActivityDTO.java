package com.TeamDeadlock.ExploreBangladesh.planner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnhancedItineraryActivityDTO {
    private Long id;
    private String activityName;
    private String type; // attraction, dining, rest, transport
    private String description;
    private LocalTime startTime;
    private LocalTime endTime;
    private String location;
    private Double latitude;
    private Double longitude;
    private Integer costBdt;
    private String category;
    private String notes;
    private Double rating;
    private Integer duration; // in minutes
}
