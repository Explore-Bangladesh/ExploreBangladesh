package com.TeamDeadlock.ExploreBangladesh.dto;

import java.util.List;

public class PlaceRecommendationResponse {

    private String location;
    private int totalResults;
    private List<Place> places;

    public PlaceRecommendationResponse() {}

    public PlaceRecommendationResponse(String location, List<Place> places) {
        this.location = location;
        this.places = places;
        this.totalResults = places != null ? places.size() : 0;
    }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getTotalResults() { return totalResults; }
    public void setTotalResults(int totalResults) { this.totalResults = totalResults; }

    public List<Place> getPlaces() { return places; }
    public void setPlaces(List<Place> places) {
        this.places = places;
        this.totalResults = places != null ? places.size() : 0;
    }

    public static class Place {
        private String id;
        private String name;
        private String category;
        private String description;
        private String imageUrl;
        private double rating;
        private String distanceNote;
        private double latitude;
        private double longitude;
        private List<String> highlights;
        private String entranceFee;
        private String bestTimeToVisit;
        private String openingHours;
        private int estimatedDuration; // in minutes
        private String accessibility;
        
        // New fields for detailed information
        private String howToGo;           // Travel instructions
        private String upazila;           // Upazila/sub-district name
        private String nearbyHotels;      // Hotel information or message
        private String nearbyCarRentals;  // Car rental information or message
        private String availableGuides;   // Guide service information or message

        public Place() {}

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

        public double getRating() { return rating; }
        public void setRating(double rating) { this.rating = rating; }

        public String getDistanceNote() { return distanceNote; }
        public void setDistanceNote(String distanceNote) { this.distanceNote = distanceNote; }

        public double getLatitude() { return latitude; }
        public void setLatitude(double latitude) { this.latitude = latitude; }

        public double getLongitude() { return longitude; }
        public void setLongitude(double longitude) { this.longitude = longitude; }

        public List<String> getHighlights() { return highlights; }
        public void setHighlights(List<String> highlights) { this.highlights = highlights; }

        public String getEntranceFee() { return entranceFee; }
        public void setEntranceFee(String entranceFee) { this.entranceFee = entranceFee; }

        public String getBestTimeToVisit() { return bestTimeToVisit; }
        public void setBestTimeToVisit(String bestTimeToVisit) { this.bestTimeToVisit = bestTimeToVisit; }

        public String getOpeningHours() { return openingHours; }
        public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }

        public int getEstimatedDuration() { return estimatedDuration; }
        public void setEstimatedDuration(int estimatedDuration) { this.estimatedDuration = estimatedDuration; }

        public String getAccessibility() { return accessibility; }
        public void setAccessibility(String accessibility) { this.accessibility = accessibility; }

        public String getHowToGo() { return howToGo; }
        public void setHowToGo(String howToGo) { this.howToGo = howToGo; }

        public String getUpazila() { return upazila; }
        public void setUpazila(String upazila) { this.upazila = upazila; }

        public String getNearbyHotels() { return nearbyHotels; }
        public void setNearbyHotels(String nearbyHotels) { this.nearbyHotels = nearbyHotels; }

        public String getNearbyCarRentals() { return nearbyCarRentals; }
        public void setNearbyCarRentals(String nearbyCarRentals) { this.nearbyCarRentals = nearbyCarRentals; }

        public String getAvailableGuides() { return availableGuides; }
        public void setAvailableGuides(String availableGuides) { this.availableGuides = availableGuides; }
    }
}
