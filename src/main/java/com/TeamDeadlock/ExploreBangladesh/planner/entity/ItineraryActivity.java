package com.TeamDeadlock.ExploreBangladesh.planner.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.LocalDateTime;

/**
 * Entity representing individual activities within a daily itinerary
 * Each activity has timing, location, cost, and type information
 */
@Entity
@Table(name = "itinerary_activities", indexes = {
    @Index(name = "idx_itinerary_id", columnList = "itinerary_id"),
    @Index(name = "idx_activity_type", columnList = "activity_type"),
    @Index(name = "idx_order", columnList = "order_index")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItineraryActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_id")
    private Long activityId;

    @Column(name = "itinerary_id", nullable = false)
    private Long itineraryId;

    @Column(name = "activity_type", nullable = false)
    private String activityType; // attraction, meal, transport, rest, shopping

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "location_lat")
    private Double locationLat;

    @Column(name = "location_lng")
    private Double locationLng;

    @Column(name = "address")
    private String address;

    @Column(name = "estimated_cost", precision = 10, scale = 2)
    private BigDecimal estimatedCost;

    @Column(name = "booking_required")
    private Boolean bookingRequired;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

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
        if (bookingRequired == null) {
            bookingRequired = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
