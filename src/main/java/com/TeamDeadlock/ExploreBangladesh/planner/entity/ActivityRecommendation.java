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
@Table(name = "activity_recommendations")
public class ActivityRecommendation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "attraction_id", nullable = false)
    private Long attractionId;
    
    @Column(name = "travel_style", nullable = false)
    private String travelStyle;
    
    @Column(name = "duration_hours")
    private Integer durationHours;
    
    @Column(name = "recommended_time_slot")
    private String recommendedTimeSlot; // morning, afternoon, evening, night
    
    @Column(name = "cost_estimation_bdt")
    private Integer costEstimationBdt;
    
    @Column(name = "suitable_for_age_group")
    private String suitableForAgeGroup;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
