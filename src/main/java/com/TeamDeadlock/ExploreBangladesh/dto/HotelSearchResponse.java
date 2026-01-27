package com.TeamDeadlock.ExploreBangladesh.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * DTO for hotel search response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelSearchResponse {
    private List<Hotel> hotels;
    private Integer totalResults;
    private String searchedDestination;
    
    // For Geoapify response
    private List<Feature> features;
    
    // Geoapify Feature wrapper
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Feature {
        private String type;
        private Properties properties;
        private Geometry geometry;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Properties {
        private String name;
        private String city;
        private String formatted;
        private String addressLine1;
        private String addressLine2;
        private String placeId;
        private List<String> categories;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Geometry {
        private String type;
        private List<Double> coordinates; // [lon, lat]
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Hotel {
        private String id;
        private String name;
        private String address;
        private Double starRating;
        private Double guestRating;
        private Integer reviewCount;
        private String imageUrl;
        private Price price;
        private List<String> amenities;
        private String description;
        private Coordinates coordinates;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Price {
        private Double amount;
        private String currency;
        private String displayPrice; // e.g., "৳5,500 per night"
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Coordinates {
        private Double latitude;
        private Double longitude;
    }
}
