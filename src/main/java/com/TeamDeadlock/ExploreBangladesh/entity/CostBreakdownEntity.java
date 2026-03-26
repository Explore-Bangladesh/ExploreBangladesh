package com.TeamDeadlock.ExploreBangladesh.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cost_breakdown")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CostBreakdownEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    @JsonIgnore
    private TravelPlanEntity travelPlan;

    /** TRANSPORT, ACCOMMODATION, FOOD, ACTIVITIES, MISCELLANEOUS */
    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private Double amount;

    @Column(length = 500)
    private String description;

    /** Constructor for seeding */
    public CostBreakdownEntity(TravelPlanEntity plan, String category, double amount, String description) {
        this.travelPlan = plan;
        this.category = category;
        this.amount = amount;
        this.description = description;
    }
}
