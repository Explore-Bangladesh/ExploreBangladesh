package com.TeamDeadlock.ExploreBangladesh.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO for flight search request from frontend
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightSearchRequest {
    private String origin;           // IATA code (e.g., "DAC")
    private String destination;      // IATA code (e.g., "CXB")
    private String departureDate;    // Format: YYYY-MM-DD
    private String returnDate;       // Optional, for round-trip
    private Integer adults;          // Number of adult passengers
    private String travelClass;      // ECONOMY, PREMIUM_ECONOMY, BUSINESS, FIRST
    private Boolean nonStop;         // Direct flights only
}
