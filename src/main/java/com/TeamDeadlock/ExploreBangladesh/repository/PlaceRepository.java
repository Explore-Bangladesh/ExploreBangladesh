package com.TeamDeadlock.ExploreBangladesh.repository;

import com.TeamDeadlock.ExploreBangladesh.entity.PlaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceRepository extends JpaRepository<PlaceEntity, String> {
    
    // Find places by location (case-insensitive)
    List<PlaceEntity> findByLocationIgnoreCase(String location);
    
    // Find places by location (normalized lowercase - more efficient)
    List<PlaceEntity> findByLocation(String location);
    
    // Find places by category (case-insensitive)
    List<PlaceEntity> findByCategoryIgnoreCase(String category);
    
    // Search places by name or description
    @Query("SELECT p FROM PlaceEntity p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<PlaceEntity> searchByKeyword(@Param("keyword") String keyword);
    
    // Get all distinct locations
    @Query("SELECT DISTINCT p.location FROM PlaceEntity p ORDER BY p.location")
    List<String> findDistinctLocations();
    
    // Get all distinct categories
    @Query("SELECT DISTINCT p.category FROM PlaceEntity p ORDER BY p.category")
    List<String> findDistinctCategories();
    
    // Find top-rated places
    @Query("SELECT p FROM PlaceEntity p WHERE p.rating IS NOT NULL ORDER BY p.rating DESC")
    List<PlaceEntity> findTopRatedPlaces();
}
