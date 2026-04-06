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
@Table(name = "attractions")
public class Attraction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "destination_id", nullable = false)
    private Long destinationId;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "LONGTEXT")
    private String description;
    
    @Column(nullable = false)
    private String category; // Historical, Museum, Beach, Adventure, etc.
    
    private Double latitude;
    private Double longitude;
    
    @Column(name = "estimated_duration_hours")
    private Integer estimatedDurationHours;
    
    @Column(name = "entry_fee_bdt")
    private Integer entryFeeBdt;
    
    @Column(name = "best_time_to_visit")
    private String bestTimeToVisit;
    
    @Column(name = "rating")
    private Double rating;
    
    @Column(name = "visit_count")
    private Integer visitCount;
    
    @Column(name = "travel_style")
    private String travelStyle; // cultural, adventure, relaxation, family, solo
    
    @Column(name = "difficulty_level")
    private String difficultyLevel; // easy, moderate, hard
    
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
