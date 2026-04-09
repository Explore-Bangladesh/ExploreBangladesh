package com.TeamDeadlock.ExploreBangladesh.service;

import com.TeamDeadlock.ExploreBangladesh.dto.PlaceRecommendationResponse;
import com.TeamDeadlock.ExploreBangladesh.dto.PlaceRecommendationResponse.Place;
import com.TeamDeadlock.ExploreBangladesh.entity.PlaceEntity;
import com.TeamDeadlock.ExploreBangladesh.repository.PlaceRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlacesRecommendationService {

    private final PlaceRepository placeRepository;

    public PlacesRecommendationService(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    /**
     * Return recommended places for the given location name.
     * The lookup is case-insensitive and tolerates extra spaces.
     */
    public PlaceRecommendationResponse getRecommendations(String location) {
        String key = location == null ? "" : location.trim().toLowerCase();
        
        // Try exact match first
        List<PlaceEntity> placeEntities = placeRepository.findByLocation(key);

        // If no exact match, try partial match
        if (placeEntities.isEmpty()) {
            placeEntities = placeRepository.findByLocationIgnoreCase(location);
        }

        // Convert entities to DTOs
        List<Place> places = placeEntities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        String displayLocation = location == null ? "Unknown" : capitalizeFirstLetter(location.trim());
        return new PlaceRecommendationResponse(displayLocation, places);
    }

    /**
     * Returns all supported location names (sorted).
     */
    public List<String> getSupportedLocations() {
        return placeRepository.findDistinctLocations().stream()
                .map(this::capitalizeFirstLetter)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Get recommendations with sorting options
     */
    public PlaceRecommendationResponse getRecommendationsFiltered(
            String location, String sortBy, String category, String searchQuery) {
        
        PlaceRecommendationResponse response = getRecommendations(location);
        List<Place> places = response.getPlaces();

        if (places == null || places.isEmpty()) {
            return response;
        }

        // Apply category filter
        if (category != null && !category.trim().isEmpty() && !"all".equalsIgnoreCase(category)) {
            places = places.stream()
                    .filter(p -> category.equalsIgnoreCase(p.getCategory()))
                    .collect(Collectors.toList());
        }

        // Apply search query
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            String query = searchQuery.toLowerCase().trim();
            places = places.stream()
                    .filter(p -> 
                        (p.getName() != null && p.getName().toLowerCase().contains(query)) ||
                        (p.getDescription() != null && p.getDescription().toLowerCase().contains(query)) ||
                        (p.getHighlights() != null && p.getHighlights().stream()
                                .anyMatch(h -> h.toLowerCase().contains(query)))
                    )
                    .collect(Collectors.toList());
        }

        // Apply sorting
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            switch (sortBy.toLowerCase()) {
                case "rating":
                    places.sort((a, b) -> Double.compare(b.getRating(), a.getRating()));
                    break;
                case "name":
                    places.sort(Comparator.comparing(Place::getName));
                    break;
                case "category":
                    places.sort(Comparator.comparing(Place::getCategory)
                            .thenComparing((a, b) -> Double.compare(b.getRating(), a.getRating())));
                    break;
                default:
                    // Default: sort by rating
                    places.sort((a, b) -> Double.compare(b.getRating(), a.getRating()));
                    break;
            }
        }

        response.setPlaces(places);
        return response;
    }

    /**
     * Get top-rated places across all locations
     */
    public List<Place> getPopularPlaces(int limit) {
        return placeRepository.findTopRatedPlaces().stream()
                .limit(limit > 0 ? limit : 10)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all available categories
     */
    public List<String> getCategories() {
        return placeRepository.findDistinctCategories().stream()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Get places by category across all locations
     */
    public List<Place> getPlacesByCategory(String category, int limit) {
        return placeRepository.findByCategoryIgnoreCase(category).stream()
                .sorted((a, b) -> Double.compare(
                        b.getRating() != null ? b.getRating() : 0.0, 
                        a.getRating() != null ? a.getRating() : 0.0))
                .limit(limit > 0 ? limit : 20)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search places across all locations
     */
    public List<Place> searchPlaces(String query, int limit) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }

        return placeRepository.searchByKeyword(query.trim()).stream()
                .sorted((a, b) -> Double.compare(
                        b.getRating() != null ? b.getRating() : 0.0, 
                        a.getRating() != null ? a.getRating() : 0.0))
                .limit(limit > 0 ? limit : 50)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert PlaceEntity to DTO Place object
     */
    private Place convertToDTO(PlaceEntity entity) {
        Place place = new Place();
        place.setId(entity.getId());
        place.setName(entity.getName());
        place.setCategory(entity.getCategory());
        place.setDescription(entity.getDescription());
        place.setImageUrl(entity.getImageUrl());
        place.setRating(entity.getRating() != null ? entity.getRating() : 0.0);
        place.setDistanceNote(entity.getDistanceNote());
        place.setLatitude(entity.getLatitude() != null ? entity.getLatitude() : 0.0);
        place.setLongitude(entity.getLongitude() != null ? entity.getLongitude() : 0.0);
        place.setEntranceFee(entity.getEntranceFee());
        place.setBestTimeToVisit(entity.getBestTimeToVisit());
        place.setOpeningHours(entity.getOpeningHours());
        place.setEstimatedDuration(entity.getEstimatedDuration() != null ? entity.getEstimatedDuration() : 0);
        place.setAccessibility(entity.getAccessibility());
        place.setUpazila(entity.getUpazila());
        place.setHowToGo(entity.getHowToGo() != null ? entity.getHowToGo() : 
                        generateDefaultHowToGo(entity.getDistanceNote(), entity.getName()));
        place.setNearbyHotels(entity.getNearbyHotels() != null ? entity.getNearbyHotels() : 
                             "Contact local hotels or use our Hotels search page.");
        place.setNearbyCarRentals(entity.getNearbyCarRentals() != null ? entity.getNearbyCarRentals() : 
                                 "Car rentals available through our Cars page.");
        place.setAvailableGuides(entity.getAvailableGuides() != null ? entity.getAvailableGuides() : 
                                "Local guides available. Check our Travel Guide page.");
        place.setHighlights(entity.getHighlights() != null ? entity.getHighlights() : new ArrayList<>());
        return place;
    }

    /**
     * Generate default "how to go" instructions
     */
    private String generateDefaultHowToGo(String distanceNote, String placeName) {
        if (distanceNote == null || distanceNote.isEmpty()) {
            return "Accessible by local transport. Check Google Maps for detailed directions.";
        }
        
        if (distanceNote.toLowerCase().contains("km")) {
            return "Take local bus, CNG, or hire a private car. Uber/Pathao may be available in major cities. " +
                   "Check Google Maps for best route.";
        }
        
        if (distanceNote.toLowerCase().contains("city center") || distanceNote.toLowerCase().contains("town")) {
            return "Located in city center. Easily accessible by rickshaw, CNG, or walking. " +
                   "Uber/Pathao available in major cities.";
        }
        
        return "Accessible by local transport. Private car recommended for comfort. " +
               "Check with local tourism office or hotel for transport options.";
    }

    /**
     * Capitalize first letter of each word
     */
    private String capitalizeFirstLetter(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        String[] words = text.split("\\s+");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                result.append(Character.toUpperCase(word.charAt(0)))
                      .append(word.substring(1).toLowerCase())
                      .append(" ");
            }
        }
        return result.toString().trim();
    }
}
