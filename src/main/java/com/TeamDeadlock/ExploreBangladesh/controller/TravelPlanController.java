package com.TeamDeadlock.ExploreBangladesh.controller;

import com.TeamDeadlock.ExploreBangladesh.service.TravelPlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/plans")
public class TravelPlanController {

    private final TravelPlanService travelPlanService;

    public TravelPlanController(TravelPlanService travelPlanService) {
        this.travelPlanService = travelPlanService;
    }

    /** Health check */
    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> test() {
        return ResponseEntity.ok(Map.of("status", "Travel Plans API is working"));
    }

    /** Get all plans or filter by tier */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getPlans(
            @RequestParam(required = false) String tier) {
        List<Map<String, Object>> plans;
        if (tier != null && !tier.isBlank()) {
            plans = travelPlanService.getPlansByTier(tier);
        } else {
            plans = travelPlanService.getAllPlans();
        }
        return ResponseEntity.ok(plans);
    }

    /** Get full plan detail with itinerary and cost breakdown */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPlanDetail(@PathVariable String id) {
        Map<String, Object> plan = travelPlanService.getPlanById(id);
        if (plan == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(plan);
    }
}
