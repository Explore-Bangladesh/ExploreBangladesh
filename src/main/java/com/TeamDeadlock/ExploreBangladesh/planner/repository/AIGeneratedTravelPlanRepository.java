package com.TeamDeadlock.ExploreBangladesh.planner.repository;

import com.TeamDeadlock.ExploreBangladesh.planner.entity.AIGeneratedTravelPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for AI-generated travel plans
 */
@Repository
public interface AIGeneratedTravelPlanRepository extends JpaRepository<AIGeneratedTravelPlan, Long> {

    /**
     * Find all AI plans created by a specific user
     */
    List<AIGeneratedTravelPlan> findByUserId(String userId);

    /**
     * Find AI plans by user and destination
     */
    List<AIGeneratedTravelPlan> findByUserIdAndDestination(String userId, String destination);

    /**
     * Find a specific AI plan by ID and verify it belongs to the user
     */
    @Query("SELECT p FROM AIGeneratedTravelPlan p WHERE p.id = :planId AND p.userId = :userId")
    Optional<AIGeneratedTravelPlan> findByIdAndUserId(@Param("planId") Long planId, @Param("userId") String userId);

    /**
     * Count AI plans created by a user
     */
    Long countByUserId(String userId);
}
