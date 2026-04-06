package com.TeamDeadlock.ExploreBangladesh.planner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for AI-generated travel plan
 * Contains detailed AI-powered itinerary with specific timings
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIGeneratedPlanDTO {

    private Long planId;

    private String userId;

    private String destination;

    private Integer durationDays;

    private LocalDate startDate;

    private LocalDate endDate;

    private String budgetTier;

    private String travelStyle;

    private BigDecimal estimatedBudget;

    private String aiInsights;

    private String weatherInfo;

    private String bestTimeToVisit;

    private List<AIDailyItineraryDTO> dailyItineraries;

    private String accommodation;

    private List<String> recommendedAttractions;

    private List<String> recommendedRestaurants;

    private String transportationTips;

    private String localTravelTips;

    private Boolean isSaved;

    private String planStatus; // draft, saved, active
}
