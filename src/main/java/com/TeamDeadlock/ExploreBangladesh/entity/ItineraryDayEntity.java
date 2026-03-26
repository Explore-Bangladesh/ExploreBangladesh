package com.TeamDeadlock.ExploreBangladesh.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "itinerary_days")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItineraryDayEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    @JsonIgnore
    private TravelPlanEntity travelPlan;

    @Column(nullable = false)
    private Integer dayNumber;

    /** e.g. "Arrival & Beach Exploration" */
    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String summary;

    @OneToMany(mappedBy = "itineraryDay", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy("sortOrder ASC")
    private List<ItineraryActivityEntity> activities = new ArrayList<>();

    /** Constructor without relationships (for seeding) */
    public ItineraryDayEntity(TravelPlanEntity travelPlan, int dayNumber, String title, String summary) {
        this.travelPlan = travelPlan;
        this.dayNumber = dayNumber;
        this.title = title;
        this.summary = summary;
    }
}
