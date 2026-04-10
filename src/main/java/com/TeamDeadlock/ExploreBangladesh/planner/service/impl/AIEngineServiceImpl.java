package com.TeamDeadlock.ExploreBangladesh.planner.service.impl;

import com.TeamDeadlock.ExploreBangladesh.planner.dto.*;
import com.TeamDeadlock.ExploreBangladesh.planner.service.AIEngineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpServerErrorException;
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
    private final SmartPlannerServiceImplV2 smartPlannerService;

    @Value("${spring.ai.google.genai.api-key:}")
    private String googleGenAIApiKey;

    private static final String GOOGLE_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    /**
     * Generate AI-powered intelligent travel plan
     * Falls back to Standard Planner if AI service is unavailable (503 error)
     * 
     * @param request User's travel request (destination, duration, budget, style)
     * @param userId User identifier
     * @return AI-generated or Standard travel plan
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
                log.info("Falling back to Standard Planner for user: {}", userId);
                return fallbackToStandardPlanner(request, userId, error);
            }

            // Step 1: Build intelligent prompt
            String prompt = buildIntelligentPrompt(request);

            // Step 2: Call Gemini API
            log.info("Calling Google Gemini API...");
            String aiResponse = callGoogleGenAIAPI(prompt);

            // Step 3: Parse response into structured DTOs
            log.info("Parsing AI response...");
            AIGeneratedPlanDTO plan = parseAIResponse(aiResponse, request, userId);
            
            // Mark as AI generated
            plan.setPlanStatus("ai_generated");
            plan.setAiInsights((plan.getAiInsights() != null ? plan.getAiInsights() : "AI-generated intelligent travel plan"));

            log.info("Plan generation completed successfully! Plan ID: {}", plan.getPlanId());
            return plan;

        } catch (AIServiceUnavailableException e) {
            // 503 Service Unavailable - Gracefully fall back to Standard Planner
            String errorMsg = "AI service temporarily unavailable (503 Service Unavailable). Reason: " + e.getMessage();
            log.warn("{}", errorMsg);
            log.info("Falling back to Standard Planner for user: {}", userId);
            return fallbackToStandardPlanner(request, userId, e.getMessage());
            
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
     * Fallback to Standard Planner when AI service is unavailable
     * Generates a complete itinerary using SmartPlannerServiceImplV2
     */
    private AIGeneratedPlanDTO fallbackToStandardPlanner(SmartPlanRequest request, String userId, String fallbackReason) {
        try {
            log.info("Generating plan using Standard Planner as fallback for user: {} due to: {}", userId, fallbackReason);
            
            // Call SmartPlannerServiceImplV2 to generate standard plan (full itinerary)
            EnhancedTravelPlanDTO standardPlan = smartPlannerService.generateSmartPlanPreview(request, userId);
            
            // Convert to AIGeneratedPlanDTO
            AIGeneratedPlanDTO aiPlan = new AIGeneratedPlanDTO();
            aiPlan.setUserId(userId);
            aiPlan.setDestination(standardPlan.getDestination());
            aiPlan.setDurationDays(standardPlan.getDurationDays());
            aiPlan.setBudgetTier(standardPlan.getBudgetTier());
            aiPlan.setTravelStyle(standardPlan.getTravelStyle());
            aiPlan.setStartDate(request.getStartDate() != null ? request.getStartDate() : LocalDate.now());
            aiPlan.setEndDate(aiPlan.getStartDate().plusDays(request.getDurationDays() - 1));
            aiPlan.setEstimatedBudget(new BigDecimal(calculateEstimatedBudget(request.getBudgetTier(), request.getDurationDays())));
            
            // Set info from standard plan
            aiPlan.setWeatherInfo("Check local weather forecast for your destination");
            aiPlan.setBestTimeToVisit("October to March for most destinations (cool and dry)");
            aiPlan.setAccommodation("Mid-range hotels and guesthouses available at reasonable prices");
            aiPlan.setTransportationTips("Use local buses, ridesharing apps (Uber/Pathao), or private taxis");
            aiPlan.setLocalTravelTips("Learn basic Bengali phrases. Cash payment needed for local markets. Carry light clothing and sun protection.");
            
            // Mark as generated using standard planner due to AI unavailability
            aiPlan.setPlanStatus("standard_planner");
            aiPlan.setAiInsights(
                "This plan was generated using our Standard Planner because the AI service is temporarily experiencing high demand (503 Service Unavailable). " +
                "Your itinerary is complete and ready to use. Once the AI service is back online, you can regenerate this plan for AI-personalized insights and recommendations."
            );
            aiPlan.setIsSaved(false);
            
            // Copy recommendations if available
            if (standardPlan.getSuggestedRestaurants() != null && !standardPlan.getSuggestedRestaurants().isEmpty()) {
                List<String> restaurantNames = new ArrayList<>();
                for (RestaurantDTO rest : standardPlan.getSuggestedRestaurants()) {
                    restaurantNames.add(rest.getName());
                }
                aiPlan.setRecommendedRestaurants(restaurantNames);
            }
            
            if (standardPlan.getPlannedAttractions() != null && !standardPlan.getPlannedAttractions().isEmpty()) {
                List<String> attractionNames = new ArrayList<>();
                for (AttractionDTO attr : standardPlan.getPlannedAttractions()) {
                    attractionNames.add(attr.getName());
                }
                aiPlan.setRecommendedAttractions(attractionNames);
            }
            
            // Convert daily itineraries
            if (standardPlan.getDailyItineraries() != null && !standardPlan.getDailyItineraries().isEmpty()) {
                List<AIDailyItineraryDTO> aiBriefItineraries = new ArrayList<>();
                for (EnhancedDailyItineraryDTO day : standardPlan.getDailyItineraries()) {
                    AIDailyItineraryDTO aiDay = new AIDailyItineraryDTO();
                    aiDay.setDayNumber(day.getDayNumber());
                    aiDay.setDate(aiPlan.getStartDate().plusDays(day.getDayNumber() - 1));
                    aiDay.setDayTitle(day.getTheme() != null ? ("Day " + day.getDayNumber() + ": " + day.getTheme()) : ("Day " + day.getDayNumber()));
                    aiDay.setSummary(day.getSummary() != null ? day.getSummary() : "");
                    aiDay.setWeatherExpectation(day.getWeatherForecast() != null ? day.getWeatherForecast() : "");
                    aiDay.setEstimatedDailyBudget(day.getTotalCostBdt() != null ? new BigDecimal(day.getTotalCostBdt()) : new BigDecimal(0));
                    
                    // Copy activities if available
                    if (day.getActivities() != null && !day.getActivities().isEmpty()) {
                        List<AIHourlyActivityDTO> aiActivities = new ArrayList<>();
                        int order = 1;
                        for (EnhancedItineraryActivityDTO activity : day.getActivities()) {
                            AIHourlyActivityDTO aiActivity = new AIHourlyActivityDTO();
                            aiActivity.setActivityName(activity.getActivityName());
                            aiActivity.setActivityType(activity.getType());
                            aiActivity.setDescription(activity.getDescription());
                            aiActivity.setLocation(activity.getLocation());
                            aiActivity.setEstimatedCost(activity.getCostBdt() != null ? new BigDecimal(activity.getCostBdt()) : new BigDecimal(0));
                            aiActivity.setStartTime(activity.getStartTime());
                            aiActivity.setEndTime(activity.getEndTime());
                            aiActivity.setVisitationOrder(order++);
                            aiActivities.add(aiActivity);
                        }
                        aiDay.setActivities(aiActivities);
                    }
                    
                    aiBriefItineraries.add(aiDay);
                }
                aiPlan.setDailyItineraries(aiBriefItineraries);
            }
            
            log.info("Standard Planner fallback completed successfully for user: {} with full itinerary", userId);
            return aiPlan;
            
        } catch (Exception e) {
            String errorMsg = "Standard Planner also failed, cannot generate itinerary: " + e.getMessage();
            log.error("{}", errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * Build intelligent prompt for Gemini API
     * Uses AI's knowledge of real Bangladesh attractions instead of hardcoded lists
     */
    private String buildIntelligentPrompt(SmartPlanRequest request) {
        String destination = request.getDestination();
        
        return String.format("""
            You are an expert travel consultant specializing in Bangladesh tourism. Your task is to create 
            a COMPLETE, DETAILED, and INTELLIGENT %d-day travel itinerary for %s with hourly breakdown.
            
            USER PROFILE:
            • Destination: %s
            • Duration: %d days
            • Budget Tier: %s
            • Travel Style: %s
            
            DAILY SCHEDULE TEMPLATE (MUST FOLLOW FOR EVERY DAY):
            08:00-09:00 | Breakfast at hotel
            09:00-12:00 | Morning activity (includes 15-30 min travel time)
            12:00-13:30 | Lunch at restaurant
            13:30-17:30 | Afternoon activities (1-2 attractions max)
            17:30-18:30 | Return to hotel / rest
            19:00-20:30 | Dinner at restaurant
            20:30-22:00 | Evening relaxation / shopping
            
            ATTRACTIONS:
            Use your knowledge of real Bangladesh attractions near %s. Suggest authentic, popular attractions
            that actually exist and match the destination. Avoid duplicate attractions across different days.
            
            CRITICAL RULES:
            1. NO duplicate attractions across different days
            2. Return to hotel by 18:00 every day WITHOUT FAIL
            3. Times in 24-hour format (08:00, 14:30, 19:45)
            4. All costs in BDT currency
            5. Realistic travel times: 15-45 minutes between locations
            6. Budget tier allocation:
               - Budget: 2,000-3,500 BDT/day total
               - Midrange: 5,000-7,500 BDT/day total
               - Luxury: 10,000+ BDT/day total
            
            RETURN ONLY VALID JSON (no markdown, no code blocks):
            
            {
              "destination": "%s",
              "durationDays": %d,
              "budgetTier": "%s",
              "travelStyle": "%s",
              "estimatedBudget": %d,
              "aiInsights": "5-7 short bullet points (each 1 sentence max) about %s. Format as • point1 • point2 • point3 etc.",
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
            destination,
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
     * Call Google Gemini API with error handling
     * Throws AIServiceUnavailableException on 503 errors (for graceful fallback)
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
                Object statusCode = error.get("code");
                
                // Check for 503 Service Unavailable
                if (statusCode instanceof Integer && (Integer) statusCode == 503) {
                    throw new AIServiceUnavailableException("AI service temporarily unavailable: " + errorMsg);
                }
                
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

        } catch (HttpServerErrorException e) {
            // Catch HTTP 5xx errors specifically
            if (e.getStatusCode().value() == 503) {
                String responseBody = e.getResponseBodyAsString();
                log.warn("Google Gemini API returned 503: {}", responseBody);
                throw new AIServiceUnavailableException("Google Gemini API is experiencing high demand. Status: 503 Service Unavailable. " +
                        "Response: " + responseBody);
            }
            throw e;
        } catch (AIServiceUnavailableException e) {
            // Re-throw our custom exception for fallback handling
            throw e;
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

    /**
     * Custom exception for AI service unavailability (503 errors)
     * Triggers graceful fallback to standard response
     */
    private static class AIServiceUnavailableException extends Exception {
        public AIServiceUnavailableException(String message) {
            super(message);
        }

    }
}

/**
 * Public exception class for AI Service Unavailability
 * Can be imported and caught by other services
 */
class PublicAIServiceUnavailableException extends Exception {
    public PublicAIServiceUnavailableException(String message) {
        super(message);
    }
    
    public PublicAIServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}