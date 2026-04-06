package com.TeamDeadlock.ExploreBangladesh.planner.repository;

import com.TeamDeadlock.ExploreBangladesh.planner.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    
    List<Hotel> findByDestinationId(Long destinationId);
    
    @Query("SELECT h FROM Hotel h WHERE h.destinationId = :destinationId " +
           "AND h.economyPriceBdt <= :maxPrice ORDER BY h.averageRating DESC")
    List<Hotel> findHotelsByDestinationAndBudget(
        @Param("destinationId") Long destinationId,
        @Param("maxPrice") Integer maxPrice
    );
    
    @Query("SELECT h FROM Hotel h WHERE h.destinationId = :destinationId " +
           "AND h.starRating >= :minStars ORDER BY h.averageRating DESC")
    List<Hotel> findHotelsByDestinationAndStarRating(
        @Param("destinationId") Long destinationId,
        @Param("minStars") Integer minStars
    );
    
    @Query("SELECT h FROM Hotel h WHERE h.destinationId = :destinationId " +
           "ORDER BY h.averageRating DESC LIMIT :limit")
    List<Hotel> findTopHotelsByDestination(
        @Param("destinationId") Long destinationId,
        @Param("limit") int limit
    );
}
