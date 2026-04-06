package com.TeamDeadlock.ExploreBangladesh.planner.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing a daily breakdown of a travel plan
 * Contains information about what activities are planned for each day
 */
@Entity
@Table(name = "daily_itineraries", indexes = {
    @Index(name = "idx_plan_id", columnList = "plan_id"),
    @Index(name = "idx_date", columnList = "date"),
    @Index(name = "unique_plan_day", columnList = "plan_id, day_number", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyItinerary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "itinerary_id")
    private Long itineraryId;

    @Column(name = "plan_id", nullable = false)
    private Long planId;

    @Column(name = "day_number", nullable = false)
    private Integer dayNumber;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "theme")
    private String theme; // e.g., "Historical Sites Tour", "Beach Day"

    @Column(name = "weather_condition")
    private String weatherCondition; // e.g., "Sunny", "Rainy", "Cloudy"

    @Column(name = "estimated_cost", precision = 10, scale = 2)
    private BigDecimal estimatedCost;

    @Column(name = "estimated_steps")
    private Integer estimatedSteps;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (estimatedCost == null) {
            estimatedCost = BigDecimal.ZERO;
        }
        if (estimatedSteps == null) {
            estimatedSteps = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
