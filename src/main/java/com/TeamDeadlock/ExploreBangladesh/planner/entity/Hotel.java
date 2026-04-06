package com.TeamDeadlock.ExploreBangladesh.planner.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hotels")
public class Hotel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "destination_id", nullable = false)
    private Long destinationId;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "LONGTEXT")
    private String description;
    
    private String address;
    private Double latitude;
    private Double longitude;
    
    @Column(name = "star_rating")
    private Integer starRating;
    
    private String phone;
    private String email;
    private String website;
    
    @Column(name = "economy_price_bdt")
    private Integer economyPriceBdt;
    
    @Column(name = "midrange_price_bdt")
    private Integer midrangePriceBdt;
    
    @Column(name = "luxury_price_bdt")
    private Integer luxuryPriceBdt;
    
    @Column(length = 500)
    private String amenities;
    
    @Column(name = "average_rating")
    private Double averageRating;
    
    @Column(name = "review_count")
    private Integer reviewCount;
    
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
