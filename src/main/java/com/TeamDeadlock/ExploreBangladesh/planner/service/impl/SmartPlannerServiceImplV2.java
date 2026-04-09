package com.TeamDeadlock.ExploreBangladesh.planner.service.impl;

import com.TeamDeadlock.ExploreBangladesh.planner.dto.*;
import com.TeamDeadlock.ExploreBangladesh.planner.entity.*;
import com.TeamDeadlock.ExploreBangladesh.planner.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SmartPlannerServiceImplV2 {

    private final TravelPlanRepository travelPlanRepository;
    private final AIGeneratedTravelPlanRepository aiGeneratedTravelPlanRepository;
    private final ObjectMapper objectMapper;
    
    // Phase 2 repositories
    private final AttractionRepository attractionRepository;
    private final HotelRepository hotelRepository;
    private final RestaurantRepository restaurantRepository;

    public EnhancedTravelPlanDTO generateSmartPlanPreview(SmartPlanRequest request, String userId) {
        // Input validation
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        if (request.getDestination() == null || request.getDestination().isBlank()) {
            throw new IllegalArgumentException("Destination cannot be empty");
        }
        if (request.getDurationDays() == null || request.getDurationDays() < 1 || request.getDurationDays() > 30) {
            throw new IllegalArgumentException("Duration must be between 1 and 30 days, got: " + request.getDurationDays());
        }
        String budgetTier = request.getBudgetTier();
        if (budgetTier == null || budgetTier.isBlank()) {
            throw new IllegalArgumentException("Budget tier cannot be null or empty");
        }
        // Normalize budget tier: accept both "mid_range" and "midrange"
        String normalizedBudgetTier = budgetTier.toLowerCase().replace("_", "");
        if (!("budget".equals(normalizedBudgetTier) || "midrange".equals(normalizedBudgetTier) || "luxury".equals(normalizedBudgetTier))) {
            throw new IllegalArgumentException("Invalid budget tier: " + budgetTier + ". Must be budget, mid_range/midrange, or luxury");
        }
        // Update request with normalized value
        request.setBudgetTier(normalizedBudgetTier);
        
        log.info("Generating Phase 2.0 smart plan PREVIEW for user: {}, destination: {} (NOT SAVING YET)", userId, request.getDestination());

        // Create temporary plan object (NOT saved to DB)
        TravelPlan tempPlan = new TravelPlan();
        tempPlan.setUserId(userId);
        tempPlan.setDestination(request.getDestination());
        tempPlan.setDurationDays(request.getDurationDays());
        tempPlan.setBudgetTier(request.getBudgetTier());
        tempPlan.setTravelStyle(request.getTravelStyle());
        // Note: NOT calling travelPlanRepository.save() - this is just a preview!
        
        // Map destination name to ID
        Long destinationId = mapDestinationToId(request.getDestination());

        // ⭐ STEP 1: SELECT HOTEL FIRST (with coordinates)
        List<HotelDTO> selectedHotels = selectAccommodations(destinationId, request);
        Hotel primaryHotel = getPrimaryHotel(destinationId, request);
        
        log.info("🏨 Selected primary hotel: {} at ({}, {})", 
            primaryHotel != null ? primaryHotel.getName() : "Default",
            primaryHotel != null ? primaryHotel.getLatitude() : "?",
            primaryHotel != null ? primaryHotel.getLongitude() : "?");

        // STEP 2: Get attractions (before itinerary generation for routing optimization)
        List<Attraction> availableAttractions = getAvailableAttractionsForPlanning(
            destinationId, request, tempPlan.getDurationDays()
        );

        // STEP 3: Get restaurant recommendations
        List<Restaurant> availableRestaurants = getAvailableRestaurantsForPlanning(
            destinationId, request
        );

        // STEP 4: Generate intelligent daily itineraries WITH hotel location & travel times
        List<EnhancedDailyItineraryDTO> dailyItineraries = generateIntelligentItinerariesWithRouting(
            tempPlan, request, primaryHotel, availableAttractions, availableRestaurants
        );


        List<AttractionDTO> plannedAttractions = availableAttractions.stream()
            .limit(tempPlan.getDurationDays() * 3)
            .map(this::convertToAttractionDTO)
            .collect(Collectors.toList());


        List<RestaurantDTO> suggestedRestaurants = availableRestaurants.stream()
            .limit(8)
            .map(this::convertToRestaurantDTO)
            .collect(Collectors.toList());


        List<TransportRouteDTO> transportRoutes = getTransportRoutes(request);


        BudgetBreakdownDto budgetBreakdown = calculateBudgetBreakdown(
            dailyItineraries, selectedHotels, request
        );


        TravelInsightsDTO insights = generateTravelInsights(
            tempPlan, dailyItineraries, request
        );

        // Build enhanced plan response
        EnhancedTravelPlanDTO enhancedPlan = new EnhancedTravelPlanDTO();
        enhancedPlan.setId(null);  // No ID yet - not saved to DB
        enhancedPlan.setDestination(request.getDestination());
        enhancedPlan.setDurationDays(request.getDurationDays());
        enhancedPlan.setBudgetTier(request.getBudgetTier());
        enhancedPlan.setTravelStyle(request.getTravelStyle());
        enhancedPlan.setDailyItineraries(dailyItineraries);
        enhancedPlan.setBudgetBreakdown(budgetBreakdown);
        enhancedPlan.setTotalEstimatedCostBdt(budgetBreakdown.getTotal().intValue());
        enhancedPlan.setSelectedHotels(selectedHotels);
        enhancedPlan.setPlannedAttractions(plannedAttractions);
        enhancedPlan.setSuggestedRestaurants(suggestedRestaurants);
        enhancedPlan.setTransportRoutes(transportRoutes);
        enhancedPlan.setInsights(insights);
        enhancedPlan.setIsSaved(false);  // NOT yet saved

        log.info("Completed plan preview generation with realistic travel times");
        return enhancedPlan;
    }

    @Transactional(readOnly = false)
    public EnhancedTravelPlanDTO savePlanToDB(SmartPlanRequest request, String userId) {
        // ✅ INPUT VALIDATION
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        if (request.getDestination() == null || request.getDestination().isBlank()) {
            throw new IllegalArgumentException("Destination cannot be empty");
        }
        if (request.getDurationDays() == null || request.getDurationDays() < 1 || request.getDurationDays() > 30) {
            throw new IllegalArgumentException("Duration must be between 1 and 30 days");
        }
        String budgetTier = request.getBudgetTier();
        if (budgetTier == null || budgetTier.isBlank()) {
            throw new IllegalArgumentException("Budget tier cannot be null or empty");
        }
        // Normalize budget tier: accept both "mid_range" and "midrange"
        String normalizedBudgetTier = budgetTier.toLowerCase().replace("_", "");
        if (!("budget".equals(normalizedBudgetTier) || "midrange".equals(normalizedBudgetTier) || "luxury".equals(normalizedBudgetTier))) {
            throw new IllegalArgumentException("Invalid budget tier: " + budgetTier + ". Must be budget, mid_range/midrange, or luxury");
        }
        // Update request with normalized value
        request.setBudgetTier(normalizedBudgetTier);
        
        log.info("Saving smart plan for user: {}, destination: {}", userId, request.getDestination());

        // Create base travel plan and save to DB
        TravelPlan travelPlan = createBasePlan(request, userId);
        
        // Map destination name to ID
        Long destinationId = mapDestinationToId(request.getDestination());

        // ⭐ STEP 1: SELECT HOTEL FIRST (with coordinates)
        Hotel primaryHotel = getPrimaryHotel(destinationId, request);
        
        log.info("🏨 Selected primary hotel: {} at ({}, {})", 
            primaryHotel != null ? primaryHotel.getName() : "Default",
            primaryHotel != null ? primaryHotel.getLatitude() : "?",
            primaryHotel != null ? primaryHotel.getLongitude() : "?");

        // Get attractions and restaurants
        List<Attraction> availableAttractions = getAvailableAttractionsForPlanning(
            destinationId, request, travelPlan.getDurationDays()
        );
        List<Restaurant> availableRestaurants = getAvailableRestaurantsForPlanning(
            destinationId, request
        );

        // ⭐ STEP 2: Generate intelligent daily itineraries WITH hotel location & travel times
        List<EnhancedDailyItineraryDTO> dailyItineraries = generateIntelligentItinerariesWithRouting(
            travelPlan, request, primaryHotel, availableAttractions, availableRestaurants
        );

        // Get attraction recommendations
        List<AttractionDTO> plannedAttractions = availableAttractions.stream()
            .limit(travelPlan.getDurationDays() * 3)
            .map(this::convertToAttractionDTO)
            .collect(Collectors.toList());

        // Get restaurant suggestions
        List<RestaurantDTO> suggestedRestaurants = availableRestaurants.stream()
            .limit(8)
            .map(this::convertToRestaurantDTO)
            .collect(Collectors.toList());

        // Get transport routes
        List<TransportRouteDTO> transportRoutes = getTransportRoutes(request);

        // Calculate budget breakdown
        BudgetBreakdownDto budgetBreakdown = calculateBudgetBreakdown(
            dailyItineraries, Collections.singletonList(convertToHotelDTO(primaryHotel)), request
        );

        // Generate insights
        TravelInsightsDTO insights = generateTravelInsights(
            travelPlan, dailyItineraries, request
        );

        // Build enhanced plan response
        EnhancedTravelPlanDTO enhancedPlan = new EnhancedTravelPlanDTO();
        enhancedPlan.setId(travelPlan.getPlanId());
        enhancedPlan.setDestination(request.getDestination());
        enhancedPlan.setDurationDays(request.getDurationDays());
        enhancedPlan.setBudgetTier(request.getBudgetTier());
        enhancedPlan.setTravelStyle(request.getTravelStyle());
        enhancedPlan.setDailyItineraries(dailyItineraries);
        enhancedPlan.setBudgetBreakdown(budgetBreakdown);
        enhancedPlan.setTotalEstimatedCostBdt(budgetBreakdown.getTotal().intValue());
        enhancedPlan.setSelectedHotels(Collections.singletonList(convertToHotelDTO(primaryHotel)));
        enhancedPlan.setPlannedAttractions(plannedAttractions);
        enhancedPlan.setSuggestedRestaurants(suggestedRestaurants);
        enhancedPlan.setTransportRoutes(transportRoutes);
        enhancedPlan.setInsights(insights);
        enhancedPlan.setIsSaved(true);

        // Save the complete plan as JSON to database
        try {
            String planDataJson = objectMapper.writeValueAsString(enhancedPlan);
            travelPlan.setPlanData(planDataJson);
            travelPlanRepository.save(travelPlan);
        } catch (Exception e) {
            log.error("Critical: Failed to serialize plan to JSON. Rolling back transaction.", e);
            throw new RuntimeException("Failed to save plan: " + e.getMessage(), e);
        }

        log.info("Plan saved to database with realistic travel times. User: {}", travelPlan.getPlanId(), userId);
        return enhancedPlan;
    }

    /**
     * Save AI-generated plan to AIGeneratedTravelPlan table
     * This is the NEW method for storing AI-generated plans separately from manual plans
     * Called when user clicks "Save This Plan to My Trips" button
     */
    @Transactional
    public EnhancedTravelPlanDTO savePlanToAITable(SmartPlanRequest request, String userId) {
        // ✅ Call overloaded method with null fullPlanData (will regenerate)
        return savePlanToAITable(request, userId, null);
    }

    /**
     * Save AI plan - WITH support for providing pre-generated full plan data
     * ✅ NEW: Accepts fullPlanData to avoid regeneration
     * If fullPlanData is provided, store it AS-IS without regenerating
     * If null, fall back to generating from scratch
     */
    @Transactional
    public EnhancedTravelPlanDTO savePlanToAITable(SmartPlanRequest request, String userId, Object fullPlanDataObj) {
        // ✅ INPUT VALIDATION
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        if (request.getDestination() == null || request.getDestination().isBlank()) {
            throw new IllegalArgumentException("Destination cannot be empty");
        }
        if (request.getDurationDays() == null || request.getDurationDays() < 1 || request.getDurationDays() > 30) {
            throw new IllegalArgumentException("Duration must be between 1 and 30 days");
        }
        String budgetTier = request.getBudgetTier();
        if (budgetTier == null || budgetTier.isBlank()) {
            throw new IllegalArgumentException("Budget tier cannot be null or empty");
        }
        // Normalize budget tier: accept both "mid_range" and "midrange"
        String normalizedBudgetTier = budgetTier.toLowerCase().replace("_", "");
        if (!("budget".equals(normalizedBudgetTier) || "midrange".equals(normalizedBudgetTier) || "luxury".equals(normalizedBudgetTier))) {
            throw new IllegalArgumentException("Invalid budget tier: " + budgetTier + ". Must be budget, mid_range/midrange, or luxury");
        }
        // Update request with normalized value
        request.setBudgetTier(normalizedBudgetTier);
        
        log.info("💾 Saving AI-generated plan to AIGeneratedTravelPlan table for user: {}, destination: {}", userId, request.getDestination());

        // Create AI plan entity (new table)
        AIGeneratedTravelPlan aiPlan = new AIGeneratedTravelPlan();
        aiPlan.setUserId(userId);
        aiPlan.setDestination(request.getDestination());
        aiPlan.setDurationDays(request.getDurationDays());
        aiPlan.setBudgetTier(normalizedBudgetTier);
        aiPlan.setTravelStyle(request.getTravelStyle());

        try {
            // ✅ CRITICAL: If fullPlanData is provided, use it directly WITHOUT regenerating
            if (fullPlanDataObj != null) {
                log.info("✅ [DIRECT SAVE] Using provided fullPlanData - STORING AS RAW JSON (NO PARSING)");
                
                // ✅ Store the AI response AS-IS without trying to parse it
                String planDataJson = objectMapper.writeValueAsString(fullPlanDataObj);
                aiPlan.setPlanData(planDataJson);
                
                log.info("📦 Stored AI plan data length: {} bytes", planDataJson.length());
                
                // Try to extract total budget from the plan object
                try {
                    if (fullPlanDataObj instanceof Map) {
                        Map<String, Object> planMap = (Map<String, Object>) fullPlanDataObj;
                        Object budgetObj = planMap.get("totalEstimatedCostBdt");
                        if (budgetObj != null) {
                            long budgetValue = ((Number) budgetObj).longValue();
                            aiPlan.setTotalBudgetEstimate(BigDecimal.valueOf(budgetValue));
                            log.info("💰 Total budget from fullPlanData: {}", budgetValue);
                        }
                    }
                } catch (Exception e) {
                    log.warn("⚠️ Could not extract budget from fullPlanData, leaving empty", e);
                }
                
                AIGeneratedTravelPlan savedPlan = aiGeneratedTravelPlanRepository.save(aiPlan);
                log.info("✅ AI Plan saved to AIGeneratedTravelPlan table with ID: {}. User: {}", savedPlan.getId(), userId);
                
                // ✅ Return a simple response with ID and RAW PLAN DATA
                // Frontend will handle JSON parsing, not Java
                EnhancedTravelPlanDTO response = new EnhancedTravelPlanDTO();
                response.setId(savedPlan.getId());
                response.setIsSaved(true);
                response.setDestination(request.getDestination());
                response.setDurationDays(request.getDurationDays());
                response.setBudgetTier(request.getBudgetTier());
                response.setTravelStyle(request.getTravelStyle());
                // ✅ NOTE: We're NOT deserializing the full plan - that happens on frontend
                
                return response;
            }
            
            // ❌ Fallback: If NO fullPlanData provided, regenerate from scratch
            log.warn("⚠️ [FALLBACK] fullPlanData is null - falling back to regeneration");
            
            // Map destination name to ID
            Long destinationId = mapDestinationToId(request.getDestination());

            // ⭐ STEP 1: SELECT HOTEL FIRST (with coordinates)
            Hotel primaryHotel = getPrimaryHotel(destinationId, request);
            
            log.info("🏨 Selected primary hotel: {} at ({}, {})", 
                primaryHotel != null ? primaryHotel.getName() : "Default",
                primaryHotel != null ? primaryHotel.getLatitude() : "?",
                primaryHotel != null ? primaryHotel.getLongitude() : "?");

            // Get attractions and restaurants
            List<Attraction> availableAttractions = getAvailableAttractionsForPlanning(
                destinationId, request, aiPlan.getDurationDays()
            );
            List<Restaurant> availableRestaurants = getAvailableRestaurantsForPlanning(
                destinationId, request
            );

            // ⭐ STEP 2: Generate intelligent daily itineraries WITH hotel location & travel times
            List<EnhancedDailyItineraryDTO> dailyItineraries = generateIntelligentItinerariesWithRouting(
                null, request, primaryHotel, availableAttractions, availableRestaurants
            );

            // Get attraction recommendations
            List<AttractionDTO> plannedAttractions = availableAttractions.stream()
                .limit(aiPlan.getDurationDays() * 3)
                .map(this::convertToAttractionDTO)
                .collect(Collectors.toList());

            // Get restaurant suggestions
            List<RestaurantDTO> suggestedRestaurants = availableRestaurants.stream()
                .limit(8)
                .map(this::convertToRestaurantDTO)
                .collect(Collectors.toList());

            // Get transport routes
            List<TransportRouteDTO> transportRoutes = getTransportRoutes(request);

            // Calculate budget breakdown
            BudgetBreakdownDto budgetBreakdown = calculateBudgetBreakdown(
                dailyItineraries, Collections.singletonList(convertToHotelDTO(primaryHotel)), request
            );

            // Generate insights
            TravelInsightsDTO insights = generateTravelInsights(
                null, dailyItineraries, request
            );

            // Build enhanced plan response
            EnhancedTravelPlanDTO enhancedPlan = new EnhancedTravelPlanDTO();
            // ID will be set after save
            enhancedPlan.setDestination(request.getDestination());
            enhancedPlan.setDurationDays(request.getDurationDays());
            enhancedPlan.setBudgetTier(normalizedBudgetTier);
            enhancedPlan.setTravelStyle(request.getTravelStyle());
            enhancedPlan.setDailyItineraries(dailyItineraries);
            enhancedPlan.setBudgetBreakdown(budgetBreakdown);
            enhancedPlan.setTotalEstimatedCostBdt(budgetBreakdown.getTotal().intValue());
            enhancedPlan.setSelectedHotels(Collections.singletonList(convertToHotelDTO(primaryHotel)));
            enhancedPlan.setPlannedAttractions(plannedAttractions);
            enhancedPlan.setSuggestedRestaurants(suggestedRestaurants);
            enhancedPlan.setTransportRoutes(transportRoutes);
            enhancedPlan.setInsights(insights);
            enhancedPlan.setIsSaved(true);

            // Set budget estimate
            aiPlan.setTotalBudgetEstimate(budgetBreakdown.getTotal());

            // Save the complete plan as JSON to aiGeneratedTravelPlan table
            String planDataJson = objectMapper.writeValueAsString(enhancedPlan);
            aiPlan.setPlanData(planDataJson);
            AIGeneratedTravelPlan savedPlan = aiGeneratedTravelPlanRepository.save(aiPlan);
            
            // Set ID in response DTO after save
            enhancedPlan.setId(savedPlan.getId());
            
            log.info("✅ AI Plan saved (regenerated) to AIGeneratedTravelPlan table with ID: {}. User: {}", savedPlan.getId(), userId);
        } catch (Exception e) {
            log.error("Critical: Failed to save AI plan. Rolling back transaction.", e);
            throw new RuntimeException("Failed to save AI plan: " + e.getMessage(), e);
        }

        return new EnhancedTravelPlanDTO(); // Placeholder, actual return above
    }

    @Transactional
    public EnhancedTravelPlanDTO generateSmartPlanV2(SmartPlanRequest request, String userId) {
        // Input validation
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        if (request.getDestination() == null || request.getDestination().isBlank()) {
            throw new IllegalArgumentException("Destination cannot be empty");
        }
        if (request.getDurationDays() == null || request.getDurationDays() < 1 || request.getDurationDays() > 30) {
            throw new IllegalArgumentException("Duration must be between 1 and 30 days");
        }
        String budgetTier = request.getBudgetTier();
        if (budgetTier == null || budgetTier.isBlank()) {
            throw new IllegalArgumentException("Budget tier cannot be null or empty");
        }
        // Normalize budget tier: accept both "mid_range" and "midrange"
        String normalizedBudgetTier = budgetTier.toLowerCase().replace("_", "");
        if (!("budget".equals(normalizedBudgetTier) || "midrange".equals(normalizedBudgetTier) || "luxury".equals(normalizedBudgetTier))) {
            throw new IllegalArgumentException("Invalid budget tier: " + budgetTier + ". Must be budget, mid_range/midrange, or luxury");
        }
        // Update request with normalized value
        request.setBudgetTier(normalizedBudgetTier);
        
        log.info("Generating smart plan for user: {}, destination: {}", userId, request.getDestination());
        TravelPlan travelPlan = createBasePlan(request, userId);
        
        // Map destination name to ID
        Long destinationId = mapDestinationToId(request.getDestination());

        // ⭐ STEP 1: SELECT HOTEL FIRST (with coordinates)
        Hotel primaryHotel = getPrimaryHotel(destinationId, request);

        // Get attractions and restaurants
        List<Attraction> availableAttractions = getAvailableAttractionsForPlanning(
            destinationId, request, travelPlan.getDurationDays()
        );
        List<Restaurant> availableRestaurants = getAvailableRestaurantsForPlanning(
            destinationId, request
        );

        // ⭐ STEP 2: Generate intelligent daily itineraries WITH hotel location & travel times
        List<EnhancedDailyItineraryDTO> dailyItineraries = generateIntelligentItinerariesWithRouting(
            travelPlan, request, primaryHotel, availableAttractions, availableRestaurants
        );


        List<AttractionDTO> plannedAttractions = availableAttractions.stream()
            .limit(travelPlan.getDurationDays() * 3)
            .map(this::convertToAttractionDTO)
            .collect(Collectors.toList());


        List<RestaurantDTO> suggestedRestaurants = availableRestaurants.stream()
            .limit(8)
            .map(this::convertToRestaurantDTO)
            .collect(Collectors.toList());


        List<TransportRouteDTO> transportRoutes = getTransportRoutes(request);


        BudgetBreakdownDto budgetBreakdown = calculateBudgetBreakdown(
            dailyItineraries, Collections.singletonList(convertToHotelDTO(primaryHotel)), request
        );


        TravelInsightsDTO insights = generateTravelInsights(
            travelPlan, dailyItineraries, request
        );

        // Build enhanced plan response
        EnhancedTravelPlanDTO enhancedPlan = new EnhancedTravelPlanDTO();
        enhancedPlan.setId(travelPlan.getPlanId());
        enhancedPlan.setDestination(request.getDestination());
        enhancedPlan.setDurationDays(request.getDurationDays());
        enhancedPlan.setBudgetTier(request.getBudgetTier());
        enhancedPlan.setTravelStyle(request.getTravelStyle());
        enhancedPlan.setDailyItineraries(dailyItineraries);
        enhancedPlan.setBudgetBreakdown(budgetBreakdown);
        enhancedPlan.setTotalEstimatedCostBdt(budgetBreakdown.getTotal().intValue());
        enhancedPlan.setSelectedHotels(Collections.singletonList(convertToHotelDTO(primaryHotel)));
        enhancedPlan.setPlannedAttractions(plannedAttractions);
        enhancedPlan.setSuggestedRestaurants(suggestedRestaurants);
        enhancedPlan.setTransportRoutes(transportRoutes);
        enhancedPlan.setInsights(insights);
        enhancedPlan.setIsSaved(true);

        log.info("Completed Phase 2.0 plan generation with REALISTIC TRAVEL TIMES for travel plan ID: {}", travelPlan.getPlanId());
        return enhancedPlan;
    }

    public EnhancedTravelPlanDTO regeneratePlanDisplay(TravelPlan existingPlan, String userId) {
        log.info("Regenerating display for plan ID: {}", existingPlan.getPlanId());
        Long destinationId = mapDestinationToId(existingPlan.getDestination());


        SmartPlanRequest request = new SmartPlanRequest(
            existingPlan.getDestination(),
            existingPlan.getDurationDays(),
            existingPlan.getBudgetTier(),
            existingPlan.getTravelStyle(),
            existingPlan.getStartDate(),
            "English"  // Default language
        );

        // ⭐ SELECT HOTEL FIRST
        Hotel primaryHotel = getPrimaryHotel(destinationId, request);

        // Get attractions and restaurants
        List<Attraction> availableAttractions = getAvailableAttractionsForPlanning(
            destinationId, request, existingPlan.getDurationDays()
        );
        List<Restaurant> availableRestaurants = getAvailableRestaurantsForPlanning(
            destinationId, request
        );

        // ⭐ Generate intelligent daily itineraries WITH hotel location & travel times
        List<EnhancedDailyItineraryDTO> dailyItineraries = generateIntelligentItinerariesWithRouting(
            existingPlan, request, primaryHotel, availableAttractions, availableRestaurants
        );


        List<AttractionDTO> plannedAttractions = availableAttractions.stream()
            .limit(existingPlan.getDurationDays() * 3)
            .map(this::convertToAttractionDTO)
            .collect(Collectors.toList());

        List<RestaurantDTO> suggestedRestaurants = availableRestaurants.stream()
            .limit(8)
            .map(this::convertToRestaurantDTO)
            .collect(Collectors.toList());

        List<TransportRouteDTO> transportRoutes = getTransportRoutes(request);
        BudgetBreakdownDto budgetBreakdown = calculateBudgetBreakdown(
            dailyItineraries, Collections.singletonList(convertToHotelDTO(primaryHotel)), request
        );
        TravelInsightsDTO insights = generateTravelInsights(existingPlan, dailyItineraries, request);

        // Build enhanced plan response
        EnhancedTravelPlanDTO enhancedPlan = new EnhancedTravelPlanDTO();
        enhancedPlan.setId(existingPlan.getPlanId());
        enhancedPlan.setDestination(existingPlan.getDestination());
        enhancedPlan.setDurationDays(existingPlan.getDurationDays());
        enhancedPlan.setBudgetTier(existingPlan.getBudgetTier());
        enhancedPlan.setTravelStyle(existingPlan.getTravelStyle());
        enhancedPlan.setDailyItineraries(dailyItineraries);
        enhancedPlan.setBudgetBreakdown(budgetBreakdown);
        enhancedPlan.setTotalEstimatedCostBdt(budgetBreakdown.getTotal().intValue());
        enhancedPlan.setSelectedHotels(Collections.singletonList(convertToHotelDTO(primaryHotel)));
        enhancedPlan.setPlannedAttractions(plannedAttractions);
        enhancedPlan.setSuggestedRestaurants(suggestedRestaurants);
        enhancedPlan.setTransportRoutes(transportRoutes);
        enhancedPlan.setInsights(insights);
        enhancedPlan.setIsSaved(true);

        log.info("Completed display regeneration for plan ID: {} with realistic travel times", existingPlan.getPlanId());
        return enhancedPlan;
    }

    /**
     * Generate intelligent daily itineraries with travel time optimization
     * 
     * Algorithm:
     * 1. Start from hotel
     * 2. For each day:
     *    - Current location = Hotel
     *    - Find nearest unvisited attraction
     *    - Calculate travel time to that attraction
     *    - Schedule activity with realistic times
     *    - Update current location
     *    - When lunch time approaches, schedule meal at nearest restaurant
     *    - Calculate travel back to hotel at end of day
     */
    private List<EnhancedDailyItineraryDTO> generateIntelligentItinerariesWithRouting(
            TravelPlan travelPlan,
            SmartPlanRequest request,
            Hotel hotel,
            List<Attraction> availableAttractions,
            List<Restaurant> availableRestaurants) {

        List<EnhancedDailyItineraryDTO> dailyItineraries = new ArrayList<>();
        
        if (hotel == null) {
            log.warn("Hotel is null, creating default hotel for routing");
            // Create a temporary default hotel
            hotel = new Hotel();
            hotel.setName("Default Hotel");
            hotel.setLatitude(getDefaultLatitude(request.getDestination()));
            hotel.setLongitude(getDefaultLongitude(request.getDestination()));
        }

        // Ensure hotel has coordinates
        if (hotel.getLatitude() == null || hotel.getLongitude() == null) {
            log.warn("Hotel coordinates are null, using destination defaults");
            hotel.setLatitude(getDefaultLatitude(request.getDestination()));
            hotel.setLongitude(getDefaultLongitude(request.getDestination()));
        }

        // Hotel coordinates (starting point for all days)
        Coordinates hotelLocation = new Coordinates(hotel.getLatitude(), hotel.getLongitude());
        
        log.info("Hotel Base Location: {} ({}, {})", hotel.getName(), 
            hotelLocation.getLatitude(), hotelLocation.getLongitude());

        // Attractions available for planning
        List<Attraction> remainingAttractions = new ArrayList<>(availableAttractions);
        Set<Long> usedAttractionIds = new HashSet<>();
        List<String> recentCategories = new ArrayList<>();

        boolean noMoreAttractions = false;  // Flag to indicate when attractions are exhausted

        for (int day = 1; day <= request.getDurationDays(); day++) {
            EnhancedDailyItineraryDTO dailyPlan = new EnhancedDailyItineraryDTO();
            dailyPlan.setDayNumber(day);
            dailyPlan.setDate("Day " + day);

            List<EnhancedItineraryActivityDTO> activities = new ArrayList<>();

            // REALISTIC SCHEDULE STARTING FROM HOTEL
            LocalTime currentTime = LocalTime.of(9, 0);  // 9:00 AM wake up
            Coordinates currentLocation = hotelLocation;
            boolean lunchScheduled = false;

            // ═══════════════════════════════════════════════════════
            // CHECK: If all attractions are exhausted (at start of day)
            // ═══════════════════════════════════════════════════════
            boolean attractionsExhaustedToday = usedAttractionIds.size() >= availableAttractions.size();

            // ═══════════════════════════════════════════════════════
            // BREAKFAST at Hotel
            // Time adjusted based on whether attractions are exhausted
            // ═══════════════════════════════════════════════════════
            String breakfastStart = attractionsExhaustedToday ? "08:00" : "09:00";
            String breakfastEnd = attractionsExhaustedToday ? "09:00" : "10:00";
            
            Restaurant breakfastRest = selectBreakfastRestaurant(mapDestinationToId(request.getDestination()));
            activities.add(createActivity(
                "Breakfast at " + (hotel.getName()),
                "Dining",
                breakfastStart,
                breakfastEnd,
                hotel.getName(),
                hotel.getLatitude(),
                hotel.getLongitude(),
                "Start your day with local cuisine",
                "Hotel breakfast included"
            ));
            currentTime = attractionsExhaustedToday ? LocalTime.of(9, 0) : LocalTime.of(10, 0);

            // ═══════════════════════════════════════════════════════
            // CHECK: If all attractions are exhausted, show completion message
            // ═══════════════════════════════════════════════════════
            if (attractionsExhaustedToday) {
                // Calculate days with attractions and remaining days
                int daysWithAttractions = day - 1;
                int remainingDays = request.getDurationDays() - daysWithAttractions;
                
                log.info("✅ All {} attractions covered in {} days. Remaining days: {}", 
                         usedAttractionIds.size(), daysWithAttractions, remainingDays);
                
                // Add completion message activity (09:00-17:00)
                activities.add(createActivity(
                    "All attractions in our database for " + request.getDestination() + 
                    " have been covered in " + daysWithAttractions + " days. " +
                    "Make your plan for the remaining " + remainingDays + " days. Stay healthy.",
                    "Info",
                    "09:00",
                    "17:00",
                    hotel.getName(),
                    hotel.getLatitude(),
                    hotel.getLongitude(),
                    "You've completed all planned activities!",
                    "Enjoy free time or explore local recommendations."
                ));
                
                // Set currentTime to 17:00 to continue with return to hotel
                currentTime = LocalTime.of(17, 0);
                
                // Set flag to break after this day
                noMoreAttractions = true;
            }

            // ═══════════════════════════════════════════════════════
            // MORNING ACTIVITY (with travel time)
            // Skip if all attractions are exhausted
            // ═══════════════════════════════════════════════════════
            if (!noMoreAttractions && !remainingAttractions.isEmpty()) {
                // Find closest attraction to hotel
                Attraction morningAttraction = findClosestAttraction(
                    currentLocation, remainingAttractions, usedAttractionIds, request.getDestination()
                );

                if (morningAttraction != null) {
                    Coordinates attractionLocation = new Coordinates(
                        morningAttraction.getLatitude(),
                        morningAttraction.getLongitude()
                    );

                    // Travel time from hotel to attraction
                    double travelTimeHours = calculateTravelTime(currentLocation, attractionLocation);
                    long travelTimeMinutes = Math.round(travelTimeHours * 60);

                    // Update current time (add travel)
                    currentTime = currentTime.plusMinutes(travelTimeMinutes);

                    // Add travel activity
                    LocalTime travelEndTime = currentTime;
                    activities.add(createActivity(
                        "Travel to " + morningAttraction.getName(),
                        "Travel",
                        formatTime(currentTime.minusMinutes(travelTimeMinutes)),
                        formatTime(travelEndTime),
                        "En route",
                        null,
                        null,
                        String.format("%.1f km drive (~%d mins)", 
                            haversineDistance(currentLocation, attractionLocation), travelTimeMinutes),
                        ""
                    ));

                    // Visit duration (with validation for negative values)
                    double visitDurationHours = morningAttraction.getEstimatedDurationHours() != null 
                        && morningAttraction.getEstimatedDurationHours() > 0
                        ? morningAttraction.getEstimatedDurationHours() 
                        : 2.0;
                    long visitMinutes = Math.round(visitDurationHours * 60);

                    LocalTime activityStartTime = currentTime;
                    currentTime = currentTime.plusMinutes(visitMinutes);
                    LocalTime activityEndTime = currentTime;

                    // Add attraction activity
                    activities.add(createAttractionActivity(morningAttraction, 
                        formatTime(activityStartTime), formatTime(activityEndTime)
                    ));

                    usedAttractionIds.add(morningAttraction.getId());
                    recentCategories.add(morningAttraction.getCategory());
                    currentLocation = attractionLocation;

                    log.info("📍 Day {}: Morning attraction: {} (Travel: {} mins, Visit: {} mins)", 
                        day, morningAttraction.getName(), travelTimeMinutes, visitMinutes);
                }
            }

            // ═══════════════════════════════════════════════════════
            // LUNCH (around 13:00 or based on current location)
            // Skip if all attractions are exhausted
            // ═══════════════════════════════════════════════════════
            if (!noMoreAttractions && currentTime.isBefore(LocalTime.of(14, 0))) {
                // Find nearest restaurant to current location
                Restaurant lunchRest = findNearestRestaurant(
                    currentLocation, availableRestaurants
                );

                if (lunchRest != null) {
                    Coordinates restaurantLocation = new Coordinates(
                        lunchRest.getLatitude(),
                        lunchRest.getLongitude()
                    );

                    // Travel to restaurant
                    double travelTimeToRest = calculateTravelTime(currentLocation, restaurantLocation);
                    long travelMinutesToRest = Math.round(travelTimeToRest * 60);

                    if (currentTime.plusMinutes(travelMinutesToRest).isBefore(LocalTime.of(14, 30))) {
                        currentTime = currentTime.plusMinutes(travelMinutesToRest);

                        // Lunch activity (1.5 hours)
                        LocalTime lunchStart = currentTime;
                        currentTime = currentTime.plusMinutes(90);
                        LocalTime lunchEnd = currentTime;

                        activities.add(createActivity(
                            "Lunch at " + lunchRest.getName(),
                            "Dining",
                            formatTime(lunchStart),
                            formatTime(lunchEnd),
                            lunchRest.getName(),
                            lunchRest.getLatitude(),
                            lunchRest.getLongitude(),
                            lunchRest.getCuisineType(),
                            "Avg cost: ৳" + lunchRest.getAverageMealCostBdt()
                        ));

                        currentLocation = restaurantLocation;
                        lunchScheduled = true;

                        log.info("🍽️  Day {}: Lunch at {} (Travel: {} mins)", day, lunchRest.getName(), travelMinutesToRest);
                    }
                }
            }

            // ═══════════════════════════════════════════════════════
            // AFTERNOON ACTIVITY (after lunch)
            // Skip if all attractions are exhausted
            // ═══════════════════════════════════════════════════════
            if (!noMoreAttractions && !remainingAttractions.isEmpty() && currentTime.isBefore(LocalTime.of(18, 0))) {
                Attraction afternoonAttraction = findClosestAttraction(
                    currentLocation, remainingAttractions, usedAttractionIds, request.getDestination()
                );

                if (afternoonAttraction != null) {
                    Coordinates attractionLocation = new Coordinates(
                        afternoonAttraction.getLatitude(),
                        afternoonAttraction.getLongitude()
                    );

                    // Travel time
                    double travelTimeHours = calculateTravelTime(currentLocation, attractionLocation);
                    long travelMinutes = Math.round(travelTimeHours * 60);

                    currentTime = currentTime.plusMinutes(travelMinutes);

                    // Visit duration (with validation for negative values)
                    double visitDurationHours = afternoonAttraction.getEstimatedDurationHours() != null 
                        && afternoonAttraction.getEstimatedDurationHours() > 0
                        ? afternoonAttraction.getEstimatedDurationHours() 
                        : 2.0;
                    long visitMinutes = Math.round(visitDurationHours * 60);

                    LocalTime activityStart = currentTime;
                    currentTime = currentTime.plusMinutes(visitMinutes);
                    LocalTime activityEnd = currentTime;

                    activities.add(createAttractionActivity(afternoonAttraction,
                        formatTime(activityStart), formatTime(activityEnd)
                    ));

                    usedAttractionIds.add(afternoonAttraction.getId());
                    recentCategories.add(afternoonAttraction.getCategory());
                    currentLocation = attractionLocation;

                    log.info("📍 Day {}: Afternoon attraction: {} (Travel: {} mins, Visit: {} mins)", 
                        day, afternoonAttraction.getName(), travelMinutes, visitMinutes);
                }
            }

            // ═══════════════════════════════════════════════════════
            // RETURN TO HOTEL & REST
            // ═══════════════════════════════════════════════════════
            double hotelReturnTravelTime = calculateTravelTime(currentLocation, hotelLocation);
            long hotelReturnMinutes = Math.round(hotelReturnTravelTime * 60);

            LocalTime returnStart = currentTime;
            currentTime = currentTime.plusMinutes(hotelReturnMinutes);
            LocalTime returnEnd = currentTime;

            if (returnEnd.isBefore(LocalTime.of(18, 0))) {
                returnEnd = LocalTime.of(18, 0);  // Ensure we get back by 6 PM
                currentTime = LocalTime.of(18, 0);
            }

            activities.add(createActivity(
                "Return to Hotel",
                "Travel",
                formatTime(returnStart),
                formatTime(returnEnd),
                "En route back to " + hotel.getName(),
                null,
                null,
                String.format("Return journey (~%d mins)", hotelReturnMinutes),
                ""
            ));

            // Rest at hotel
            activities.add(createActivity(
                "Rest & Refresh at Hotel",
                "Rest",
                formatTime(returnEnd),
                "20:00",
                hotel.getName(),
                hotel.getLatitude(),
                hotel.getLongitude(),
                "Rest and prepare for dinner",
                ""
            ));

            // ═══════════════════════════════════════════════════════
            // DINNER (8 PM)
            // ═══════════════════════════════════════════════════════
            Restaurant dinnerRest = selectDinnerRestaurant(
                mapDestinationToId(request.getDestination()), 
                request.getTravelStyle()
            );

            activities.add(createActivity(
                "Dinner at " + (dinnerRest != null ? dinnerRest.getName() : "Local Restaurant"),
                "Dining",
                "20:00",
                "21:30",
                dinnerRest != null ? dinnerRest.getName() : "Local restaurant",
                dinnerRest != null ? dinnerRest.getLatitude() : null,
                dinnerRest != null ? dinnerRest.getLongitude() : null,
                dinnerRest != null ? dinnerRest.getCuisineType() : "Local cuisine",
                dinnerRest != null ? ("Avg cost: ৳" + dinnerRest.getAverageMealCostBdt()) : ""
            ));

            // Sleep
            activities.add(createActivity(
                "Rest at Hotel",
                "Rest",
                "21:30",
                "23:00",
                hotel.getName(),
                hotel.getLatitude(),
                hotel.getLongitude(),
                "Sleep and refresh",
                ""
            ));

            // Set daily details
            dailyPlan.setTheme(generateDayTheme(day, request));
            dailyPlan.setActivities(activities);
            dailyPlan.setTotalCostBdt(calculateDailyCost(activities));
            dailyPlan.setWeatherForecast("⚠️ Real-time weather API not configured - Please integrate OpenWeather API or WeatherAPI");
            dailyPlan.setAccommodation(hotel.getName());
            dailyPlan.setAccommodationCost(calculateHotelCost(request));
            dailyPlan.setAdvisories(generateDayAdvisories(request.getTravelStyle()));

            dailyItineraries.add(dailyPlan);
            
            // ═══════════════════════════════════════════════════════
            // If all attractions have been covered, stop generating more days
            // ═══════════════════════════════════════════════════════
            if (noMoreAttractions) {
                log.info("🏁 Itinerary generation complete. Total days created: {}", dailyItineraries.size());
                break;  // Exit the day loop after finishing the current day
            }
        }

        return dailyItineraries;
    }

    /**
     * Select accommodations based on budget tier and preferences
     */
    private List<HotelDTO> selectAccommodations(Long destinationId, SmartPlanRequest request) {
        Long actualDestinationId = destinationId != null ? destinationId : 1L;
        List<Hotel> hotels;

        if ("budget".equalsIgnoreCase(request.getBudgetTier())) {
            hotels = hotelRepository.findHotelsByDestinationAndBudget(actualDestinationId, 3000);
        } else if ("luxury".equalsIgnoreCase(request.getBudgetTier())) {
            hotels = hotelRepository.findHotelsByDestinationAndStarRating(actualDestinationId, 5);
        } else {
            hotels = hotelRepository.findTopHotelsByDestination(actualDestinationId, 5);
        }

        return hotels.stream()
            .map(this::convertToHotelDTO)
            .collect(Collectors.toList());
    }

    /**
     * Get recommended attractions filtered by travel style
     */
    private List<AttractionDTO> getRecommendedAttractions(
            Long destinationId, SmartPlanRequest request, Integer durationDays) {

        // Default to Dhaka (ID 1) if destination ID not found
        Long actualDestinationId = destinationId != null ? destinationId : 1L;
        List<Attraction> attractions = attractionRepository
            .findTopAttractionsByDestinationAndStyle(actualDestinationId, request.getTravelStyle());

        int attractionLimit = Math.min(durationDays * 2, attractions.size());
        return attractions.stream()
            .limit(attractionLimit)
            .map(this::convertToAttractionDTO)
            .collect(Collectors.toList());
    }

    /**
     * Get restaurant suggestions for all meal times
     */
    private List<RestaurantDTO> getRestaurantSuggestions(
            Long destinationId, SmartPlanRequest request) {

        List<Restaurant> restaurants = restaurantRepository
            .findRestaurantsByDestinationAndPriceRange(
                destinationId, 
                request.getBudgetTier()
            );

        return restaurants.stream()
            .limit(8)
            .map(this::convertToRestaurantDTO)
            .collect(Collectors.toList());
    }

    /**
     * Get transport routes between destinations
     */
    private List<TransportRouteDTO> getTransportRoutes(SmartPlanRequest request) {
        // Phase 2: For single destination, return local transport info
        return new ArrayList<>();
    }

    /**
     * Calculate detailed budget breakdown
     */
    private BudgetBreakdownDto calculateBudgetBreakdown(
            List<EnhancedDailyItineraryDTO> dailyItineraries,
            List<HotelDTO> hotels,
            SmartPlanRequest request) {

        // Calculate costs in BigDecimal
        long foodTotal = dailyItineraries.stream()
            .flatMap(d -> d.getActivities().stream())
            .filter(a -> "Dining".equals(a.getType()))
            .mapToLong(a -> a.getCostBdt() != null ? a.getCostBdt() : 0)
            .sum();

        // ✅ USE ACTUAL CONSTRAINTS
        // Use ONLY the primary/selected hotel (first hotel in list)
        long accommodationTotal = 0L;
        if (hotels != null && !hotels.isEmpty()) {
            // 🔧 BUG FIX: Use only the PRIMARY (first) hotel, not all hotels
            HotelDTO primaryHotel = hotels.get(0);
            long hotelPricePerNight = 0L;
            if ("budget".equalsIgnoreCase(request.getBudgetTier())) {
                hotelPricePerNight = primaryHotel.getEconomyPriceBdt() != null ? primaryHotel.getEconomyPriceBdt() : 2000L;
            } else if ("luxury".equalsIgnoreCase(request.getBudgetTier())) {
                hotelPricePerNight = primaryHotel.getLuxuryPriceBdt() != null ? primaryHotel.getLuxuryPriceBdt() : 10000L;
            } else {
                hotelPricePerNight = primaryHotel.getMidrangePriceBdt() != null ? primaryHotel.getMidrangePriceBdt() : 6000L;
            }
            accommodationTotal = hotelPricePerNight * dailyItineraries.size();
        } else {
            // Fallback if no hotels provided
            log.warn("⚠️ No hotel prices available, using default 6000 BDT/night");
            accommodationTotal = dailyItineraries.size() * 6000L;
        }

        long attractionTotal = dailyItineraries.stream()
            .flatMap(d -> d.getActivities().stream())
            .filter(a -> "Attraction".equals(a.getType()))
            .mapToLong(a -> a.getCostBdt() != null ? a.getCostBdt() : 0)
            .sum();

        long transportTotal = 500L;
        long miscTotal = 300L;
        long grandTotal = foodTotal + accommodationTotal + attractionTotal + transportTotal + miscTotal;

        BudgetBreakdownDto breakdown = new BudgetBreakdownDto();
        breakdown.setFood(new java.math.BigDecimal(foodTotal));
        breakdown.setAccommodation(new java.math.BigDecimal(accommodationTotal));
        breakdown.setAttractions(new java.math.BigDecimal(attractionTotal));
        breakdown.setTransport(new java.math.BigDecimal(transportTotal));
        breakdown.setMiscellaneous(new java.math.BigDecimal(miscTotal));
        breakdown.setTotal(new java.math.BigDecimal(grandTotal));

        // Verify total is set
        log.info("💰 ═══════════════════════════════════════════════════════════════");
        log.info("💰 BUDGET BREAKDOWN CALCULATED:");
        log.info("💰   ├─ Accommodation: ৳ {}", accommodationTotal);
        log.info("💰   ├─ Food & Dining: ৳ {}", foodTotal);
        log.info("💰   ├─ Attractions: ৳ {}", attractionTotal);
        log.info("💰   ├─ Transportation: ৳ {}", transportTotal);
        log.info("💰   ├─ Miscellaneous: ৳ {}", miscTotal);
        log.info("💰   └─ 🎯 GRAND TOTAL: ৳ {} ✅", grandTotal);
        log.info("💰 ═══════════════════════════════════════════════════════════════");

        return breakdown;
    }

    /**
     * Generate travel insights and tips
     */
    private TravelInsightsDTO generateTravelInsights(
            TravelPlan travelPlan,
            List<EnhancedDailyItineraryDTO> dailyItineraries,
            SmartPlanRequest request) {

        TravelInsightsDTO insights = new TravelInsightsDTO();

        if ("adventure".equalsIgnoreCase(request.getTravelStyle())) {
            insights.setHighlights(Arrays.asList(
                "Pack light, comfortable shoes for exploration",
                "Bring water bottles for outdoor activities",
                "Early mornings are best for most adventures"
            ));
        } else if ("family".equalsIgnoreCase(request.getTravelStyle())) {
            insights.setHighlights(Arrays.asList(
                "Family-friendly attractions included",
                "Rest periods planned for children",
                "Safe, accessible locations recommended"
            ));
        } else {
            insights.setHighlights(Arrays.asList(
                "Cultural experiences highlighted",
                "Local interactions encouraged",
                "Traditional dining recommended"
            ));
        }

        insights.setRecommendations(Arrays.asList(
            "Visit major attractions during off-peak hours",
            "Book restaurants in advance for dinner",
            "Use local transport for authentic experience"
        ));

        insights.setVisaInformation("No visa required for Bangladesh tourism");
        insights.setCurrencyInfo("Use BDT (Bangladeshi Taka). ATMs available in cities");
        insights.setBestTimeToVisit("October to March for comfortable weather");

        return insights;
    }

    // Helper methods
    private TravelPlan createBasePlan(SmartPlanRequest request, String userId) {
        log.info("💾 [DATABASE SAVE] Creating new TravelPlan entry for user: {}, destination: {}", userId, request.getDestination());
        TravelPlan plan = new TravelPlan();
        plan.setUserId(userId);
        plan.setDestination(request.getDestination());
        plan.setDurationDays(request.getDurationDays());
        plan.setBudgetTier(request.getBudgetTier());
        plan.setTravelStyle(request.getTravelStyle());
        TravelPlan saved = travelPlanRepository.save(plan);
        log.info("✅ [DATABASE SAVED] New plan saved with ID: {}", saved.getPlanId());
        return saved;
    }

    private EnhancedItineraryActivityDTO createActivity(
            String name, String type, String startTime, String endTime,
            String location, Double lat, Double lng,
            String additional, String notes) {

        EnhancedItineraryActivityDTO activity = new EnhancedItineraryActivityDTO();
        activity.setActivityName(name);
        activity.setType(type);
        activity.setStartTime(LocalTime.parse(startTime));
        activity.setEndTime(LocalTime.parse(endTime));
        activity.setLocation(location);
        activity.setLatitude(lat);
        activity.setLongitude(lng);
        activity.setNotes(notes);
        activity.setCostBdt(estimateCost(type, name));
        return activity;
    }

    private EnhancedItineraryActivityDTO createAttractionActivity(
            Attraction attraction, String startTime, String endTime) {

        EnhancedItineraryActivityDTO activity = new EnhancedItineraryActivityDTO();
        activity.setActivityName(attraction.getName());
        activity.setType("Attraction");
        activity.setDescription(attraction.getDescription());
        activity.setStartTime(LocalTime.parse(startTime));
        activity.setEndTime(LocalTime.parse(endTime));
        activity.setLocation(attraction.getName());
        activity.setLatitude(attraction.getLatitude());
        activity.setLongitude(attraction.getLongitude());
        activity.setCategory(attraction.getCategory());
        activity.setRating(attraction.getRating());
        activity.setCostBdt(attraction.getEntryFeeBdt());
        return activity;
    }

    private Attraction selectNextAttraction(
            List<Attraction> attractions, String travelStyle, String difficulty) {

        return attractions.stream()
            .filter(a -> travelStyle.equalsIgnoreCase(a.getTravelStyle()))
            .filter(a -> difficulty.equalsIgnoreCase(a.getDifficultyLevel()))
            .findFirst()
            .orElse(attractions.stream().findFirst().orElse(null));
    }

    /**
     * Select the next unused attraction, avoiding repetition
     * Ensures each day has unique attractions
     */
    private Attraction selectNextUnusedAttraction(
            List<Attraction> attractions, String travelStyle, String difficulty, Set<Long> usedIds) {

        // First, try to find attraction matching travel style and difficulty that hasn't been used
        return attractions.stream()
            .filter(a -> !usedIds.contains(a.getId()))
            .filter(a -> travelStyle.equalsIgnoreCase(a.getTravelStyle()))
            .filter(a -> difficulty.equalsIgnoreCase(a.getDifficultyLevel()))
            .findFirst()
            .orElse(
                // Fallback: find any unused attraction matching travel style
                attractions.stream()
                    .filter(a -> !usedIds.contains(a.getId()))
                    .filter(a -> travelStyle.equalsIgnoreCase(a.getTravelStyle()))
                    .findFirst()
                    .orElse(
                        // Last resort: any unused attraction
                        attractions.stream()
                            .filter(a -> !usedIds.contains(a.getId()))
                            .findFirst()
                            .orElse(null)
                    )
            );
    }

    /**
     * 🏆 SMART HYBRID ATTRACTION SELECTION 🏆
     * 
     * This algorithm prioritizes:
     * 1. Travel Style Match (What user wants)
     * 2. High Rating (Quality assurance)
     * 3. Category Diversity (Avoid boring repetition)
     * 4. No Duplicates (Fresh experiences)
     * 
     * Result: Users get personalized, varied, quality experiences that make them praise your app!
     */
    private Attraction selectNextSmartAttraction(
            List<Attraction> attractions,
            String travelStyle,
            String difficulty,
            Set<Long> usedIds,
            List<String> recentCategories,
            boolean prioritizeRating) {

        log.info("🎯 SMART HYBRID: Finding attraction (style={}, difficulty={}, categoryDiversity={})", 
            travelStyle, difficulty, recentCategories);

        // Step 1: Find candidates matching travel style and difficulty
        List<Attraction> primaryCandidates = attractions.stream()
            .filter(a -> !usedIds.contains(a.getId()))
            .filter(a -> travelStyle.equalsIgnoreCase(a.getTravelStyle()))
            .filter(a -> difficulty.equalsIgnoreCase(a.getDifficultyLevel()))
            .collect(Collectors.toList());

        // Step 2: If no primary candidates, expand to all attractions matching travel style
        if (primaryCandidates.isEmpty()) {
            primaryCandidates = attractions.stream()
                .filter(a -> !usedIds.contains(a.getId()))
                .filter(a -> travelStyle.equalsIgnoreCase(a.getTravelStyle()))
                .collect(Collectors.toList());
        }

        // Step 3: If still empty, get any unused attraction
        if (primaryCandidates.isEmpty()) {
            primaryCandidates = attractions.stream()
                .filter(a -> !usedIds.contains(a.getId()))
                .collect(Collectors.toList());
        }

        if (primaryCandidates.isEmpty()) {
            return null;
        }

        // Step 4: SCORE each candidate for variety and quality
        return primaryCandidates.stream()
            .sorted((a1, a2) -> {
                double score1 = calculateAttractionScore(a1, recentCategories, prioritizeRating);
                double score2 = calculateAttractionScore(a2, recentCategories, prioritizeRating);
                return Double.compare(score2, score1);  // Higher score first
            })
            .findFirst()
            .orElse(null);
    }

    /**
     * Calculate attraction score based on:
     * - Rating (quality) - primary factor
     * - Category diversity (avoid repetition) - secondary
     * - Difficulty match (personalization) - tertiary
     */
    private double calculateAttractionScore(
            Attraction attraction,
            List<String> recentCategories,
            boolean prioritizeRating) {

        double score = 0.0;

        // Base: Rating (0-4.8, scaled to 0-100)
        if (prioritizeRating) {
            score += (attraction.getRating() != null ? attraction.getRating() : 4.0) * 20;  // 0-96
        } else {
            score += (attraction.getRating() != null ? attraction.getRating() : 4.0) * 15;  // 0-72
        }

        // Bonus: Category Diversity (avoid same category in recent days)
        if (!recentCategories.isEmpty()) {
            // Check if this category was used in last 2 days
            if (recentCategories.stream().filter(c -> c.equalsIgnoreCase(attraction.getCategory())).count() == 0) {
                score += 25;  // Strong bonus for new category
            } else if (recentCategories.stream().filter(c -> c.equalsIgnoreCase(attraction.getCategory())).count() == 1) {
                score += 10;  // Small penalty for repeating category
            } else {
                score += 0;   // No bonus if already used twice
            }
        }

        // Bonus: Lesser-known but quality attractions
        if (attraction.getRating() != null && attraction.getRating() >= 4.4) {
            score += 5;  // Quality boost for highly-rated
        }

        log.debug("📊 Scoring attraction: {} = {} (rating: {}, category: {})",
            attraction.getName(), score, attraction.getRating(), attraction.getCategory());

        return score;
    }

    /**
     * Map destination name to ID
     */
    private Long mapDestinationToId(String destination) {
    if (destination == null) return 1L;
    
    return switch (destination.toLowerCase()) {
        case "dhaka" -> 1L;
        case "cox's bazar", "cox bazar" -> 2L;
        case "sylhet" -> 3L;                      
        case "chittagong", "chattogram" -> 4L;    
        case "khulna" -> 5L;
        case "bandarban" -> 6L;                   
        case "rajshahi" -> 1L;                    
        case "bogra" -> 1L;                       
        case "kuakata" -> 5L;                     
        default -> 1L;
    };
}
    
    private Restaurant selectBreakfastRestaurant(Long destinationId) {
        if (destinationId == null) return null;
        return restaurantRepository.findByDestinationId(destinationId).stream()
            .filter(r -> r.getVegetarianOptions() != null && r.getVegetarianOptions())
            .findFirst()
            .orElse(null);
    }

    private Restaurant selectLunchRestaurant(Long destinationId, String travelStyle) {
        if (destinationId == null) return null;
        return restaurantRepository.findByDestinationId(destinationId).stream().findFirst().orElse(null);
    }

    private Restaurant selectDinnerRestaurant(Long destinationId, String travelStyle) {
        if (destinationId == null) return null;
        return restaurantRepository.findByDestinationId(destinationId).stream().findFirst().orElse(null);
    }

    private String generateDayTheme(int day, SmartPlanRequest request) {
        String[] themes = {"Exploration Day", "Cultural Day", "Adventure Day", "Relaxation Day", "Discovery Day"};
        return themes[day % themes.length];
    }

    private String generateDayAdvisories(String travelStyle) {
        if ("adventure".equalsIgnoreCase(travelStyle)) {
            return "Wear comfortable shoes and bring water";
        }
        return "Have a great day exploring!";
    }

    private int calculateDailyCost(List<EnhancedItineraryActivityDTO> activities) {
        return activities.stream()
            .mapToInt(a -> a.getCostBdt() != null ? a.getCostBdt() : 0)
            .sum();
    }

    private int calculateHotelCost(SmartPlanRequest request) {
        if ("budget".equalsIgnoreCase(request.getBudgetTier())) return 2000;
        if ("luxury".equalsIgnoreCase(request.getBudgetTier())) return 10000;
        return 6000;
    }

    private int estimateCost(String type, String name) {
        if ("Dining".equals(type)) return 400;
        if ("Attraction".equals(type)) return 100;
        return 0;
    }

    private AttractionDTO convertToAttractionDTO(Attraction a) {
        return new AttractionDTO(
            a.getId(), a.getName(), a.getDescription(), a.getCategory(),
            a.getLatitude(), a.getLongitude(), a.getEstimatedDurationHours(),
            a.getEntryFeeBdt(), a.getBestTimeToVisit(), a.getRating(),
            a.getTravelStyle(), a.getDifficultyLevel()
        );
    }

    private HotelDTO convertToHotelDTO(Hotel h) {
        return new HotelDTO(
            h.getId(), h.getName(), h.getDescription(), h.getAddress(),
            h.getLatitude(), h.getLongitude(), h.getStarRating(),
            h.getPhone(), h.getEmail(), h.getWebsite(),
            h.getEconomyPriceBdt(), h.getMidrangePriceBdt(), h.getLuxuryPriceBdt(),
            h.getAmenities(), h.getAverageRating(), h.getReviewCount()
        );
    }

    private RestaurantDTO convertToRestaurantDTO(Restaurant r) {
        return new RestaurantDTO(
            r.getId(), r.getName(), r.getDescription(), r.getCuisineType(),
            r.getAddress(), r.getLatitude(), r.getLongitude(),
            r.getPriceRange(), r.getAverageMealCostBdt(), r.getOperatingHours(),
            r.getPhone(), r.getAverageRating(), r.getReviewCount(),
            r.getSpecialties(), r.getVegetarianOptions()
        );
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // COORDINATES HELPER CLASS
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    private static class Coordinates {
        private Double latitude;
        private Double longitude;

        public Coordinates(Double latitude, Double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public Double getLatitude() { 
            return latitude; 
        }

        public Double getLongitude() { 
            return longitude; 
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // LOCATION-AWARE ROUTE OPTIMIZATION & TRAVEL TIME CALCULATION
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * ⭐ Calculate realistic travel time between two locations
     * Using Haversine formula to get distance, then estimate time based on local speed
     * 
     * @param from Starting coordinates
     * @param to Destination coordinates
     * @return Travel time in hours
     */
    private double calculateTravelTime(Coordinates from, Coordinates to) {
        if (from == null || to == null || from.getLatitude() == null || to.getLatitude() == null) {
            return 0.5;  // Default 30 mins
        }

        double distanceKm = haversineDistance(from, to);
        
        // Local city speed: 30-40 km/h average (with traffic)
        // Highway speed: 60-80 km/h
        // Conservative estimate: 35 km/h average
        double averageSpeedKmH = 35.0;
        double travelTimeHours = distanceKm / averageSpeedKmH;

        log.debug("⏱️  Travel calculation: {:.1f} km / {:.0f} km/h = {:.2f} hours ({} mins)",
            distanceKm, averageSpeedKmH, travelTimeHours, Math.round(travelTimeHours * 60));

        return travelTimeHours;
    }

    /**
     * ⭐ Calculate distance between two coordinates using Haversine formula
     * @return Distance in kilometers
     */
    private double haversineDistance(Coordinates from, Coordinates to) {
        final int EARTH_RADIUS = 6371;  // Radius in km

        if (from.getLatitude() == null || from.getLongitude() == null ||
            to.getLatitude() == null || to.getLongitude() == null) {
            return 0;
        }

        double lat1 = Math.toRadians(from.getLatitude());
        double lat2 = Math.toRadians(to.getLatitude());
        double deltaLat = Math.toRadians(to.getLatitude() - from.getLatitude());
        double deltaLng = Math.toRadians(to.getLongitude() - from.getLongitude());

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(lat1) * Math.cos(lat2) *
                   Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS * c;

        return distance;
    }

    /**
     * ⭐ Find closest unvisited attraction to current location
     * Using distance-based + quality scoring
     * Now handles attractions with null coordinates by assigning defaults to the CORRECT destination
     */
    private Attraction findClosestAttraction(
            Coordinates currentLocation,
            List<Attraction> attractions,
            Set<Long> usedIds,
            String destinationName) {

        return attractions.stream()
            .filter(a -> !usedIds.contains(a.getId()))
            .peek(a -> {
                // Ensure attraction has coordinates
                if (a.getLatitude() == null || a.getLongitude() == null) {
                    // Use actual destination instead of hardcoded Cox's Bazar
                    a.setLatitude(getDefaultLatitude(destinationName));
                    a.setLongitude(getDefaultLongitude(destinationName));
                    log.warn("📍 Attraction '{}' had no coordinates, assigned default for {}", a.getName(), destinationName);
                }
            })
            .filter(a -> a.getLatitude() != null && a.getLongitude() != null)
            .sorted((a1, a2) -> {
                Coordinates loc1 = new Coordinates(a1.getLatitude(), a1.getLongitude());
                Coordinates loc2 = new Coordinates(a2.getLatitude(), a2.getLongitude());

                double dist1 = haversineDistance(currentLocation, loc1);
                double dist2 = haversineDistance(currentLocation, loc2);

                // Primary: Distance (closer is better)
                double distanceDiff = Double.compare(dist1, dist2);
                if (Math.abs(distanceDiff) > 0.1) {  // More than 100m difference
                    return (int) distanceDiff;
                }

                // Secondary: Rating (higher is better)
                double rating1 = a1.getRating() != null ? a1.getRating() : 3.0;
                double rating2 = a2.getRating() != null ? a2.getRating() : 3.0;
                return Double.compare(rating2, rating1);  // Higher rating first
            })
            .findFirst()
            .orElse(null);
    }

    /**
     * ⭐ Find nearest restaurant to current location
     * Now handles restaurants with null coordinates by assigning defaults
     */
    private Restaurant findNearestRestaurant(
            Coordinates currentLocation,
            List<Restaurant> restaurants) {

        return restaurants.stream()
            .peek(r -> {
                // Ensure restaurant has coordinates
                if (r.getLatitude() == null || r.getLongitude() == null) {
                    // Assign restaurant to current location (approximate)
                    r.setLatitude(currentLocation.getLatitude());
                    r.setLongitude(currentLocation.getLongitude());
                }
            })
            .filter(r -> r.getLatitude() != null && r.getLongitude() != null)
            .sorted((r1, r2) -> {
                Coordinates loc1 = new Coordinates(r1.getLatitude(), r1.getLongitude());
                Coordinates loc2 = new Coordinates(r2.getLatitude(), r2.getLongitude());

                double dist1 = haversineDistance(currentLocation, loc1);
                double dist2 = haversineDistance(currentLocation, loc2);

                return Double.compare(dist1, dist2);
            })
            .findFirst()
            .orElse(null);
    }

    /**
     * ⭐ Get primary hotel for route planning
     * Now more tolerant of missing coordinates (uses defaults if needed)
     */
    private Hotel getPrimaryHotel(Long destinationId, SmartPlanRequest request) {
        Long actualDestinationId = destinationId != null ? destinationId : 1L;
        List<Hotel> hotels;

        if ("budget".equalsIgnoreCase(request.getBudgetTier())) {
            hotels = hotelRepository.findHotelsByDestinationAndBudget(actualDestinationId, 3000);
        } else if ("luxury".equalsIgnoreCase(request.getBudgetTier())) {
            hotels = hotelRepository.findHotelsByDestinationAndStarRating(actualDestinationId, 5);
        } else {
            hotels = hotelRepository.findTopHotelsByDestination(actualDestinationId, 5);
        }

        if (hotels.isEmpty()) {
            log.warn("⚠️ No hotels found for destination ID: {}, will use default", actualDestinationId);
            return null;
        }

        // Return highest-rated hotel with coordinates (or without if needed)
        return hotels.stream()
            .sorted((h1, h2) -> Double.compare(
                h2.getAverageRating() != null ? h2.getAverageRating() : 0,
                h1.getAverageRating() != null ? h1.getAverageRating() : 0
            ))
            .findFirst()
            .orElse(null);
    }

    /**
     * ⭐ Get attractions available for route planning
     * Now includes attractions even without coordinates (will be filled in during scheduling)
     */
    private List<Attraction> getAvailableAttractionsForPlanning(
            Long destinationId, SmartPlanRequest request, Integer durationDays) {

        Long actualDestinationId = destinationId != null ? destinationId : 1L;
        List<Attraction> attractions = attractionRepository
            .findTopAttractionsByDestinationAndStyle(actualDestinationId, request.getTravelStyle());

        // Get more attractions than needed (filtering happens during route optimization)
        // No longer filtering by coordinates - we handle nulls during scheduling
        return attractions.stream()
            .limit(Math.max(durationDays * 4, 12))  // Need extras for optimization
            .collect(Collectors.toList());
    }

    /**
     * ⭐ Get restaurants available for planning
     * Now includes restaurants even without coordinates (will be filled in during scheduling)
     */
    private List<Restaurant> getAvailableRestaurantsForPlanning(
            Long destinationId, SmartPlanRequest request) {

        return restaurantRepository
            .findRestaurantsByDestinationAndPriceRange(
                destinationId,
                request.getBudgetTier()
            ).stream()
            // No longer filtering by coordinates - we handle nulls during scheduling
            .collect(Collectors.toList());
    }

    /**
     * ⭐ Format LocalTime to HH:mm string
     */
    private String formatTime(LocalTime time) {
        if (time == null) return "00:00";
        return String.format("%02d:%02d", time.getHour(), time.getMinute());
    }

    /**
     * ⭐ Format time string to LocalTime (for backward compatibility)
     */
    private LocalTime parseTime(String timeStr) {
        try {
            return LocalTime.parse(timeStr);
        } catch (Exception e) {
            return LocalTime.of(9, 0);
        }
    }

    /**
     * ⭐ Get default latitude for destination (fallback if hotel has no coordinates)
     */
    private Double getDefaultLatitude(String destination) {
        if (destination == null) return 23.8103;
        
        return switch (destination.toLowerCase()) {
            case "dhaka" -> 23.8103;
            case "cox's bazar", "cox bazar" -> 21.4272;
            case "chittagong", "chattogram" -> 22.3569;
            case "sylhet" -> 24.8949;
            case "khulna" -> 22.8456;
            case "rajshahi" -> 24.3736;
            case "bogra" -> 24.8500;
            case "bandarban" -> 21.9500;
            case "kuakata" -> 22.2919;
            default -> 23.8103;  // Default to Dhaka
        };
    }

    /**
     * ⭐ Get default longitude for destination (fallback if hotel has no coordinates)
     */
    private Double getDefaultLongitude(String destination) {
        if (destination == null) return 90.4125;
        
        return switch (destination.toLowerCase()) {
            case "dhaka" -> 90.4125;
            case "cox's bazar", "cox bazar" -> 91.9700;
            case "chittagong", "chattogram" -> 91.7832;
            case "sylhet" -> 91.8734;
            case "khulna" -> 89.5644;
            case "rajshahi" -> 88.5959;
            case "bogra" -> 89.6139;
            case "bandarban" -> 92.2400;
            case "kuakata" -> 91.9163;
            default -> 90.4125;  // Default to Dhaka
        };
    }
}
