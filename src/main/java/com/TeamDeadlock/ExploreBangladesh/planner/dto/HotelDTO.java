package com.TeamDeadlock.ExploreBangladesh.planner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelDTO {
    private Long id;
    private String name;
    private String description;
    private String address;
    private Double latitude;
    private Double longitude;
    private Integer starRating;
    private String phone;
    private String email;
    private String website;
    private Integer economyPriceBdt;
    private Integer midrangePriceBdt;
    private Integer luxuryPriceBdt;
    private String amenities;
    private Double averageRating;
    private Integer reviewCount;
}
