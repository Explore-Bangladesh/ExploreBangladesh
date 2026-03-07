package com.TeamDeadlock.ExploreBangladesh.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "places")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceEntity {
    
    @Id
    private String id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String category;
    
    @Column(length = 2000)
    private String description;
    
    private String imageUrl;
    
    private Double rating;
    
    private String distanceNote;
    
    @Column(nullable = false)
    private String location;  // city/location name (normalized lowercase)
    
    private Double latitude;
    
    private Double longitude;
    
    private String entranceFee;
    
    private String bestTimeToVisit;
    
    private String openingHours;
    
    private Integer estimatedDuration;  // minutes
    
    @Column(length = 1000)
    private String accessibility;
    
    private String upazila;
    
    @Column(length = 1000)
    private String howToGo;
    
    @Column(length = 1000)
    private String nearbyHotels;
    
    @Column(length = 1000)
    private String nearbyCarRentals;
    
    @Column(length = 1000)
    private String availableGuides;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "place_highlights", joinColumns = @JoinColumn(name = "place_id"))
    @Column(name = "highlight")
    private List<String> highlights = new ArrayList<>();
    
    // Constructor for basic places (backward compatibility)
    public PlaceEntity(String id, String name, String category, String description, 
                      String imageUrl, Double rating, String distanceNote, 
                      String location, List<String> highlights) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.distanceNote = distanceNote;
        this.location = location.toLowerCase();
        this.highlights = highlights != null ? highlights : new ArrayList<>();
    }
}
