package com.TeamDeadlock.ExploreBangladesh.service;

import com.TeamDeadlock.ExploreBangladesh.dto.HotelSearchRequest;
import com.TeamDeadlock.ExploreBangladesh.dto.HotelSearchResponse;
import com.TeamDeadlock.ExploreBangladesh.entity.CityCoordinate;
import com.TeamDeadlock.ExploreBangladesh.repository.CityCoordinateRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Service
public class HotelsApiService {

    @Value("${geoapify.api.key}")
    private String geoapifyApiKey;

    @Value("${geoapify.base-url}")
    private String geoapifyBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final CityCoordinateRepository cityCoordinateRepository;

    public HotelsApiService(CityCoordinateRepository cityCoordinateRepository) {
        this.cityCoordinateRepository = cityCoordinateRepository;
    }

    public HotelSearchResponse searchHotels(HotelSearchRequest request) {

        String rawDestination = request.getDestination();
        String destination = (rawDestination == null || rawDestination.trim().isEmpty()) ? "Dhaka" : rawDestination;

        // Look up coordinates for the destination from the database
        CityCoordinate coord = cityCoordinateRepository
                .findByCityKey(destination.toLowerCase().trim())
                .orElseThrow(() -> new RuntimeException("City not supported yet: " + destination));

        double lon = coord.getLongitude();
        double lat = coord.getLatitude();

        // Build Geoapify Places API request
        String filter = String.format("circle:%f,%f,10000", lon, lat); // 10km radius

        String url = UriComponentsBuilder.fromUriString(geoapifyBaseUrl)
                .queryParam("categories", "accommodation.hotel")
                .queryParam("filter", filter)
                .queryParam("limit", 120)
                .queryParam("apiKey", geoapifyApiKey)
                .build()
                .toUriString();

        System.out.println("Geoapify URL: " + url);

        @SuppressWarnings("unchecked")
        Map<String, Object> geoapifyResponse = restTemplate.getForObject(url, Map.class);

        HotelSearchResponse response = new HotelSearchResponse();
        response.setFeatures((List<HotelSearchResponse.Feature>) geoapifyResponse.get("features"));

        return response;
    }
}
