package com.TeamDeadlock.ExploreBangladesh.planner.service.impl;

import com.TeamDeadlock.ExploreBangladesh.planner.dto.*;
import com.TeamDeadlock.ExploreBangladesh.planner.service.AIEngineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.time.LocalTime;
import java.math.BigDecimal;
import java.util.*;

/**
 * AI Engine Service - Intelligent Independent Itinerary Generator
 * 
 * Generates intelligent itineraries using Google Gemini AI with capabilities including:
 * - Hourly activity breakdown and scheduling
 * - AI-curated attraction selections
 * - Smart meal planning with restaurant recommendations
 * - Realistic travel time estimates
 * - Budget-conscious recommendations
 * - Cultural insights and local tips
 * - Error handling with detailed logging
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIEngineServiceImpl implements AIEngineService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.ai.google.genai.api-key:}")
    private String googleGenAIApiKey;

    private static final String GOOGLE_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";
    
    // Destination-based attractions (no database needed - pure config)
    private static final Map<String, List<String>> DESTINATION_ATTRACTIONS = buildAttractionMap();

    private static Map<String, List<String>> buildAttractionMap() {
        Map<String, List<String>> map = new HashMap<>();
        
        // DHAKA
        map.put("dhaka", Arrays.asList(
            "Lalbagh Fort (4.5, 2h, 100 BDT) - Historical",
            "Ahsan Manzil (4.4, 1.5h, 100 BDT) - Historical",
            "National Museum (4.3, 2.5h, 50 BDT) - Museum",
            "Star Mosque (4.2, 1h, Free) - Religious",
            "Bangladesh National Zoo (4.1, 3h, 100 BDT) - Wildlife",
            "Hatirjheel Lake (4.3, 1.5h, Free) - Recreation",
            "Old Dhaka Walking Tour (4.4, 2h, Free) - Cultural",
            "Sundarbans National Park (4.6, 1 day, 2500 BDT) - Nature",
            "Buriganga River Cruise (4.2, 2h, 300 BDT) - Recreation"
        ));
        
        // COX'S BAZAR
        map.put("cox's bazar", Arrays.asList(
            "Cox's Bazar Beach (4.7, Flexible, Free) - Beach",
            "Himchari Waterfall (4.5, 2h, 50 BDT) - Nature",
            "Inani Beach (4.4, 2h, Free) - Beach",
            "Maheshkhali Island (4.3, 4h, 200 BDT) - Island",
            "Saint Martin Island (4.6, 1 day, 800 BDT) - Beach",
            "Ramu Buddhist Temple (4.3, 1.5h, Free) - Religious",
            "Sundarban Tour (4.5, 1 day, 3000 BDT) - Nature"
        ));
        
        // SYLHET
        map.put("sylhet", Arrays.asList(
            "Jaflong (4.6, 3h, Free) - Nature",
            "Ratargul Swamp Forest (4.5, 2h, 150 BDT) - Forest",
            "Lalakhal (4.4, 1.5h, Free) - Lake",
            "Bisnakandi (4.5, 2h, Free) - Springs",
            "Khasi Tribe Village (4.4, 2h, Free) - Cultural",
            "Tamabil Tea Garden (4.3, 2.5h, Free) - Nature",
            "Srimangal (4.5, 1 day, Free) - Tea Gardens"
        ));
        
        // CHITTAGONG
        map.put("chittagong", Arrays.asList(
            "Six Domed Mosque (4.4, 1.5h, Free) - Religious",
            "Foy's Lake (4.3, 1.5h, Free) - Recreation",
            "Chittagong Zoo (4.2, 3h, 100 BDT) - Wildlife",
            "Patenga Beach (4.5, 2h, Free) - Beach",
            "War Cemetery (4.2, 1h, Free) - Historical",
            "Rajshahi Silk Factory (4.5, 2h, Free) - Cultural"
        ));
        
        // KHULNA
        map.put("khulna", Arrays.asList(
            "Sundarbans National Park (4.6, 1 day, 2500 BDT) - Nature",
            "Khulna Museum (4.2, 2h, 50 BDT) - Museum",
            "Nine Domed Mosque (4.3, 1h, Free) - Religious",
            "Shait Gumbaz Mosque (4.4, 1h, Free) - Religious"
        ));
        
        // RAJSHAHI
        map.put("rajshahi", Arrays.asList(
            "Rajshahi Silk Museum (4.5, 2h, 100 BDT) - Museum",
            "Varendra Museum (4.3, 2h, 50 BDT) - Museum",
            "Padma River Tour (4.4, 2h, Free) - Recreation",
            "Puthia (4.5, 3h, Free) - Historical"
        ));
        
        // RANGPUR
        map.put("rangpur", Arrays.asList(
            "Rangpur Fort (4.3, 2h, 50 BDT) - Historical",
            "Curzon Hall (4.2, 1h, Free) - Historical",
            "Rangpur Museum (4.4, 1.5h, Free) - Museum"
        ));
        
        // KUAKATA
        map.put("kuakata", Arrays.asList(
            "Kuakata Beach (4.6, Flexible, Free) - Beach",
            "Sudkanya Beach (4.5, 2h, Free) - Beach",
            "Laboni Point (4.4, 1.5h, Free) - Beach",
            "Fisherman Village (4.3, 2h, Free) - Cultural",
            "Buddhist Temple (4.3, 1h, Free) - Religious",
            "Hill Tracks (4.4, 3h, Free) - Nature",
            "Saint Martin Island (4.5, 1 day, 800 BDT) - Island"
        ));
        
        // BARISAL
        map.put("barisal", Arrays.asList(
            "Barisal Floating Market (4.5, 3h, Free) - Market",
            "Sundarbans Tour (4.6, 1 day, 2500 BDT) - Nature",
            "Swat River (4.4, 2h, Free) - River",
            "Kirtonkhola River (4.3, 2h, Free) - River",
            "Tentulia Bridge (4.4, 1.5h, Free) - Historical"
        ));
        
        // MYMENSINGH
        map.put("mymensingh", Arrays.asList(
            "Botanical Garden (4.4, 2.5h, 50 BDT) - Garden",
            "Mymensingh Agricultural University (4.3, 2h, Free) - Educational",
            "Shoshimpur Mosque (4.3, 1h, Free) - Religious",
            "Kewzar Lake (4.4, 1.5h, Free) - Recreation"
        ));
        
        return map;
    }

    /**
     * Get formatted attractions for a specific destination
     */
    private String getFormattedAttractionsForDestination(String destination) {
        String key = destination.toLowerCase().trim();
        
        // Get attractions for this destination, or return generic list
        List<String> attractions = DESTINATION_ATTRACTIONS.getOrDefault(key, 
            DESTINATION_ATTRACTIONS.getOrDefault("dhaka", new ArrayList<>()));
        
        if (attractions.isEmpty()) {
            // Return generic format if destination not found
            return String.format("""
                %s:
                - Historic or Cultural Attraction (Rating: 4.4, Entry: 100-200 BDT, Duration: 2h)
                - Nature or Recreation Site (Rating: 4.5, Entry: Free-100 BDT, Duration: 2-3h)
                - Beach or Mountain (Rating: 4.6, Entry: Free, Duration: 3-4h)
                """, destination);
        }
        
        // Format attractions nicely
        StringBuilder sb = new StringBuilder();
        sb.append(destination.toUpperCase()).append(":\n");
        for (String attraction : attractions) {
            sb.append("- ").append(attraction).append("\n");
        }
        return sb.toString();
    }

    /**
     * Generate AI-powered intelligent travel plan
     * 
     * @param request User's travel request (destination, duration, budget, style)
     * @param userId User identifier
     * @return AI-generated travel plan with hourly breakdown
     */
    @Override
    public AIGeneratedPlanDTO generateAIPoweredPlan(SmartPlanRequest request, String userId) {
        log.info("Starting intelligent plan generation for user: {} - destination: {} ({} days, {} budget)",
                userId, request.getDestination(), request.getDurationDays(), request.getBudgetTier());

        try {
            // Validate API key first
            if (googleGenAIApiKey == null || googleGenAIApiKey.trim().isEmpty()) {
                String error = "Google Gemini API key not configured in application.properties";
                log.error("AI engine failed due to: {}", error);
                throw new RuntimeException("AI engine failed due to: " + error);
            }

            // Step 1: Build intelligent prompt
            String prompt = buildIntelligentPrompt(request);

            // Step 2: Call Gemini API
            log.info("Calling Google Gemini API...");
            String aiResponse = callGoogleGenAIAPI(prompt);

            // Step 3: Parse response into structured DTOs
            log.info("Parsing AI response...");
            AIGeneratedPlanDTO plan = parseAIResponse(aiResponse, request, userId);

            log.info("Plan generation completed successfully! Plan ID: {}", plan.getPlanId());
            return plan;

        } catch (IllegalArgumentException e) {
            String errorMsg = "AI engine failed due to: Invalid input - " + e.getMessage();
            log.error("{}", errorMsg);
            throw new RuntimeException(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "AI engine failed due to: " + e.getMessage();
            log.error("{}", errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * Build intelligent prompt for Gemini API with dynamic attractions
     */
    private String buildIntelligentPrompt(SmartPlanRequest request) {
        String destination = request.getDestination();
        String attractions = getFormattedAttractionsForDestination(destination);
        
        return String.format("""
            You are an expert travel consultant specializing in Bangladesh tourism. Your task is to create 
            a COMPLETE, DETAILED, and INTELLIGENT %d-day travel itinerary for %s with hourly breakdown.
            
            ═══════════════════════════════════════════════════════════════════════════════════════
            USER PROFILE:
            ═══════════════════════════════════════════════════════════════════════════════════════
            • Destination: %s
            • Duration: %d days
            • Budget Tier: %s
            • Travel Style: %s
            
            ═══════════════════════════════════════════════════════════════════════════════════════
            DAILY SCHEDULE TEMPLATE (MUST FOLLOW FOR EVERY DAY):
            ═══════════════════════════════════════════════════════════════════════════════════════
            08:00-09:00 | Breakfast at hotel
            09:00-12:00 | Morning activity (includes 15-30 min travel time)
            12:00-13:30 | Lunch at restaurant
            13:30-17:30 | Afternoon activities (1-2 attractions max)
            17:30-18:30 | Return to hotel / rest
            19:00-20:30 | Dinner at restaurant
            20:30-22:00 | Evening relaxation / shopping
            
            ═══════════════════════════════════════════════════════════════════════════════════════
            AVAILABLE ATTRACTIONS (Choose from these - NO duplicates across days):
            ═══════════════════════════════════════════════════════════════════════════════════════
            %s
            
            ═══════════════════════════════════════════════════════════════════════════════════════
            CRITICAL RULES:
            ═══════════════════════════════════════════════════════════════════════════════════════
            1. NO duplicate attractions across different days
            2. Return to hotel by 18:00 every day WITHOUT FAIL
            3. Times in 24-hour format (08:00, 14:30, 19:45)
            4. All costs in BDT currency
            5. Realistic travel times: 15-45 minutes between locations
            6. Budget tier allocation:
               - Budget: 2,000-3,500 BDT/day total
               - Midrange: 5,000-7,500 BDT/day total
               - Luxury: 10,000+ BDT/day total
            
            ═══════════════════════════════════════════════════════════════════════════════════════
            RETURN ONLY VALID JSON (no markdown, no code blocks):
            ═══════════════════════════════════════════════════════════════════════════════════════
            
            {
              "destination": "%s",
              "durationDays": %d,
              "budgetTier": "%s",
              "travelStyle": "%s",
              "estimatedBudget": %d,
              "aiInsights": "2-3 paragraph analysis of %s",
              "weatherInfo": "Expected weather conditions during your visit",
              "bestTimeToVisit": "Best months to visit",
              "accommodations": "Hotel and lodging recommendations",
              "transportationTips": "How to get around the city",
              "localTravelTips": "Local customs and culture tips",
              "packingTips": ["Tip 1", "Tip 2", "Tip 3"],
              "safetyTips": ["Safety 1", "Safety 2"],
              "recommendedAttractions": ["Must-see attraction 1", "Must-see attraction 2"],
              "recommendedRestaurants": ["Best restaurant 1", "Best restaurant 2"],
              "dailyItineraries": [
                {
                  "dayNumber": 1,
                  "date": "%s",
                  "dayTitle": "Day 1 Title",
                  "summary": "Brief overview of Day 1",
                  "mealPlan": "Breakfast at X → Lunch at Y → Dinner at Z",
                  "travelTips": "Specific transport tips for Day 1",
                  "weatherExpectation": "Expected weather",
                  "packingReminders": ["Item 1", "Item 2"],
                  "estimatedDailyBudget": %d,
                  "activities": [
                    {
                      "startTime": "08:00",
                      "endTime": "09:00",
                      "activityName": "Breakfast at Hotel",
                      "activityType": "meal",
                      "location": "Hotel Name",
                      "description": "Hotel breakfast with local options",
                      "estimatedCost": 0,
                      "duration": "1 hour",
                      "travelTime": "0 minutes",
                      "recommendations": "Try local dishes",
                      "bookingInfo": "Included with hotel",
                      "dressCode": "Casual"
                    }
                  ]
                }
              ]
            }
            """,
            request.getDurationDays(),
            destination,
            destination,
            request.getDurationDays(),
            request.getBudgetTier(),
            request.getTravelStyle(),
            attractions,
            destination,
            request.getDurationDays(),
            request.getBudgetTier(),
            request.getTravelStyle(),
            calculateEstimatedBudget(request.getBudgetTier(), request.getDurationDays()),
            destination,
            (request.getStartDate() != null ? request.getStartDate() : LocalDate.now()).toString(),
            calculateEstimatedBudget(request.getBudgetTier(), 1)
        );
    }

    /**
     * Call Google Gemini API
     */
    private String callGoogleGenAIAPI(String prompt) throws Exception {
        String url = GOOGLE_API_URL + "?key=" + googleGenAIApiKey;

        Map<String, Object> requestBody = new HashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();
        
        Map<String, Object> content = new HashMap<>();
        List<Map<String, Object>> parts = new ArrayList<>();
        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);
        parts.add(part);
        content.put("parts", parts);
        contents.add(content);
        
        requestBody.put("contents", contents);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(url, requestBody, Map.class);

            if (response == null) {
                throw new RuntimeException("Empty response from Google Gemini API");
            }

            if (response.containsKey("error")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> error = (Map<String, Object>) response.get("error");
                String errorMsg = (String) error.getOrDefault("message", "Unknown API error");
                throw new RuntimeException("Google API Error: " + errorMsg);
            }

            if (!response.containsKey("candidates")) {
                throw new RuntimeException("No candidates in Google API response");
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                throw new RuntimeException("Empty candidates list from Google API");
            }

            Map<String, Object> candidate = candidates.get(0);
            @SuppressWarnings("unchecked")
            Map<String, Object> contentMap = (Map<String, Object>) candidate.get("content");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> partsList = (List<Map<String, Object>>) contentMap.get("parts");

            if (partsList == null || partsList.isEmpty()) {
                throw new RuntimeException("No parts in API response");
            }

            return (String) partsList.get(0).get("text");

        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("401")) {
                throw new RuntimeException("Invalid API key - Authentication failed with Google Gemini");
            }
            throw e;
        }
    }

    /**
     * Parse AI response into structured data transfer objects
     */
    private AIGeneratedPlanDTO parseAIResponse(String aiResponse, SmartPlanRequest request, String userId) throws Exception {
        try {
            // Clean response - remove markdown if present
            String cleanJson = cleanJsonResponse(aiResponse);

            // Parse JSON
            JsonNode root = objectMapper.readTree(cleanJson);

            AIGeneratedPlanDTO plan = new AIGeneratedPlanDTO();
            plan.setUserId(userId);
            plan.setDestination(root.path("destination").asText(request.getDestination()));
            plan.setDurationDays(root.path("durationDays").asInt(request.getDurationDays()));
            plan.setBudgetTier(root.path("budgetTier").asText(request.getBudgetTier()));
            plan.setTravelStyle(root.path("travelStyle").asText(request.getTravelStyle()));
            plan.setStartDate(request.getStartDate() != null ? request.getStartDate() : LocalDate.now());
            plan.setEndDate(plan.getStartDate().plusDays(plan.getDurationDays() - 1));
            
            // Budget
            long budgetAmount = root.path("estimatedBudget").asLong(calculateEstimatedBudget(request.getBudgetTier(), request.getDurationDays()));
            plan.setEstimatedBudget(new BigDecimal(budgetAmount));
            
            // AI-specific content
            plan.setAiInsights(root.path("aiInsights").asText("AI-generated intelligent travel plan"));
            plan.setWeatherInfo(root.path("weatherInfo").asText("Weather information coming from AI analysis"));
            plan.setBestTimeToVisit(root.path("bestTimeToVisit").asText("Year-round destination"));
            plan.setAccommodation(root.path("accommodations").asText("Hotel recommendations"));
            plan.setTransportationTips(root.path("transportationTips").asText("Local transportation guide"));
            plan.setLocalTravelTips(root.path("localTravelTips").asText("Local travel tips and insights"));
            
            plan.setIsSaved(false);
            plan.setPlanStatus("ai_generated");

            // Parse daily itineraries
            List<AIDailyItineraryDTO> dailyItineraries = new ArrayList<>();
            JsonNode itinerariesNode = root.path("dailyItineraries");
            
            if (itinerariesNode.isArray()) {
                for (JsonNode dayNode : itinerariesNode) {
                    AIDailyItineraryDTO day = parseAIDay(dayNode, plan.getStartDate());
                    dailyItineraries.add(day);
                }
            }
            
            plan.setDailyItineraries(dailyItineraries);

            // Parse recommendations
            List<String> recommendations = new ArrayList<>();
            JsonNode recNode = root.path("recommendedAttractions");
            if (recNode.isArray()) {
                for (JsonNode rec : recNode) {
                    recommendations.add(rec.asText());
                }
            }
            plan.setRecommendedAttractions(recommendations);

            // Parse restaurants
            List<String> restaurants = new ArrayList<>();
            JsonNode restNode = root.path("recommendedRestaurants");
            if (restNode.isArray()) {
                for (JsonNode rest : restNode) {
                    restaurants.add(rest.asText());
                }
            }
            plan.setRecommendedRestaurants(restaurants);

            return plan;

        } catch (Exception e) {
            log.error("Failed to parse AI response: {}", e.getMessage(), e);
            throw new RuntimeException("AI engine failed due to: Response parsing error - " + e.getMessage(), e);
        }
    }

    /**
     * Parse a single day from AI response
     */
    private AIDailyItineraryDTO parseAIDay(JsonNode dayNode, LocalDate startDate) throws Exception {
        AIDailyItineraryDTO day = new AIDailyItineraryDTO();
        
        int dayNumber = dayNode.path("dayNumber").asInt(1);
        day.setDayNumber(dayNumber);
        day.setDate(startDate.plusDays(dayNumber - 1));
        day.setDayTitle(dayNode.path("dayTitle").asText("Day " + dayNumber));
        day.setSummary(dayNode.path("summary").asText(""));
        day.setMealPlan(dayNode.path("mealPlan").asText(""));
        day.setTravelTips(dayNode.path("travelTips").asText(""));
        day.setWeatherExpectation(dayNode.path("weatherExpectation").asText(""));
        
        BigDecimal dailyBudget = new BigDecimal(dayNode.path("estimatedDailyBudget").asLong(5000));
        day.setEstimatedDailyBudget(dailyBudget);

        // Parse packing reminders
        List<String> packingReminders = new ArrayList<>();
        JsonNode packNode = dayNode.path("packingReminders");
        if (packNode.isArray()) {
            for (JsonNode pack : packNode) {
                packingReminders.add(pack.asText());
            }
        }
        day.setPackingReminders(packingReminders);

        // Parse activities
        List<AIHourlyActivityDTO> activities = new ArrayList<>();
        JsonNode activitiesNode = dayNode.path("activities");
        if (activitiesNode.isArray()) {
            int order = 1;
            for (JsonNode actNode : activitiesNode) {
                AIHourlyActivityDTO activity = parseActivity(actNode, order++);
                activities.add(activity);
            }
        }
        day.setActivities(activities);

        return day;
    }

    /**
     * Parse a single activity
     */
    private AIHourlyActivityDTO parseActivity(JsonNode actNode, int order) throws Exception {
        AIHourlyActivityDTO activity = new AIHourlyActivityDTO();

        String startTimeStr = actNode.path("startTime").asText("08:00");
        String endTimeStr = actNode.path("endTime").asText("09:00");
        
        activity.setStartTime(LocalTime.parse(startTimeStr));
        activity.setEndTime(LocalTime.parse(endTimeStr));
        activity.setActivityName(actNode.path("activityName").asText("Activity"));
        activity.setDescription(actNode.path("description").asText(""));
        activity.setLocation(actNode.path("location").asText(""));
        activity.setLatitude(actNode.path("latitude").isNull() ? null : actNode.path("latitude").asDouble());
        activity.setLongitude(actNode.path("longitude").isNull() ? null : actNode.path("longitude").asDouble());
        activity.setActivityType(actNode.path("activityType").asText("activity"));
        activity.setDuration(actNode.path("duration").asText("1 hour"));
        activity.setRecommendations(actNode.path("recommendations").asText(""));
        activity.setEstimatedCost(new BigDecimal(actNode.path("estimatedCost").asLong(0)));
        activity.setBookingInfo(actNode.path("bookingInfo").asText(""));
        activity.setDressCode(actNode.path("dressCode").asText("Casual"));
        activity.setTravelTime(actNode.path("travelTime").asText("0 minutes"));
        activity.setVisitationOrder(order);

        return activity;
    }

    /**
     * Clean JSON response (remove markdown code blocks if present)
     */
    private String cleanJsonResponse(String response) {
        // Remove markdown code blocks
        response = response.replaceAll("```json\\s*", "");
        response = response.replaceAll("```\\s*", "");
        
        // Find JSON object
        int start = response.indexOf('{');
        int end = response.lastIndexOf('}');
        
        if (start != -1 && end != -1 && end > start) {
            return response.substring(start, end + 1);
        }
        
        return response;
    }

    private long calculateEstimatedBudget(String budgetTier, int days) {
        long dailyBudget = switch (budgetTier.toLowerCase()) {
            case "budget" -> 3000L;
            case "midrange", "mid_range" -> 6000L;
            case "luxury" -> 12000L;
            default -> 6000L;
        };
        return dailyBudget * days;
    }

    /**
     * Stream AI response for real-time itinerary generation
     */
    @Override
    public String generateAIPlanStream(SmartPlanRequest request, String userId) {
        try {
            String prompt = buildIntelligentPrompt(request);
            return callGoogleGenAIAPI(prompt);
        } catch (Exception e) {
            String error = "AI engine failed due to: " + e.getMessage();
            log.error("{}", error);
            return error;
        }
    }

    /**
     * Optimize an existing itinerary using AI based on user feedback
     */
    @Override
    public String optimizePlanWithAI(String planContent, String optimizationHint) {
        try {
            if (googleGenAIApiKey == null || googleGenAIApiKey.trim().isEmpty()) {
                throw new RuntimeException("API key not configured");
            }

            String prompt = String.format("""
                You are travel optimization expert. Optimize this travel plan based on user request.
                
                CURRENT PLAN:
                %s
                
                USER'S OPTIMIZATION REQUEST:
                %s
                
                RULES:
                ✅ Keep same structure and format as original
                ✅ Maintain daily breakfast/lunch/dinner schedule
                ✅ Return ONLY valid JSON (no markdown)
                ✅ Return optimized plan in same JSON format
                
                RETURN ONLY JSON - NO EXTRA TEXT
                """, planContent, optimizationHint);

            return callGoogleGenAIAPI(prompt);
        } catch (Exception e) {
            String error = "AI engine failed due to: " + e.getMessage();
            log.error("{}", error);
            return error;
        }
    }
}