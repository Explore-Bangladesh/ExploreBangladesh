package com.TeamDeadlock.ExploreBangladesh.planner.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entity representing destination information
 * Stores cached data about travel destinations for smart recommendations
 */
@Entity
@Table(name = "destination_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DestinationInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "destination_id")
    private Long destinationId;

    @Column(name = "city_name", nullable = false, unique = true)
    private String cityName;

    @Column(name = "country")
    private String country;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "best_month_from")
    private Integer bestMonthFrom; // 1-12

    @Column(name = "best_month_to")
    private Integer bestMonthTo; // 1-12

    @Column(name = "suggested_duration_days")
    private Integer suggestedDurationDays;

    @Column(name = "safety_rating")
    private Double safetyRating; // 0.0 to 5.0

    @Column(name = "language")
    private String language;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", updatable = false)
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
