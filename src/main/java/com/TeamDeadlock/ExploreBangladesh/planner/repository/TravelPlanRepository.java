package com.TeamDeadlock.ExploreBangladesh.planner.repository;

import com.TeamDeadlock.ExploreBangladesh.planner.entity.TravelPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TravelPlanRepository extends JpaRepository<TravelPlan, Long> {
    List<TravelPlan> findByUserIdOrderByCreatedAtDesc(String userId);
    Optional<TravelPlan> findByPlanIdAndUserId(Long planId, String userId);
    List<TravelPlan> findByStatus(String status);
    List<TravelPlan> findByDestinationIgnoreCase(String destination);
}
