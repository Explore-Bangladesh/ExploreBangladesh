package com.TeamDeadlock.ExploreBangladesh.planner.repository;

import com.TeamDeadlock.ExploreBangladesh.planner.entity.Attraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttractionRepository extends JpaRepository<Attraction, Long> {
    
    List<Attraction> findByDestinationId(Long destinationId);
    
    List<Attraction> findByDestinationIdAndTravelStyle(Long destinationId, String travelStyle);
    
    List<Attraction> findByDestinationIdAndCategory(Long destinationId, String category);
    
    @Query("SELECT a FROM Attraction a WHERE a.destinationId = :destinationId " +
           "AND a.travelStyle = :travelStyle ORDER BY a.rating DESC")
    List<Attraction> findTopAttractionsByDestinationAndStyle(
        @Param("destinationId") Long destinationId,
        @Param("travelStyle") String travelStyle
    );
    
    @Query("SELECT a FROM Attraction a WHERE a.destinationId = :destinationId " +
           "AND a.difficultyLevel = :difficultyLevel ORDER BY a.rating DESC LIMIT :limit")
    List<Attraction> findAttractionsByDifficultyLevel(
        @Param("destinationId") Long destinationId,
        @Param("difficultyLevel") String difficultyLevel,
        @Param("limit") int limit
    );
    
    @Query("SELECT a FROM Attraction a WHERE a.destinationId = :destinationId " +
           "ORDER BY a.rating DESC LIMIT :limit")
    List<Attraction> findTopAttractionsByDestination(
        @Param("destinationId") Long destinationId,
        @Param("limit") int limit
    );
}
