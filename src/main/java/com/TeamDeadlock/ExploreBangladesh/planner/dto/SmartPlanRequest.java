package com.TeamDeadlock.ExploreBangladesh.planner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;

/**
 * DTO for Smart Plan generation request
 * User provides minimal input, system generates complete itinerary
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmartPlanRequest {

    @NotBlank(message = "Destination is required")
    private String destination;

    @Min(value = 1, message = "Duration must be at least 1 day")
    private Integer durationDays;

    private String budgetTier; // economy, mid_range, luxury

    private String travelStyle; // adventure, relaxation, cultural, family, solo

    private LocalDate startDate;

    private String preferredLanguage;

}
