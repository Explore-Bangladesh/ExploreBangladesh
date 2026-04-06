package com.TeamDeadlock.ExploreBangladesh.planner.repository;

import com.TeamDeadlock.ExploreBangladesh.planner.entity.DailyItinerary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DailyItineraryRepository extends JpaRepository<DailyItinerary, Long> {
    List<DailyItinerary> findByPlanIdOrderByDayNumber(Long planId);
    Optional<DailyItinerary> findByPlanIdAndDayNumber(Long planId, Integer dayNumber);
    void deleteByPlanId(Long planId);
}
