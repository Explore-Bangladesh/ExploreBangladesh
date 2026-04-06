package com.TeamDeadlock.ExploreBangladesh.planner.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entity storing user travel preferences
 * Used for personalized recommendations and smarter itinerary generation
 */
@Entity
@Table(name = "travel_preferences", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TravelPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "preference_id")
    private Long preferenceId;

    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    @Column(name = "preferred_cuisine")
    private String preferredCuisine;

    @Column(name = "activity_pace")
    private String activityPace; // slow, moderate, fast

    @Column(name = "morning_person")
    private Boolean morningPerson;

    @Column(name = "nightlife_interest")
    private Boolean nightlifeInterest;

    @Column(name = "nature_interest")
    private Boolean natureInterest;

    @Column(name = "history_interest")
    private Boolean historyInterest;

    @Column(name = "adventure_interest")
    private Boolean adventureInterest;

    @Column(name = "shopping_interest")
    private Boolean shoppingInterest;

    @Column(name = "dietary_restrictions")
    private String dietaryRestrictions;

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
