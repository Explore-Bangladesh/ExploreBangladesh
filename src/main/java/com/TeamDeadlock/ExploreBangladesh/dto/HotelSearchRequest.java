package com.TeamDeadlock.ExploreBangladesh.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for hotel search request from frontend
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelSearchRequest {
    private String destination; // City name (e.g., "Dhaka", "Cox's Bazar")
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer adults;
    private String sortOrder; // "RECOMMENDED", "PRICE_LOW_TO_HIGH", "REVIEW_RATING"
    private Integer minPrice;
    private Integer maxPrice;
    private Integer minStarRating;
}
