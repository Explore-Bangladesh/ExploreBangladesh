package com.TeamDeadlock.ExploreBangladesh.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuideSearchResponse {
    private List<Guide> guides;
    private int totalResults;
    private String searchedCity;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Guide {
        private String id;
        private String name;
        private String city;
        private int experienceYears;
        private List<String> languages;
        private double rating;
        private String imageUrl;
    }
}
