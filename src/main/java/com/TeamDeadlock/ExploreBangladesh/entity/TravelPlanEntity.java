package com.TeamDeadlock.ExploreBangladesh.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "travel_plans_legacy")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TravelPlanEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String destination;

    @Column(length = 512)
    private String destinationImage;

    /** BUDGET, STANDARD, or PREMIUM */
    @Column(nullable = false)
    private String budgetTier;

    @Column(nullable = false)
    private Integer durationDays;

    @Column(nullable = false)
    private Integer durationNights;

    /** Total estimated cost in BDT */
    @Column(nullable = false)
    private Double totalCost;

    @Column(length = 2000)
    private String description;

    private String bestTimeToVisit;

    private String groupSize;

    /** Bangladesh division name */
    @Column(nullable = false)
    private String division;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "travel_plan_highlights", joinColumns = @JoinColumn(name = "plan_id"))
    @Column(name = "highlight")
    private List<String> highlights = new ArrayList<>();

    @OneToMany(mappedBy = "travelPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("dayNumber ASC")
    private List<ItineraryDayEntity> days = new ArrayList<>();

    @OneToMany(mappedBy = "travelPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CostBreakdownEntity> costBreakdown = new ArrayList<>();

    /** Constructor without relationships (for seeding) */
    public TravelPlanEntity(String id, String destination, String destinationImage,
                            String budgetTier, int durationDays, int durationNights,
                            double totalCost, String description, String bestTimeToVisit,
                            String groupSize, String division, List<String> highlights) {
        this.id = id;
        this.destination = destination;
        this.destinationImage = destinationImage;
        this.budgetTier = budgetTier;
        this.durationDays = durationDays;
        this.durationNights = durationNights;
        this.totalCost = totalCost;
        this.description = description;
        this.bestTimeToVisit = bestTimeToVisit;
        this.groupSize = groupSize;
        this.division = division;
        this.highlights = highlights != null ? highlights : new ArrayList<>();
    }
}
