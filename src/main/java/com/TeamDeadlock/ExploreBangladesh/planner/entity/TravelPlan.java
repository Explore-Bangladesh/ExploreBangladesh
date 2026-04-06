package com.TeamDeadlock.ExploreBangladesh.planner.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing a user's smart travel plan
 * Contains high-level information about the planned trip
 */
@Entity
@Table(name = "travel_plans", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_destination", columnList = "destination")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TravelPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long planId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "destination", nullable = false)
    private String destination;

    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;

    @Column(name = "budget_tier", nullable = false)
    private String budgetTier; // economy, mid_range, luxury

    @Column(name = "travel_style")
    private String travelStyle; // adventure, relaxation, cultural, family, solo

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "status")
    private String status; // draft, active, completed, cancelled

    @Column(name = "total_budget_estimate", precision = 12, scale = 2)
    private BigDecimal totalBudgetEstimate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "plan_data", columnDefinition = "LONGTEXT")
    private String planData; // Stores the complete EnhancedTravelPlanDTO as JSON

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = "draft";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
