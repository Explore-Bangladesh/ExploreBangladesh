package com.TeamDeadlock.ExploreBangladesh.service;

import com.TeamDeadlock.ExploreBangladesh.entity.CostBreakdownEntity;
import com.TeamDeadlock.ExploreBangladesh.entity.ItineraryDayEntity;
import com.TeamDeadlock.ExploreBangladesh.entity.TravelPlanEntity;
import com.TeamDeadlock.ExploreBangladesh.repository.CostBreakdownRepository;
import com.TeamDeadlock.ExploreBangladesh.repository.ItineraryDayRepository;
import com.TeamDeadlock.ExploreBangladesh.repository.TravelPlanEntityRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TravelPlanService {

    private final TravelPlanEntityRepository planRepository;
    private final ItineraryDayRepository dayRepository;
    private final CostBreakdownRepository costRepository;

    public TravelPlanService(TravelPlanEntityRepository planRepository,
                             ItineraryDayRepository dayRepository,
                             CostBreakdownRepository costRepository) {
        this.planRepository = planRepository;
        this.dayRepository = dayRepository;
        this.costRepository = costRepository;
    }

    /** Get all plans (summary only, no itinerary details) */
    public List<Map<String, Object>> getAllPlans() {
        return planRepository.findAll().stream()
                .map(this::toSummaryMap)
                .collect(Collectors.toList());
    }

    /** Get plans filtered by budget tier */
    public List<Map<String, Object>> getPlansByTier(String tier) {
        return planRepository.findByBudgetTierIgnoreCase(tier).stream()
                .map(this::toSummaryMap)
                .collect(Collectors.toList());
    }

    /** Get full plan detail with itinerary and cost breakdown */
    public Map<String, Object> getPlanById(String id) {
        Optional<TravelPlanEntity> optPlan = planRepository.findById(id);
        if (optPlan.isEmpty()) return null;

        TravelPlanEntity plan = optPlan.get();
        Map<String, Object> result = toSummaryMap(plan);

        // Add itinerary days with activities
        List<ItineraryDayEntity> days = dayRepository.findByTravelPlanIdOrderByDayNumberAsc(id);
        List<Map<String, Object>> daysList = new ArrayList<>();
        for (ItineraryDayEntity day : days) {
            Map<String, Object> dayMap = new LinkedHashMap<>();
            dayMap.put("dayNumber", day.getDayNumber());
            dayMap.put("title", day.getTitle());
            dayMap.put("summary", day.getSummary());

            List<Map<String, Object>> activitiesList = new ArrayList<>();
            if (day.getActivities() != null) {
                for (var activity : day.getActivities()) {
                    Map<String, Object> actMap = new LinkedHashMap<>();
                    actMap.put("startTime", activity.getStartTime());
                    actMap.put("endTime", activity.getEndTime());
                    actMap.put("title", activity.getTitle());
                    actMap.put("description", activity.getDescription());
                    actMap.put("activityType", activity.getActivityType());
                    actMap.put("location", activity.getLocation());
                    actMap.put("estimatedCost", activity.getEstimatedCost());
                    actMap.put("tips", activity.getTips());
                    activitiesList.add(actMap);
                }
            }
            dayMap.put("activities", activitiesList);
            daysList.add(dayMap);
        }
        result.put("days", daysList);

        // Add cost breakdown
        List<CostBreakdownEntity> costs = costRepository.findByTravelPlanId(id);
        List<Map<String, Object>> costList = new ArrayList<>();
        for (CostBreakdownEntity cost : costs) {
            Map<String, Object> costMap = new LinkedHashMap<>();
            costMap.put("category", cost.getCategory());
            costMap.put("amount", cost.getAmount());
            costMap.put("description", cost.getDescription());
            costList.add(costMap);
        }
        result.put("costBreakdown", costList);

        return result;
    }

    /** Convert entity to summary map (without itinerary details) */
    private Map<String, Object> toSummaryMap(TravelPlanEntity plan) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", plan.getId());
        map.put("destination", plan.getDestination());
        map.put("destinationImage", plan.getDestinationImage());
        map.put("budgetTier", plan.getBudgetTier());
        map.put("durationDays", plan.getDurationDays());
        map.put("durationNights", plan.getDurationNights());
        map.put("totalCost", plan.getTotalCost());
        map.put("description", plan.getDescription());
        map.put("highlights", plan.getHighlights());
        map.put("bestTimeToVisit", plan.getBestTimeToVisit());
        map.put("groupSize", plan.getGroupSize());
        map.put("division", plan.getDivision());
        return map;
    }
}
