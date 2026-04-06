package com.TeamDeadlock.ExploreBangladesh.planner.repository;

import com.TeamDeadlock.ExploreBangladesh.planner.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    
    List<Restaurant> findByDestinationId(Long destinationId);
    
    List<Restaurant> findByDestinationIdAndCuisineType(Long destinationId, String cuisineType);
    
    @Query("SELECT r FROM Restaurant r WHERE r.destinationId = :destinationId " +
           "AND r.vegetarianOptions = true ORDER BY r.averageRating DESC")
    List<Restaurant> findVegetarianRestaurantsByDestination(@Param("destinationId") Long destinationId);
    
    @Query("SELECT r FROM Restaurant r WHERE r.destinationId = :destinationId " +
           "AND r.priceRange = :priceRange ORDER BY r.averageRating DESC")
    List<Restaurant> findRestaurantsByDestinationAndPriceRange(
        @Param("destinationId") Long destinationId,
        @Param("priceRange") String priceRange
    );
    
    @Query("SELECT r FROM Restaurant r WHERE r.destinationId = :destinationId " +
           "ORDER BY r.averageRating DESC LIMIT :limit")
    List<Restaurant> findTopRestaurantsByDestination(
        @Param("destinationId") Long destinationId,
        @Param("limit") int limit
    );
}
