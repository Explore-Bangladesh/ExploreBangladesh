package com.TeamDeadlock.ExploreBangladesh.planner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnhancedTravelPlanDTO {
    private Long id;
    private String destination;
    private Integer durationDays;
    private String budgetTier;
    private String travelStyle;
    
    // Enhanced itinerary
    private List<EnhancedDailyItineraryDTO> dailyItineraries;
    
    // Budget details
    private BudgetBreakdownDto budgetBreakdown;
    private Integer totalEstimatedCostBdt;
    
    // Accommodations
    private List<HotelDTO> selectedHotels;
    
    // Attractions
    private List<AttractionDTO> plannedAttractions;
    
    // Dining
    private List<RestaurantDTO> suggestedRestaurants;
    
    // Transportation
    private List<TransportRouteDTO> transportRoutes;
    
    // Insights
    private TravelInsightsDTO insights;
    
    // Planning details
    private String createdAt;
    private String notes;
    private Boolean isSaved;
}
