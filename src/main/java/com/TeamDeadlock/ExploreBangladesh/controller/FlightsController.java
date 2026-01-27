package com.TeamDeadlock.ExploreBangladesh.controller;

import com.TeamDeadlock.ExploreBangladesh.dto.FlightSearchRequest;
import com.TeamDeadlock.ExploreBangladesh.dto.FlightSearchResponse;
import com.TeamDeadlock.ExploreBangladesh.service.FlightsApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/flights")
@CrossOrigin(origins = "*")
public class FlightsController {

    private final FlightsApiService flightsApiService;

    public FlightsController(FlightsApiService flightsApiService) {
        this.flightsApiService = flightsApiService;
    }

    /**
     * Search for flights
     * POST /api/flights/search
     */
    @PostMapping("/search")
    public ResponseEntity<FlightSearchResponse> searchFlights(@RequestBody FlightSearchRequest request) {
        // Set defaults if not provided
        if (request.getOrigin() == null || request.getOrigin().trim().isEmpty()) {
            request.setOrigin("DAC"); // Default: Dhaka
        }
        if (request.getAdults() == null || request.getAdults() < 1) {
            request.setAdults(1);
        }

        FlightSearchResponse response = flightsApiService.searchFlights(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get list of supported airports
     * GET /api/flights/airports
     */
    @GetMapping("/airports")
    public ResponseEntity<Map<String, String>> getAirports() {
        return ResponseEntity.ok(flightsApiService.getSupportedAirports());
    }

    /**
     * Quick test endpoint
     * GET /api/flights/test
     */
    @GetMapping("/test")
    public ResponseEntity<FlightSearchResponse> testFlights() {
        FlightSearchRequest req = new FlightSearchRequest();
        req.setOrigin("DAC");
        req.setDestination("CXB");
        req.setDepartureDate("2026-02-15");
        req.setAdults(1);
        return ResponseEntity.ok(flightsApiService.searchFlights(req));
    }
}
