package com.TeamDeadlock.ExploreBangladesh.planner.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "restaurants")
public class Restaurant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "destination_id", nullable = false)
    private Long destinationId;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "LONGTEXT")
    private String description;
    
    @Column(name = "cuisine_type", nullable = false)
    private String cuisineType;
    
    private String address;
    private Double latitude;
    private Double longitude;
    
    @Column(name = "price_range")
    private String priceRange; // budget, midrange, luxury
    
    @Column(name = "average_meal_cost_bdt")
    private Integer averageMealCostBdt;
    
    @Column(name = "operating_hours")
    private String operatingHours;
    
    private String phone;
    
    @Column(name = "average_rating")
    private Double averageRating;
    
    @Column(name = "review_count")
    private Integer reviewCount;
    
    @Column(length = 500)
    private String specialties;
    
    @Column(name = "vegetarian_options")
    private Boolean vegetarianOptions;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
