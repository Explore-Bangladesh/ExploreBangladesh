package com.TeamDeadlock.ExploreBangladesh.planner.controller;

import com.TeamDeadlock.ExploreBangladesh.planner.dto.SmartPlanRequest;
import com.TeamDeadlock.ExploreBangladesh.planner.entity.TravelPlan;
import com.TeamDeadlock.ExploreBangladesh.planner.entity.AIGeneratedTravelPlan;
import com.TeamDeadlock.ExploreBangladesh.planner.repository.TravelPlanRepository;
import com.TeamDeadlock.ExploreBangladesh.planner.repository.AIGeneratedTravelPlanRepository;
import com.TeamDeadlock.ExploreBangladesh.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Collectors;

/**
 * Phase 1: Smart Planner REST API
 * Basic CRUD operations for travel plans
 */
@Slf4j
@RestController
@RequestMapping("/api/planner")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class SmartPlannerPhase1Controller {

    private final TravelPlanRepository travelPlanRepository;
    private final AIGeneratedTravelPlanRepository aiGeneratedTravelPlanRepository;
    private final UserRepository userRepository;

    /**
     * GET /api/planner/my-plans
     * Retrieve ALL plans (both manual and AI-generated) for the authenticated user
     */
    @GetMapping("/my-plans")
    public ResponseEntity<?> getUserPlans(Authentication authentication) {
        try {
            String userId = extractUserIdFromAuthentication(authentication);
            
            if (userId == null || userId.isEmpty()) {
                log.warn("Could not extract user ID from authentication");
                Map<String, Object> error = new HashMap<>();
                error.put("status", 401);
                error.put("message", "Unauthorized: Could not identify user");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
            log.info("Fetching ALL plans for user: {}", userId);

            // ✅ GET MANUAL PLANS from travel_plans table
            List<TravelPlan> manualPlans = travelPlanRepository.findByUserIdOrderByCreatedAtDesc(userId);
            log.info("📊 Found {} MANUAL plans for user {}", manualPlans.size(), userId);
            
            // ✅ GET AI-GENERATED PLANS from ai_generated_travel_plans table
            List<AIGeneratedTravelPlan> aiPlans = aiGeneratedTravelPlanRepository.findByUserId(userId);
            log.info("🤖 Found {} AI-GENERATED plans for user {}", aiPlans.size(), userId);
            
            // ✅ COMBINE BOTH LISTS
            List<Map<String, Object>> allPlans = new ArrayList<>();
            
            // Add manual plans
            allPlans.addAll(manualPlans.stream().map(p -> {
                Map<String, Object> dto = convertToSimpleDTO(p);
                dto.put("planType", "manual");  // Mark as manual
                return dto;
            }).collect(Collectors.toList()));
            
            // Add AI-generated plans
            allPlans.addAll(aiPlans.stream().map(p -> {
                Map<String, Object> dto = convertAITravelPlanToDTO(p);
                dto.put("planType", "ai_generated");  // Mark as AI-generated
                return dto;
            }).collect(Collectors.toList()));
            
            int totalPlans = manualPlans.size() + aiPlans.size();
            log.info("✅ Total {} plans retrieved (Manual: {}, AI-Generated: {})", 
                totalPlans, manualPlans.size(), aiPlans.size());
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Plans retrieved successfully");
            response.put("data", allPlans);
            response.put("totalPlans", totalPlans);
            response.put("manualPlans", manualPlans.size());
            response.put("aiGeneratedPlans", aiPlans.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching user plans", e);
            Map<String, Object> error = new HashMap<>();
            error.put("status", 500);
            error.put("message", "Failed to fetch plans: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * GET /api/planner/{planId}
     * Retrieve a specific plan (basic details only, not full itinerary)
     * ✅ FIXED: Now checks both manual AND AI-generated plans
     */
    @GetMapping("/{planId}")
    public ResponseEntity<?> getPlan(
            @PathVariable Long planId,
            Authentication authentication) {
        try {
            String userId = extractUserIdFromAuthentication(authentication);
            
            if (userId == null || userId.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("status", 401);
                error.put("message", "Unauthorized: Could not identify user");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
            log.info("User {} retrieving plan: {}", userId, planId);

            // ✅ TRY MANUAL PLANS FIRST
            var manualPlan = travelPlanRepository.findById(planId);
            if (manualPlan.isPresent() && manualPlan.get().getUserId().equals(userId)) {
                log.info("✅ Found MANUAL plan {} for user {}", planId, userId);
                Map<String, Object> response = new HashMap<>();
                response.put("status", 200);
                response.put("message", "Plan retrieved successfully");
                response.put("data", convertToSimpleDTO(manualPlan.get()));
                response.put("planType", "manual");
                return ResponseEntity.ok(response);
            }

            // ✅ TRY AI-GENERATED PLANS SECOND
            var aiPlan = aiGeneratedTravelPlanRepository.findById(planId);
            if (aiPlan.isPresent() && aiPlan.get().getUserId().equals(userId)) {
                log.info("✅ Found AI-GENERATED plan {} for user {}", planId, userId);
                Map<String, Object> response = new HashMap<>();
                response.put("status", 200);
                response.put("message", "Plan retrieved successfully");
                response.put("data", convertAITravelPlanToDTO(aiPlan.get()));
                response.put("planType", "ai_generated");
                return ResponseEntity.ok(response);
            }

            // ❌ PLAN NOT FOUND
            log.warn("⚠️ Plan {} not found or unauthorized access for user {}", planId, userId);
            Map<String, Object> error = new HashMap<>();
            error.put("status", 404);
            error.put("message", "Plan not found or unauthorized access");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            log.error("Error retrieving plan", e);
            Map<String, Object> error = new HashMap<>();
            error.put("status", 500);
            error.put("message", "Failed to retrieve plan: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * PUT /api/planner/{planId}
     * Update an existing plan
     */
    @PutMapping("/{planId}")
    public ResponseEntity<?> updatePlan(
            @PathVariable Long planId,
            @Valid @RequestBody SmartPlanRequest request,
            Authentication authentication) {
        try {
            String userId = extractUserIdFromAuthentication(authentication);
            
            if (userId == null || userId.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("status", 401);
                error.put("message", "Unauthorized: Could not identify user");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
            log.info("User {} updating plan: {}", userId, planId);

            var plan = travelPlanRepository.findById(planId);
            if (plan.isEmpty() || !plan.get().getUserId().equals(userId)) {
                Map<String, Object> error = new HashMap<>();
                error.put("status", 404);
                error.put("message", "Plan not found or unauthorized access");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            TravelPlan existingPlan = plan.get();
            
            // Update fields
            if (request.getDestination() != null) {
                existingPlan.setDestination(request.getDestination());
            }
            if (request.getDurationDays() != null) {
                existingPlan.setDurationDays(request.getDurationDays());
            }
            if (request.getBudgetTier() != null) {
                existingPlan.setBudgetTier(request.getBudgetTier());
            }
            if (request.getTravelStyle() != null) {
                existingPlan.setTravelStyle(request.getTravelStyle());
            }
            if (request.getStartDate() != null) {
                existingPlan.setStartDate(request.getStartDate());
            }

            TravelPlan updated = travelPlanRepository.save(existingPlan);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Plan updated successfully");
            response.put("data", convertToSimpleDTO(updated));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating plan", e);
            Map<String, Object> error = new HashMap<>();
            error.put("status", 500);
            error.put("message", "Failed to update plan: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * DELETE /api/planner/{planId}
     * Delete a plan (checks both manual and AI tables)
     * ✅ FIXED: Now handles both table types
     */
    @DeleteMapping("/{planId}")
    public ResponseEntity<?> deletePlan(
            @PathVariable Long planId,
            Authentication authentication) {
        try {
            String userId = extractUserIdFromAuthentication(authentication);
            
            if (userId == null || userId.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("status", 401);
                error.put("message", "Unauthorized: Could not identify user");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
            log.info("User {} deleting plan: {}", userId, planId);

            // ✅ Try manual plans first
            var manualPlan = travelPlanRepository.findById(planId);
            if (manualPlan.isPresent() && manualPlan.get().getUserId().equals(userId)) {
                travelPlanRepository.deleteById(planId);
                log.info("✅ Deleted MANUAL plan {} for user {}", planId, userId);
                
                Map<String, Object> response = new HashMap<>();
                response.put("status", 200);
                response.put("message", "Plan deleted successfully");
                response.put("data", null);
                return ResponseEntity.ok(response);
            }

            // ✅ Try AI plans second
            var aiPlan = aiGeneratedTravelPlanRepository.findById(planId);
            if (aiPlan.isPresent() && aiPlan.get().getUserId().equals(userId)) {
                aiGeneratedTravelPlanRepository.deleteById(planId);
                log.info("✅ Deleted AI-GENERATED plan {} for user {}", planId, userId);
                
                Map<String, Object> response = new HashMap<>();
                response.put("status", 200);
                response.put("message", "Plan deleted successfully");
                response.put("data", null);
                return ResponseEntity.ok(response);
            }

            // ❌ Plan not found in either table
            Map<String, Object> error = new HashMap<>();
            error.put("status", 404);
            error.put("message", "Plan not found or unauthorized access");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            log.error("Error deleting plan", e);
            Map<String, Object> error = new HashMap<>();
            error.put("status", 500);
            error.put("message", "Failed to delete plan: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Convert TravelPlan to simple DTO for listing/basic retrieval
     */
    private Map<String, Object> convertToSimpleDTO(TravelPlan plan) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("planId", plan.getPlanId());
        dto.put("destination", plan.getDestination());
        dto.put("durationDays", plan.getDurationDays());
        dto.put("budgetTier", plan.getBudgetTier());
        dto.put("travelStyle", plan.getTravelStyle());
        dto.put("startDate", plan.getStartDate());
        dto.put("endDate", plan.getEndDate());
        dto.put("status", plan.getStatus() != null ? plan.getStatus() : "draft");
        dto.put("totalBudget", plan.getTotalBudgetEstimate());
        dto.put("createdAt", plan.getCreatedAt());
        dto.put("updatedAt", plan.getUpdatedAt());
        return dto;
    }

    /**
     * Convert AIGeneratedTravelPlan to simple DTO for listing/basic retrieval
     * Extracts key fields from stored plan JSON when needed
     */
    private Map<String, Object> convertAITravelPlanToDTO(AIGeneratedTravelPlan plan) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("planId", plan.getId());
        dto.put("destination", plan.getDestination());
        dto.put("durationDays", plan.getDurationDays());
        dto.put("budgetTier", plan.getBudgetTier());
        dto.put("travelStyle", plan.getTravelStyle());
        dto.put("totalBudgetEstimate", plan.getTotalBudgetEstimate());
        dto.put("totalBudget", plan.getTotalBudgetEstimate());  // Alias for consistency
        dto.put("status", "completed");  // AI plans are auto-completed
        dto.put("createdAt", plan.getCreatedAt());
        dto.put("updatedAt", plan.getUpdatedAt());
        // Note: Full planData JSON is stored in plan.getPlanData(), can be retrieved if needed
        return dto;
    }

    /**
     * Helper method to extract user ID from authentication
     * Extracts email from authentication principal and looks up user UUID
     */
    private String extractUserIdFromAuthentication(Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("⚠️ Authentication is null or not authenticated");
                return null;
            }
            
            // authentication.getName() returns the email (set by JwtAuthenticationFilter)
            String email = authentication.getName();
            log.info("📧 Extracted email from authentication: {}", email);
            
            if (email == null || email.isEmpty()) {
                log.warn("⚠️ Email is empty or null");
                return null;
            }
            
            // Look up user by email and get their UUID
            var user = userRepository.findByEmail(email);
            if (user.isPresent()) {
                String userId = user.get().getId().toString();
                log.info("Found user: {} with ID: {}", email, userId);
                return userId;
            }
            
            log.warn("⚠️ User not found in database for email: {}", email);
            return null;
        } catch (Exception e) {
            log.error("Error extracting user ID from authentication", e);
            return null;
        }
    }
}
