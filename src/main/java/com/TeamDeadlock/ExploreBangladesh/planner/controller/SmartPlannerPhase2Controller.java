package com.TeamDeadlock.ExploreBangladesh.planner.controller;

import com.TeamDeadlock.ExploreBangladesh.planner.dto.*;
import com.TeamDeadlock.ExploreBangladesh.planner.entity.TravelPlan;
import com.TeamDeadlock.ExploreBangladesh.planner.entity.AIGeneratedTravelPlan;
import com.TeamDeadlock.ExploreBangladesh.planner.repository.*;
import com.TeamDeadlock.ExploreBangladesh.auth.repository.UserRepository;
import com.TeamDeadlock.ExploreBangladesh.planner.service.impl.SmartPlannerServiceImplV2;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Phase 2: Intelligent Itinerary Generation API
 * Provides endpoints for attractions, hotels, restaurants, and detailed planning
 */
@Slf4j
@RestController
@RequestMapping("/api/planner")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class SmartPlannerPhase2Controller {

    private final ObjectMapper objectMapper;
    private final AttractionRepository attractionRepository;
    private final HotelRepository hotelRepository;
    private final RestaurantRepository restaurantRepository;
    private final TransportRouteRepository transportRouteRepository;
    private final ActivityRecommendationRepository activityRecommendationRepository;
    private final TravelPlanRepository travelPlanRepository;
    private final AIGeneratedTravelPlanRepository aiGeneratedTravelPlanRepository;
    private final SmartPlannerServiceImplV2 smartPlannerServiceV2;
    private final UserRepository userRepository;

    /**
     * Generate intelligent travel plan PREVIEW (WITHOUT saving to database)
     * User can review before clicking "Save Plan" button
     * POST /api/planner/v2/generate
     * Note: Anonymous users (not logged in) can also generate previews
     */
    @PostMapping("/v2/generate")
    public ResponseEntity<?> generateSmartPlanV2(
            @Valid @RequestBody SmartPlanRequest request,
            Authentication authentication) {
        try {
            // Extract user ID from authentication (null for anonymous users)
            String userId = extractUserIdFromAuthentication(authentication);
            
            // Allow anonymous users - they can preview but cannot save
            if (userId != null) {
                log.info("🚀 [PLAN PREVIEW] User {} requesting Phase 2 smart plan for: {} (NOT SAVING YET)", userId, request.getDestination());
            } else {
                log.info("🚀 [PLAN PREVIEW - ANONYMOUS] Generating Phase 2 smart plan for: {} (NOT SAVING YET)", request.getDestination());
            }
            
            log.info("📋 Request details - Duration: {} days, Budget: {}, Style: {}", 
                request.getDurationDays(), request.getBudgetTier(), request.getTravelStyle());

            // Generate preview WITHOUT saving to database
            EnhancedTravelPlanDTO plan = smartPlannerServiceV2.generateSmartPlanPreview(request, userId);

            log.info("Plan preview ready - waiting for Save Plan button click");

            return ResponseEntity.ok(new ApiResponse<>(
                200,
                "Plan generated successfully. Click 'Save Plan' to save it to My Trips",
                plan
            ));
        } catch (Exception e) {
            log.error("Error generating Phase 2 smart plan", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                500,
                "Failed to generate plan: " + e.getMessage(),
                null
            ));
        }
    }

    /**
     * Save generated plan to database
     * Called when user clicks "Save Plan" button
     * POST /api/planner/v2/save
     */
    @PostMapping("/v2/save")
    public ResponseEntity<?> savePlanV2(
            @Valid @RequestBody SmartPlanRequest request,
            Authentication authentication) {
        try {
            // Extract actual user ID from authentication
            String userId = extractUserIdFromAuthentication(authentication);
            
            if (userId == null || userId.isEmpty()) {
                log.warn("❌ Could not extract user ID from authentication");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(
                    401,
                    "Unauthorized: Could not identify user",
                    null
                ));
            }
            
            log.info("💾 [SAVE PLAN] User {} saving Phase 2 smart plan: {}", userId, request.getDestination());

            // Save plan to database
            EnhancedTravelPlanDTO plan = smartPlannerServiceV2.savePlanToDB(request, userId);

            log.info("Plan ID: {} has been saved to database. User: {}", plan.getId(), userId);

            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(
                201,
                "Plan saved successfully and added to My Trips",
                plan
            ));
        } catch (Exception e) {
            log.error("Error saving Phase 2 smart plan", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                500,
                "Failed to save plan: " + e.getMessage(),
                null
            ));
        }
    }

    /**
     * Save AI-generated plan to AIGeneratedTravelPlan table
     * NEW endpoint for AI-generated plans (separate from manual plans)
     * Called when user clicks "Save This Plan to My Trips" button from AI engine
     * POST /api/planner/v2/ai/save
     * ✅ FIXED: Now accepts FULL plan data and stores without regenerating
     */
    @PostMapping("/v2/ai/save")
    public ResponseEntity<?> saveAIPlanV2(
            @RequestBody Map<String, Object> payload,
            Authentication authentication) {
        try {
            // Extract actual user ID from authentication
            String userId = extractUserIdFromAuthentication(authentication);
            
            if (userId == null || userId.isEmpty()) {
                log.warn("❌ Could not extract user ID from authentication");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(
                    401,
                    "Unauthorized: Could not identify user",
                    null
                ));
            }
            
            // Extract basic plan information from payload
            String destination = (String) payload.get("destination");
            Integer durationDays = payload.get("durationDays") instanceof Number 
                ? ((Number) payload.get("durationDays")).intValue() 
                : null;
            String budgetTier = (String) payload.get("budgetTier");
            String travelStyle = (String) payload.get("travelStyle");
            String startDate = (String) payload.get("startDate");
            
            log.info("💾 [SAVE AI PLAN] User {} saving AI-generated smart plan: {} ({} days)", 
                userId, destination, durationDays);

            // ✅ Extract FULL plan data (the complete AI response)
            Object fullPlanData = payload.get("fullPlanData");
            
            if (fullPlanData == null) {
                log.warn("⚠️ fullPlanData is null, will attempt save without full data");
            }
            
            // Create a SmartPlanRequest for basic info
            SmartPlanRequest request = new SmartPlanRequest();
            request.setDestination(destination);
            request.setDurationDays(durationDays);
            request.setBudgetTier(budgetTier);
            request.setTravelStyle(travelStyle);
            // ✅ FIXED: Convert String startDate to LocalDate
            if (startDate != null && !startDate.isEmpty()) {
                try {
                    request.setStartDate(LocalDate.parse(startDate));
                } catch (Exception e) {
                    log.warn("⚠️ Could not parse startDate: {}", startDate);
                    request.setStartDate(null);
                }
            }
            
            // ✅ Save AI plan to AIGeneratedTravelPlan table with FULL plan data
            EnhancedTravelPlanDTO plan = smartPlannerServiceV2.savePlanToAITable(request, userId, fullPlanData);

            log.info("✅ AI Plan ID: {} has been saved to AIGeneratedTravelPlan table. User: {}", plan.getId(), userId);

            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(
                201,
                "AI plan saved successfully and added to My Trips",
                plan
            ));
        } catch (Exception e) {
            log.error("Error saving AI-generated plan", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                500,
                "Failed to save AI plan: " + e.getMessage(),
                null
            ));
        }
    }

    /**
     * Get Phase 2 intelligent travel plan by ID
     * GET /api/planner/v2/{planId}
     * ✅ FIXED: Now checks both manual plans AND AI-generated plans
     * Regenerates full plan data from saved metadata
     */
    @GetMapping("/v2/{planId}")
    public ResponseEntity<?> getTravelPlanV2(
            @PathVariable Long planId,
            Authentication authentication) {
        try {
            String userId = extractUserIdFromAuthentication(authentication);
            
            if (userId == null || userId.isEmpty()) {
                log.warn("❌ Could not extract user ID from authentication");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(
                    401,
                    "Unauthorized: Could not identify user",
                    null
                ));
            }

            log.info("User {} retrieving travel plan: {}", userId, planId);

            // ✅ TRY AI-GENERATED PLANS FIRST (more specific)
            var aiGeneratedPlan = aiGeneratedTravelPlanRepository.findById(planId);
            if (aiGeneratedPlan.isPresent() && aiGeneratedPlan.get().getUserId().equals(userId)) {
                log.info("✅ Found AI-GENERATED plan {} for user {}", planId, userId);
                
                AIGeneratedTravelPlan savedAIPlan = aiGeneratedPlan.get();
                if (savedAIPlan.getPlanData() == null || savedAIPlan.getPlanData().isEmpty()) {
                    log.warn("⚠️ AI Plan saved but plan_data is empty for plan {}", planId);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                        500,
                        "AI plan data is missing",
                        null
                    ));
                }

                try {
                    // ✅ Return RAW JSON as Map (NO deserialization into Java DTO)
                    // Frontend will handle JSON parsing
                    Map<String, Object> rawPlanData = objectMapper.readValue(
                        savedAIPlan.getPlanData(),
                        Map.class
                    );
                    
                    log.info("📋 Successfully retrieved AI-GENERATED plan {} as raw JSON", planId);
                    return ResponseEntity.ok(new ApiResponse<>(
                        200,
                        "AI-generated travel plan retrieved successfully (raw format for frontend)",
                        rawPlanData
                    ));
                } catch (Exception e) {
                    log.error("Failed to parse AI-generated plan as JSON", e);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                        500,
                        "Failed to parse AI plan: " + e.getMessage(),
                        null
                    ));
                }
            }

            // ✅ TRY MANUAL PLANS SECOND
            var manualTravelPlan = travelPlanRepository.findById(planId);
            if (manualTravelPlan.isPresent() && manualTravelPlan.get().getUserId().equals(userId)) {
                log.info("✅ Found MANUAL plan {} for user {}", planId, userId);
                
                TravelPlan savedPlan = manualTravelPlan.get();
                if (savedPlan.getPlanData() == null || savedPlan.getPlanData().isEmpty()) {
                    log.warn("⚠️ Plan saved but plan_data is empty, falling back to regeneration");
                    // Fallback: regenerate if data is missing
                    EnhancedTravelPlanDTO planDTO = smartPlannerServiceV2.regeneratePlanDisplay(savedPlan, userId);
                    return ResponseEntity.ok(new ApiResponse<>(
                        200,
                        "Travel plan retrieved successfully",
                        planDTO
                    ));
                }

                try {
                    // Deserialize the saved plan from JSON
                    EnhancedTravelPlanDTO planDTO = objectMapper.readValue(
                        savedPlan.getPlanData(),
                        EnhancedTravelPlanDTO.class
                    );
                    
                    log.info("📋 Successfully retrieved saved MANUAL plan {}", planId);
                    return ResponseEntity.ok(new ApiResponse<>(
                        200,
                        "Travel plan retrieved successfully",
                        planDTO
                    ));
                } catch (Exception e) {
                    log.error("Failed to deserialize saved plan, attempting regeneration", e);
                    // Fallback: regenerate if deserialization fails
                    EnhancedTravelPlanDTO planDTO = smartPlannerServiceV2.regeneratePlanDisplay(savedPlan, userId);
                    return ResponseEntity.ok(new ApiResponse<>(
                        200,
                        "Travel plan retrieved successfully",
                        planDTO
                    ));
                }
            }

            // ❌ PLAN NOT FOUND IN EITHER TABLE
            log.warn("⚠️ Plan {} not found in either table or unauthorized access for user {}", planId, userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(
                404,
                "Travel plan not found",
                null
            ));
        } catch (Exception e) {
            log.error("Error retrieving travel plan", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                500,
                "Failed to retrieve plan: " + e.getMessage(),
                null
            ));
        }
    }

    /**
     * Get MANUAL travel plan by ID (specific endpoint)
     * GET /api/planner/v2/manual/{planId}
     * Only checks travel_plans table (manual plans)
     */
    @GetMapping("/v2/manual/{planId}")
    public ResponseEntity<?> getManualTravelPlanV2(
            @PathVariable Long planId,
            Authentication authentication) {
        try {
            String userId = extractUserIdFromAuthentication(authentication);
            
            if (userId == null || userId.isEmpty()) {
                log.warn("❌ Could not extract user ID from authentication");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(
                    401,
                    "Unauthorized: Could not identify user",
                    null
                ));
            }

            log.info("User {} retrieving MANUAL travel plan: {}", userId, planId);

            var manualTravelPlan = travelPlanRepository.findById(planId);
            if (manualTravelPlan.isEmpty() || !manualTravelPlan.get().getUserId().equals(userId)) {
                log.warn("⚠️ MANUAL plan {} not found or unauthorized access for user {}", planId, userId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(
                    404,
                    "Manual travel plan not found",
                    null
                ));
            }

            TravelPlan savedPlan = manualTravelPlan.get();
            if (savedPlan.getPlanData() == null || savedPlan.getPlanData().isEmpty()) {
                log.warn("⚠️ MANUAL plan saved but plan_data is empty, falling back to regeneration");
                EnhancedTravelPlanDTO planDTO = smartPlannerServiceV2.regeneratePlanDisplay(savedPlan, userId);
                return ResponseEntity.ok(new ApiResponse<>(
                    200,
                    "Manual travel plan retrieved successfully",
                    planDTO
                ));
            }

            try {
                EnhancedTravelPlanDTO planDTO = objectMapper.readValue(
                    savedPlan.getPlanData(),
                    EnhancedTravelPlanDTO.class
                );
                
                log.info("📋 Successfully retrieved MANUAL plan {}", planId);
                return ResponseEntity.ok(new ApiResponse<>(
                    200,
                    "Manual travel plan retrieved successfully",
                    planDTO
                ));
            } catch (Exception e) {
                log.error("Failed to deserialize MANUAL plan, attempting regeneration", e);
                EnhancedTravelPlanDTO planDTO = smartPlannerServiceV2.regeneratePlanDisplay(savedPlan, userId);
                return ResponseEntity.ok(new ApiResponse<>(
                    200,
                    "Manual travel plan retrieved successfully",
                    planDTO
                ));
            }
        } catch (Exception e) {
            log.error("Error retrieving manual travel plan", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                500,
                "Failed to retrieve manual plan: " + e.getMessage(),
                null
            ));
        }
    }

    /**
     * Get AI-GENERATED travel plan by ID (specific endpoint)
     * GET /api/planner/v2/ai/{planId}
     * Only checks ai_generated_travel_plans table (AI plans)
     */
    @GetMapping("/v2/ai/{planId}")
    public ResponseEntity<?> getAITravelPlanV2(
            @PathVariable Long planId,
            Authentication authentication) {
        try {
            String userId = extractUserIdFromAuthentication(authentication);
            
            if (userId == null || userId.isEmpty()) {
                log.warn("❌ Could not extract user ID from authentication");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(
                    401,
                    "Unauthorized: Could not identify user",
                    null
                ));
            }

            log.info("User {} retrieving AI-GENERATED travel plan: {}", userId, planId);

            var aiGeneratedPlan = aiGeneratedTravelPlanRepository.findById(planId);
            if (aiGeneratedPlan.isEmpty() || !aiGeneratedPlan.get().getUserId().equals(userId)) {
                log.warn("⚠️ AI plan {} not found or unauthorized access for user {}", planId, userId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(
                    404,
                    "AI-generated travel plan not found",
                    null
                ));
            }

            AIGeneratedTravelPlan savedAIPlan = aiGeneratedPlan.get();
            if (savedAIPlan.getPlanData() == null || savedAIPlan.getPlanData().isEmpty()) {
                log.warn("⚠️ AI plan saved but plan_data is empty for plan {}", planId);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                    500,
                    "AI plan data is missing",
                    null
                ));
            }

            try {
                // ✅ Return RAW JSON as Map (NO deserialization into Java DTO)
                // Frontend will handle JSON parsing
                Map<String, Object> rawPlanData = objectMapper.readValue(
                    savedAIPlan.getPlanData(),
                    Map.class
                );
                
                log.info("📋 Successfully retrieved AI-GENERATED plan {} as raw JSON", planId);
                return ResponseEntity.ok(new ApiResponse<>(
                    200,
                    "AI-generated travel plan retrieved successfully (raw format for frontend)",
                    rawPlanData
                ));
            } catch (Exception e) {
                log.error("Failed to parse AI-generated plan as JSON", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                    500,
                    "Failed to parse AI plan: " + e.getMessage(),
                    null
                ));
            }
        } catch (Exception e) {
            log.error("Error retrieving AI-generated travel plan", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                500,
                "Failed to retrieve AI plan: " + e.getMessage(),
                null
            ));
        }
    }

    /**
     * Get attractions for a specific destination
     */
    @GetMapping("/attractions/{destinationId}")
    public ResponseEntity<ApiResponse<List<AttractionDTO>>> getAttractions(
            @PathVariable Long destinationId) {

        log.info("Fetching attractions for destination: {}", destinationId);

        List<AttractionDTO> attractions = attractionRepository.findByDestinationId(destinationId)
            .stream()
            .map(this::convertToAttractionDTO)
            .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse<>(
            200,
            "Attractions retrieved successfully",
            attractions
        ));
    }

    /**
     * Get attractions by travel style
     */
    @GetMapping("/attractions/{destinationId}/style/{travelStyle}")
    public ResponseEntity<ApiResponse<List<AttractionDTO>>> getAttractionsByStyle(
            @PathVariable Long destinationId,
            @PathVariable String travelStyle) {

        log.info("Fetching {} attractions for destination: {}", travelStyle, destinationId);

        List<AttractionDTO> attractions = attractionRepository
            .findByDestinationIdAndTravelStyle(destinationId, travelStyle)
            .stream()
            .map(this::convertToAttractionDTO)
            .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse<>(
            200,
            "Attractions filtered by travel style",
            attractions
        ));
    }

    /**
     * Get hotels for a destination
     */
    @GetMapping("/hotels/{destinationId}")
    public ResponseEntity<ApiResponse<List<HotelDTO>>> getHotels(
            @PathVariable Long destinationId) {

        log.info("Fetching hotels for destination: {}", destinationId);

        List<HotelDTO> hotels = hotelRepository.findByDestinationId(destinationId)
            .stream()
            .map(this::convertToHotelDTO)
            .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse<>(
            200,
            "Hotels retrieved successfully",
            hotels
        ));
    }

    /**
     * Get hotels by budget tier
     */
    @GetMapping("/hotels/{destinationId}/tier/{budgetTier}")
    public ResponseEntity<ApiResponse<List<HotelDTO>>> getHotelsByBudget(
            @PathVariable Long destinationId,
            @PathVariable String budgetTier) {

        log.info("Fetching {} tier hotels for destination: {}", budgetTier, destinationId);

        List<HotelDTO> hotels;
        if ("budget".equalsIgnoreCase(budgetTier)) {
            hotels = hotelRepository.findHotelsByDestinationAndBudget(destinationId, 3000)
                .stream()
                .map(this::convertToHotelDTO)
                .collect(Collectors.toList());
        } else if ("luxury".equalsIgnoreCase(budgetTier)) {
            hotels = hotelRepository.findTopHotelsByDestination(destinationId, 10)
                .stream()
                .map(this::convertToHotelDTO)
                .collect(Collectors.toList());
        } else {
            hotels = hotelRepository.findTopHotelsByDestination(destinationId, 8)
                .stream()
                .map(this::convertToHotelDTO)
                .collect(Collectors.toList());
        }

        return ResponseEntity.ok(new ApiResponse<>(
            200,
            "Hotels filtered by budget tier",
            hotels
        ));
    }

    /**
     * Get restaurants for a destination
     */
    @GetMapping("/restaurants/{destinationId}")
    public ResponseEntity<ApiResponse<List<RestaurantDTO>>> getRestaurants(
            @PathVariable Long destinationId) {

        log.info("Fetching restaurants for destination: {}", destinationId);

        List<RestaurantDTO> restaurants = restaurantRepository.findByDestinationId(destinationId)
            .stream()
            .map(this::convertToRestaurantDTO)
            .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse<>(
            200,
            "Restaurants retrieved successfully",
            restaurants
        ));
    }

    /**
     * Get restaurants by cuisine type
     */
    @GetMapping("/restaurants/{destinationId}/cuisine/{cuisineType}")
    public ResponseEntity<ApiResponse<List<RestaurantDTO>>> getRestaurantsByCuisine(
            @PathVariable Long destinationId,
            @PathVariable String cuisineType) {

        log.info("Fetching {} restaurants for destination: {}", cuisineType, destinationId);

        List<RestaurantDTO> restaurants = restaurantRepository
            .findByDestinationIdAndCuisineType(destinationId, cuisineType)
            .stream()
            .map(this::convertToRestaurantDTO)
            .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse<>(
            200,
            "Restaurants filtered by cuisine",
            restaurants
        ));
    }

    /**
     * Get vegetarian restaurants
     */
    @GetMapping("/restaurants/{destinationId}/vegetarian")
    public ResponseEntity<ApiResponse<List<RestaurantDTO>>> getVegetarianRestaurants(
            @PathVariable Long destinationId) {

        log.info("Fetching vegetarian restaurants for destination: {}", destinationId);

        List<RestaurantDTO> restaurants = restaurantRepository
            .findVegetarianRestaurantsByDestination(destinationId)
            .stream()
            .map(this::convertToRestaurantDTO)
            .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse<>(
            200,
            "Vegetarian restaurants retrieved",
            restaurants
        ));
    }

    /**
     * Get transport routes between destinations
     */
    @GetMapping("/transport/{sourceId}/{targetId}")
    public ResponseEntity<ApiResponse<List<TransportRouteDTO>>> getTransportRoutes(
            @PathVariable Long sourceId,
            @PathVariable Long targetId) {

        log.info("Fetching transport routes from {} to {}", sourceId, targetId);

        List<TransportRouteDTO> routes = transportRouteRepository
            .findRoutesBetweenDestinations(sourceId, targetId)
            .stream()
            .map(this::convertToTransportRouteDTO)
            .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse<>(
            200,
            "Transport routes retrieved successfully",
            routes
        ));
    }

    /**
     * Get cheapest transport route between destinations
     */
    @GetMapping("/transport/{sourceId}/{targetId}/cheapest")
    public ResponseEntity<ApiResponse<TransportRouteDTO>> getCheapestRoute(
            @PathVariable Long sourceId,
            @PathVariable Long targetId) {

        log.info("Fetching cheapest route from {} to {}", sourceId, targetId);

        TransportRouteDTO route = transportRouteRepository
            .findCheapestRoutes(sourceId, targetId)
            .stream()
            .findFirst()
            .map(this::convertToTransportRouteDTO)
            .orElse(null);

        return ResponseEntity.ok(new ApiResponse<>(
            200,
            "Cheapest route retrieved",
            route
        ));
    }

    /**
     * Get fastest transport route between destinations
     */
    @GetMapping("/transport/{sourceId}/{targetId}/fastest")
    public ResponseEntity<ApiResponse<TransportRouteDTO>> getFastestRoute(
            @PathVariable Long sourceId,
            @PathVariable Long targetId) {

        log.info("Fetching fastest route from {} to {}", sourceId, targetId);

        TransportRouteDTO route = transportRouteRepository
            .findFastestRoutes(sourceId, targetId)
            .stream()
            .findFirst()
            .map(this::convertToTransportRouteDTO)
            .orElse(null);

        return ResponseEntity.ok(new ApiResponse<>(
            200,
            "Fastest route retrieved",
            route
        ));
    }

    /**
     * Get activity recommendations for an attraction
     */
    @GetMapping("/activity-recommendations/{attractionId}")
    public ResponseEntity<ApiResponse<List<ActivityRecommendationDTO>>> getActivityRecommendations(
            @PathVariable Long attractionId) {

        log.info("Fetching activity recommendations for attraction: {}", attractionId);

        List<ActivityRecommendationDTO> recommendations = activityRecommendationRepository
            .findByAttractionId(attractionId)
            .stream()
            .map(this::convertToActivityRecommendationDTO)
            .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse<>(
            200,
            "Activity recommendations retrieved",
            recommendations
        ));
    }

    /**
     * Get plan budget estimate
     */
    @GetMapping("/budget-estimate/{planId}")
    public ResponseEntity<ApiResponse<BudgetBreakdownDto>> getBudgetEstimate(
            @PathVariable Long planId) {

        log.info("Fetching budget estimate for plan: {}", planId);

        var plan = travelPlanRepository.findById(planId);
        if (plan.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        BudgetBreakdownDto breakdown = new BudgetBreakdownDto();
        long accommodationTotal = 6000L * plan.get().getDurationDays();
        long foodTotal = 1200L * plan.get().getDurationDays();
        long attractionTotal = 500L * (plan.get().getDurationDays() - 1);
        long transportTotal = 500L;
        long miscTotal = 300L;

        breakdown.setAccommodation(new java.math.BigDecimal(accommodationTotal));
        breakdown.setFood(new java.math.BigDecimal(foodTotal));
        breakdown.setAttractions(new java.math.BigDecimal(attractionTotal));
        breakdown.setTransport(new java.math.BigDecimal(transportTotal));
        breakdown.setMiscellaneous(new java.math.BigDecimal(miscTotal));
        breakdown.setTotal(new java.math.BigDecimal(
            accommodationTotal + foodTotal + attractionTotal + transportTotal + miscTotal
        ));

        return ResponseEntity.ok(new ApiResponse<>(
            200,
            "Budget estimate retrieved",
            breakdown
        ));
    }

    // Helper conversion methods
    
    private AttractionDTO convertToAttractionDTO(com.TeamDeadlock.ExploreBangladesh.planner.entity.Attraction a) {
        return new AttractionDTO(
            a.getId(), a.getName(), a.getDescription(), a.getCategory(),
            a.getLatitude(), a.getLongitude(), a.getEstimatedDurationHours(),
            a.getEntryFeeBdt(), a.getBestTimeToVisit(), a.getRating(),
            a.getTravelStyle(), a.getDifficultyLevel()
        );
    }

    private HotelDTO convertToHotelDTO(com.TeamDeadlock.ExploreBangladesh.planner.entity.Hotel h) {
        return new HotelDTO(
            h.getId(), h.getName(), h.getDescription(), h.getAddress(),
            h.getLatitude(), h.getLongitude(), h.getStarRating(),
            h.getPhone(), h.getEmail(), h.getWebsite(),
            h.getEconomyPriceBdt(), h.getMidrangePriceBdt(), h.getLuxuryPriceBdt(),
            h.getAmenities(), h.getAverageRating(), h.getReviewCount()
        );
    }

    private RestaurantDTO convertToRestaurantDTO(com.TeamDeadlock.ExploreBangladesh.planner.entity.Restaurant r) {
        return new RestaurantDTO(
            r.getId(), r.getName(), r.getDescription(), r.getCuisineType(),
            r.getAddress(), r.getLatitude(), r.getLongitude(),
            r.getPriceRange(), r.getAverageMealCostBdt(), r.getOperatingHours(),
            r.getPhone(), r.getAverageRating(), r.getReviewCount(),
            r.getSpecialties(), r.getVegetarianOptions()
        );
    }

    private TransportRouteDTO convertToTransportRouteDTO(com.TeamDeadlock.ExploreBangladesh.planner.entity.TransportRoute t) {
        return new TransportRouteDTO(
            t.getId(), t.getSourceDestinationId(), t.getTargetDestinationId(),
            t.getTransportType(), t.getDistanceKm(), t.getTravelTimeHours(),
            t.getCostEconomyBdt(), t.getCostMidrangeBdt(), t.getCostLuxuryBdt(),
            t.getNotes()
        );
    }

    private ActivityRecommendationDTO convertToActivityRecommendationDTO(
            com.TeamDeadlock.ExploreBangladesh.planner.entity.ActivityRecommendation ar) {
        return new ActivityRecommendationDTO(
            ar.getId(), ar.getAttractionId(), ar.getTravelStyle(),
            ar.getDurationHours(), ar.getRecommendedTimeSlot(),
            ar.getCostEstimationBdt(), ar.getSuitableForAgeGroup()
        );
    }

    /**
     * DELETE /api/planner/v2/manual/{planId}
     * Delete a MANUAL travel plan
     */
    @DeleteMapping("/v2/manual/{planId}")
    public ResponseEntity<?> deleteManualTravelPlanV2(
            @PathVariable Long planId,
            Authentication authentication) {
        try {
            String userId = extractUserIdFromAuthentication(authentication);
            
            if (userId == null || userId.isEmpty()) {
                log.warn("❌ Could not extract user ID from authentication");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(
                    401,
                    "Unauthorized: Could not identify user",
                    null
                ));
            }

            log.info("User {} deleting MANUAL travel plan: {}", userId, planId);

            var manualTravelPlan = travelPlanRepository.findById(planId);
            if (manualTravelPlan.isEmpty() || !manualTravelPlan.get().getUserId().equals(userId)) {
                log.warn("⚠️ MANUAL plan {} not found or unauthorized access for user {}", planId, userId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(
                    404,
                    "Manual travel plan not found",
                    null
                ));
            }

            travelPlanRepository.deleteById(planId);
            log.info("✅ Deleted MANUAL plan {} for user {}", planId, userId);
            
            return ResponseEntity.ok(new ApiResponse<>(
                200,
                "Manual travel plan deleted successfully",
                null
            ));
        } catch (Exception e) {
            log.error("Error deleting manual travel plan", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                500,
                "Failed to delete manual plan: " + e.getMessage(),
                null
            ));
        }
    }

    /**
     * DELETE /api/planner/v2/ai/{planId}
     * Delete an AI-GENERATED travel plan
     */
    @DeleteMapping("/v2/ai/{planId}")
    public ResponseEntity<?> deleteAITravelPlanV2(
            @PathVariable Long planId,
            Authentication authentication) {
        try {
            String userId = extractUserIdFromAuthentication(authentication);
            
            if (userId == null || userId.isEmpty()) {
                log.warn("❌ Could not extract user ID from authentication");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(
                    401,
                    "Unauthorized: Could not identify user",
                    null
                ));
            }

            log.info("User {} deleting AI-GENERATED travel plan: {}", userId, planId);

            var aiGeneratedPlan = aiGeneratedTravelPlanRepository.findById(planId);
            if (aiGeneratedPlan.isEmpty() || !aiGeneratedPlan.get().getUserId().equals(userId)) {
                log.warn("⚠️ AI plan {} not found or unauthorized access for user {}", planId, userId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(
                    404,
                    "AI-generated travel plan not found",
                    null
                ));
            }

            aiGeneratedTravelPlanRepository.deleteById(planId);
            log.info("✅ Deleted AI-GENERATED plan {} for user {}", planId, userId);
            
            return ResponseEntity.ok(new ApiResponse<>(
                200,
                "AI-generated travel plan deleted successfully",
                null
            ));
        } catch (Exception e) {
            log.error("Error deleting AI-generated travel plan", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                500,
                "Failed to delete AI plan: " + e.getMessage(),
                null
            ));
        }
    }


    /**
     * Helper method to extract user ID from authentication
     * Extracts email from authentication principal and looks up user UUID
     */
    private String extractUserIdFromAuthentication(Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return null;
            }
            
            // authentication.getName() returns the email (set by JwtAuthenticationFilter)
            String email = authentication.getName();
            if (email == null || email.isEmpty()) {
                return null;
            }
            
            // Look up user by email and get their UUID
            var user = userRepository.findByEmail(email);
            if (user.isPresent()) {
                return user.get().getId().toString();
            }
            
            return null;
        } catch (Exception e) {
            log.error("Error extracting user ID from authentication", e);
            return null;
        }
    }

    /**
     * Generic API response wrapper
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class ApiResponse<T> {
        private int status;
        private String message;
        private T data;
    }
}
