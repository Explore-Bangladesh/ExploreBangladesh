package com.TeamDeadlock.ExploreBangladesh.planner.repository;

import com.TeamDeadlock.ExploreBangladesh.planner.entity.ItineraryActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItineraryActivityRepository extends JpaRepository<ItineraryActivity, Long> {
    List<ItineraryActivity> findByItineraryIdOrderByOrderIndex(Long itineraryId);
    List<ItineraryActivity> findByActivityType(String activityType);
    void deleteByItineraryId(Long itineraryId);
}
