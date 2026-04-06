package com.TeamDeadlock.ExploreBangladesh.planner.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entity representing system-generated insights and tips for travel plans
 * Includes weather warnings, safety alerts, crowd tips, etc.
 */
@Entity
@Table(name = "travel_insights", indexes = {
    @Index(name = "idx_plan_id", columnList = "plan_id"),
    @Index(name = "idx_insight_type", columnList = "insight_type")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TravelInsight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "insight_id")
    private Long insightId;

    @Column(name = "plan_id", nullable = false)
    private Long planId;

    @Column(name = "insight_type", nullable = false)
    private String insightType; // weather, crowd, safety, local_customs, budget, timing

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "severity")
    private String severity; // info, warning, critical

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (severity == null) {
            severity = "info";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
