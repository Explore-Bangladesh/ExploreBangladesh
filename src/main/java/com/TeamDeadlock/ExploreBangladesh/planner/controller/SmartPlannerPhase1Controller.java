package com.TeamDeadlock.ExploreBangladesh.planner.controller;

import com.TeamDeadlock.ExploreBangladesh.planner.dto.SmartPlanRequest;
import com.TeamDeadlock.ExploreBangladesh.planner.entity.TravelPlan;
import com.TeamDeadlock.ExploreBangladesh.planner.repository.TravelPlanRepository;
import com.TeamDeadlock.ExploreBangladesh.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final UserRepository userRepository;

    /**
     * GET /api/planner/my-plans
     * Retrieve all plans for the authenticated user
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
            
            log.info("Fetching plans for user: {}", userId);

            List<TravelPlan> plans = travelPlanRepository.findByUserIdOrderByCreatedAtDesc(userId);
            
            log.info("📊 Found {} plans for user {}", plans.size(), userId);
            if (plans.size() > 0) {
                plans.forEach(p -> log.info("   - Plan ID: {}, Destination: {}, User: {}", p.getPlanId(), p.getDestination(), p.getUserId()));
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Plans retrieved successfully");
            response.put("data", plans.stream().map(this::convertToSimpleDTO).collect(Collectors.toList()));
            response.put("totalPlans", plans.size());
            
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

            var plan = travelPlanRepository.findById(planId);
            if (plan.isEmpty() || !plan.get().getUserId().equals(userId)) {
                Map<String, Object> error = new HashMap<>();
                error.put("status", 404);
                error.put("message", "Plan not found or unauthorized access");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Plan retrieved successfully");
            response.put("data", convertToSimpleDTO(plan.get()));
            
            return ResponseEntity.ok(response);
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
     * Delete a plan
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

            var plan = travelPlanRepository.findById(planId);
            if (plan.isEmpty() || !plan.get().getUserId().equals(userId)) {
                Map<String, Object> error = new HashMap<>();
                error.put("status", 404);
                error.put("message", "Plan not found or unauthorized access");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            travelPlanRepository.deleteById(planId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Plan deleted successfully");
            response.put("data", null);
            
            return ResponseEntity.ok(response);
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
