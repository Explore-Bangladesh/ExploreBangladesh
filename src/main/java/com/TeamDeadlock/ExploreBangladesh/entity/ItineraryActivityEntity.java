package com.TeamDeadlock.ExploreBangladesh.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "itinerary_activities")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItineraryActivityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "day_id", nullable = false)
    @JsonIgnore
    private ItineraryDayEntity itineraryDay;

    /** e.g. "06:00" */
    private String startTime;

    /** e.g. "07:30" */
    private String endTime;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    /** TRAVEL, FOOD, SIGHTSEEING, REST, ACTIVITY */
    @Column(nullable = false)
    private String activityType;

    private String location;

    /** Estimated cost for this activity in BDT */
    private Double estimatedCost;

    @Column(length = 500)
    private String tips;

    /** Sort order within the day */
    @Column(nullable = false)
    private Integer sortOrder;

    /** Constructor for seeding */
    public ItineraryActivityEntity(ItineraryDayEntity day, String startTime, String endTime,
                                    String title, String description, String activityType,
                                    String location, Double estimatedCost, String tips, int sortOrder) {
        this.itineraryDay = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.title = title;
        this.description = description;
        this.activityType = activityType;
        this.location = location;
        this.estimatedCost = estimatedCost;
        this.tips = tips;
        this.sortOrder = sortOrder;
    }
}
