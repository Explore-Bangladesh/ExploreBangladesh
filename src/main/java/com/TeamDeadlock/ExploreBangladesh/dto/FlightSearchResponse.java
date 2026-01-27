package com.TeamDeadlock.ExploreBangladesh.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * DTO for flight search response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightSearchResponse {
    private List<FlightOffer> flights;
    private String currency;
    private int totalResults;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlightOffer {
        private String id;
        private String price;
        private String currency;
        private List<Itinerary> itineraries;
        private int numberOfStops;
        private String airline;
        private String airlineName;
        private boolean instantTicketingRequired;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Itinerary {
        private String duration;
        private List<Segment> segments;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Segment {
        private String departureAirport;
        private String departureCity;
        private String departureTime;
        private String arrivalAirport;
        private String arrivalCity;
        private String arrivalTime;
        private String flightNumber;
        private String carrierCode;
        private String carrierName;
        private String aircraft;
        private String duration;
        private String cabinClass;
    }
}
