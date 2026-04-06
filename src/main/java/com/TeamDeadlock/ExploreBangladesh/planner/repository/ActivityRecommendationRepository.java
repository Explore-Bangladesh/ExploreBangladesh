package com.TeamDeadlock.ExploreBangladesh.planner.repository;

import com.TeamDeadlock.ExploreBangladesh.planner.entity.ActivityRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRecommendationRepository extends JpaRepository<ActivityRecommendation, Long> {
    
    @Query("SELECT ar FROM ActivityRecommendation ar WHERE " +
           "ar.attractionId IN (SELECT a.id FROM Attraction a WHERE a.destinationId = :destinationId) " +
           "AND ar.travelStyle = :travelStyle")
    List<ActivityRecommendation> findRecommendationsByDestinationAndStyle(
        @Param("destinationId") Long destinationId,
        @Param("travelStyle") String travelStyle
    );
    
    @Query("SELECT ar FROM ActivityRecommendation ar WHERE ar.attractionId = :attractionId")
    List<ActivityRecommendation> findByAttractionId(@Param("attractionId") Long attractionId);
}
