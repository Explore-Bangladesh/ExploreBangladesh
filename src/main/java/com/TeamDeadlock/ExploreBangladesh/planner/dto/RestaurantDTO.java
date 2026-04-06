package com.TeamDeadlock.ExploreBangladesh.planner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDTO {
    private Long id;
    private String name;
    private String description;
    private String cuisineType;
    private String address;
    private Double latitude;
    private Double longitude;
    private String priceRange;
    private Integer averageMealCostBdt;
    private String operatingHours;
    private String phone;
    private Double averageRating;
    private Integer reviewCount;
    private String specialties;
    private Boolean vegetarianOptions;
}
