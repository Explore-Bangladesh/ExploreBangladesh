package com.TeamDeadlock.ExploreBangladesh.planner.repository;

import com.TeamDeadlock.ExploreBangladesh.planner.entity.TravelInsight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TravelInsightRepository extends JpaRepository<TravelInsight, Long> {
    List<TravelInsight> findByPlanId(Long planId);
    List<TravelInsight> findByPlanIdAndInsightType(Long planId, String insightType);
    void deleteByPlanId(Long planId);
}
