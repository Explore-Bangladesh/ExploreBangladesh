package com.TeamDeadlock.ExploreBangladesh.planner.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transport_routes")
public class TransportRoute {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "source_destination_id", nullable = false)
    private Long sourceDestinationId;
    
    @Column(name = "target_destination_id", nullable = false)
    private Long targetDestinationId;
    
    @Column(name = "transport_type", nullable = false)
    private String transportType; // bus, train, flight, car
    
    @Column(name = "distance_km")
    private Integer distanceKm;
    
    @Column(name = "travel_time_hours")
    private BigDecimal travelTimeHours;
    
    @Column(name = "cost_economy_bdt")
    private Integer costEconomyBdt;
    
    @Column(name = "cost_midrange_bdt")
    private Integer costMidrangeBdt;
    
    @Column(name = "cost_luxury_bdt")
    private Integer costLuxuryBdt;
    
    @Column(length = 500)
    private String notes;
    
    @Column(name = "created_at", nullable = false, updatable = false)
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
