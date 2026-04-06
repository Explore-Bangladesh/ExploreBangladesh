package com.TeamDeadlock.ExploreBangladesh.planner.service;

import com.TeamDeadlock.ExploreBangladesh.planner.dto.AIGeneratedPlanDTO;
import com.TeamDeadlock.ExploreBangladesh.planner.dto.SmartPlanRequest;

/**
 * AI-powered trip planning service using Google Generative AI
 */
public interface AIEngineService {

    AIGeneratedPlanDTO generateAIPoweredPlan(SmartPlanRequest request, String userId);

    String generateAIPlanStream(SmartPlanRequest request, String userId);

    String optimizePlanWithAI(String planContent, String optimizationHint);
}
