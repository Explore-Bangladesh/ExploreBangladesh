package com.TeamDeadlock.ExploreBangladesh.planner.repository;

import com.TeamDeadlock.ExploreBangladesh.planner.entity.TravelPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TravelPreferenceRepository extends JpaRepository<TravelPreference, Long> {
    Optional<TravelPreference> findByUserId(String userId);
}
