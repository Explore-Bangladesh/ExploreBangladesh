package com.TeamDeadlock.ExploreBangladesh.service;

import com.TeamDeadlock.ExploreBangladesh.dto.HotelSearchRequest;
import com.TeamDeadlock.ExploreBangladesh.dto.HotelSearchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HotelsApiService {

    @Value("${geoapify.api.key}")
    private String geoapifyApiKey;

    @Value("${geoapify.base-url}")
    private String geoapifyBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    // Hardcoded coordinates for Bangladesh cities/districts
    private static final Map<String, double[]> CITY_COORDS = new HashMap<>();

    static {
        // Major cities
        CITY_COORDS.put("dhaka", new double[]{90.4125, 23.8103});
        CITY_COORDS.put("cox's bazar", new double[]{91.9670, 21.4272});
        CITY_COORDS.put("coxsbazar", new double[]{91.9670, 21.4272});
        CITY_COORDS.put("chittagong", new double[]{91.7832, 22.3569});
        CITY_COORDS.put("sylhet", new double[]{91.8687, 24.8949});
        CITY_COORDS.put("khulna", new double[]{89.5690, 22.8456});
        CITY_COORDS.put("rajshahi", new double[]{88.6042, 24.3745});
        CITY_COORDS.put("rangpur", new double[]{89.2517, 25.7439});
        CITY_COORDS.put("barishal", new double[]{90.3667, 22.7010});
        CITY_COORDS.put("mymensingh", new double[]{90.4066, 24.7471});
        
        // Hill districts
        CITY_COORDS.put("rangamati", new double[]{92.1821, 22.6372});
        CITY_COORDS.put("bandarban", new double[]{92.2184, 22.1953});
        
        // Additional districts
        CITY_COORDS.put("tangail", new double[]{89.9168, 24.2513});
    }

    public HotelSearchResponse searchHotels(HotelSearchRequest request) {

        String destination = request.getDestination();
        if (destination == null || destination.trim().isEmpty()) {
            destination = "Dhaka";
        }

        // Get coordinates for the destination
        double[] coords = CITY_COORDS.get(destination.toLowerCase().trim());

        if (coords == null) {
            throw new RuntimeException("City not supported yet: " + destination);
        }

        double lon = coords[0];
        double lat = coords[1];

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
