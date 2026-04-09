package com.TeamDeadlock.ExploreBangladesh.controller;

import com.TeamDeadlock.ExploreBangladesh.dto.PlaceRecommendationResponse;
import com.TeamDeadlock.ExploreBangladesh.dto.PlaceRecommendationResponse.Place;
import com.TeamDeadlock.ExploreBangladesh.service.PlacesRecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/places")
@CrossOrigin(origins = "*")
public class PlacesController {

    private final PlacesRecommendationService placesService;

    public PlacesController(PlacesRecommendationService placesService) {
        this.placesService = placesService;
    }

    /**
     * GET /api/places/nearby?location=Tangail&sortBy=rating&category=Heritage&search=mosque
     * Returns recommended visiting places for the given location with optional filtering and sorting.
     */
    @GetMapping("/nearby")
    public ResponseEntity<PlaceRecommendationResponse> getNearbyPlaces(
            @RequestParam(required = false, defaultValue = "Dhaka") String location,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {

        if (location.trim().isEmpty()) {
            location = "Dhaka";
        }

        PlaceRecommendationResponse response = placesService.getRecommendationsFiltered(
                location, sortBy, category, search);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/places/locations
     * Returns all supported location names.
     */
    @GetMapping("/locations")
    public ResponseEntity<List<String>> getSupportedLocations() {
        return ResponseEntity.ok(placesService.getSupportedLocations());
    }

    /**
     * GET /api/places/popular?limit=10
     * Returns top-rated places across all locations.
     */
    @GetMapping("/popular")
    public ResponseEntity<List<Place>> getPopularPlaces(
            @RequestParam(required = false, defaultValue = "10") int limit) {
        return ResponseEntity.ok(placesService.getPopularPlaces(limit));
    }

    /**
     * GET /api/places/categories
     * Returns all available categories.
     */
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(placesService.getCategories());
    }

    /**
     * GET /api/places/by-category?category=Heritage&limit=20
     * Returns places matching a specific category.
     */
    @GetMapping("/by-category")
    public ResponseEntity<List<Place>> getPlacesByCategory(
            @RequestParam String category,
            @RequestParam(required = false, defaultValue = "20") int limit) {
        return ResponseEntity.ok(placesService.getPlacesByCategory(category, limit));
    }

    /**
     * GET /api/places/search?q=mosque&limit=50
     * Search places across all locations.
     */
    @GetMapping("/search")
    public ResponseEntity<List<Place>> searchPlaces(
            @RequestParam String q,
            @RequestParam(required = false, defaultValue = "50") int limit) {
        return ResponseEntity.ok(placesService.searchPlaces(q, limit));
    }

    /**
     * Quick browser-testable endpoint.
     * GET /api/places/test
     */
    @GetMapping("/test")
    public ResponseEntity<PlaceRecommendationResponse> testPlaces() {
        return ResponseEntity.ok(placesService.getRecommendations("Sylhet"));
    }
}
