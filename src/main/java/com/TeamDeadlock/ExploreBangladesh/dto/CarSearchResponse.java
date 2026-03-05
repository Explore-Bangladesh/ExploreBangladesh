package com.TeamDeadlock.ExploreBangladesh.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarSearchResponse {
    private List<Car> cars;
    private int totalResults;
    private String searchedLocation;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Car {
        private String id;
        private String name;
        private String type;       // e.g., "Sedan", "SUV", "Micro"
        private int seats;
        private double price;      // per day in BDT
        private String imageUrl;
    }
}
