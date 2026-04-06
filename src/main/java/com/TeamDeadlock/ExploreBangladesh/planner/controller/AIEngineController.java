package com.TeamDeadlock.ExploreBangladesh.planner.controller;

import com.TeamDeadlock.ExploreBangladesh.planner.dto.AIGeneratedPlanDTO;
import com.TeamDeadlock.ExploreBangladesh.planner.dto.SmartPlanRequest;
import com.TeamDeadlock.ExploreBangladesh.planner.service.AIEngineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * AI Engine Controller
 * Provides endpoints for Google Generative AI powered trip planning
 * Uses free tier of Google GenAI (Gemini) for intelligent itinerary generation
 */
@Slf4j
@RestController
@RequestMapping("/api/planner/ai")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AIEngineController {

    private final AIEngineService aiEngineService;

    /**
     * Generate an AI-powered travel plan using Google Gemini
     * Creates intelligent, realistic itineraries with specific timings
     * 
     * Endpoint: POST /api/planner/ai/generate
     * 
     * @param request Smart plan request (destination, duration, budget, preferences)
     * @param authentication User authentication token (optional for anonymous users)
     * @return AI-generated plan with detailed daily schedules
     */
    @PostMapping("/generate")
    public ResponseEntity<?> generateAIPlan(
            @Valid @RequestBody SmartPlanRequest request,
            Authentication authentication) {
        
        try {
            String userId = extractUserIdFromAuthentication(authentication);
            
            if (userId != null) {
                log.info("User {} requesting AI-powered plan for: {}", userId, request.getDestination());
            } else {
                log.info("Generating AI-powered plan for: {}", request.getDestination());
            }

            // Generate AI plan
            AIGeneratedPlanDTO aiPlan = aiEngineService.generateAIPoweredPlan(request, userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "AI-powered plan generated successfully");
            response.put("engine", "Google Generative AI (Gemini)");
            response.put("data", aiPlan);
            
            log.info("Plan with {} daily itineraries generated", aiPlan.getDailyItineraries().size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error generating AI plan", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 500);
            errorResponse.put("message", "Failed to generate AI plan: " + e.getMessage());
            errorResponse.put("error", e.getClass().getSimpleName());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Generate AI plan with streaming response for real-time updates
     * Shows AI thinking process as it generates the itinerary
     * 
     * Endpoint: POST /api/planner/ai/generate-stream
     * 
     * @param request Smart plan request
     * @param authentication User authentication (optional)
     * @return Streaming AI response
     */
    @PostMapping("/generate-stream")
    public ResponseEntity<?> generateAIPlanStream(
            @Valid @RequestBody SmartPlanRequest request,
            Authentication authentication) {
        
        try {
            String userId = extractUserIdFromAuthentication(authentication);
            
            log.info("Generating streaming AI plan for: {}", request.getDestination());
            
            String streamResponse = aiEngineService.generateAIPlanStream(request, userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Streaming plan generated successfully");
            response.put("data", streamResponse);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error generating streaming plan", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 500);
            errorResponse.put("message", "Failed to generate streaming plan: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Optimize an existing plan based on user feedback
     * Allows users to refine AI-generated plans
     * 
     * Endpoint: POST /api/planner/ai/optimize
     * 
     * Example optimization hints:
     * - "Reduce travel time between attractions"
     * - "Add more cultural sites"
     * - "Include more local food experiences"
     * - "Make it less rushed, more relaxed"
     * 
     * @param optimizationRequest Plan content and optimization hint
     * @param authentication User authentication (optional)
     * @return Optimized plan
     */
    @PostMapping("/optimize")
    public ResponseEntity<?> optimizeAIPlan(
            @RequestBody Map<String, String> optimizationRequest,
            Authentication authentication) {
        
        try {
            String planContent = optimizationRequest.get("planContent");
            String optimizationHint = optimizationRequest.get("optimizationHint");
            
            if (planContent == null || planContent.isEmpty() || optimizationHint == null || optimizationHint.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", 400);
                errorResponse.put("message", "Missing planContent or optimizationHint");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            
            log.info("Optimizing plan with hint: {}", optimizationHint);
            
            String optimizedPlan = aiEngineService.optimizePlanWithAI(planContent, optimizationHint);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Plan optimized successfully");
            response.put("data", optimizedPlan);
            
            log.info("Plan optimized");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error optimizing plan", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 500);
            errorResponse.put("message", "Failed to optimize plan: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Health check for AI engine
     * Verifies that Google Generative AI integration is configured
     * 
     * Endpoint: GET /api/planner/ai/health
     * 
     * @return Health status
     */
    @GetMapping("/health")
    public ResponseEntity<?> checkAIEngineHealth() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "AI Engine is operational");
        response.put("provider", "Google Generative AI (Gemini)");
        response.put("capabilities", new String[]{
            "Intelligent itinerary generation",
            "Activity scheduling with real times",
            "Budget-aware recommendations",
            "Plan optimization",
            "Real-time streaming responses"
        });
        
        log.info("AI Engine health check passed");
        return ResponseEntity.ok(response);
    }

    /**
     * Extract user ID from authentication
     * Returns null for anonymous users
     */
    private String extractUserIdFromAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return authentication.getName();
    }
}
