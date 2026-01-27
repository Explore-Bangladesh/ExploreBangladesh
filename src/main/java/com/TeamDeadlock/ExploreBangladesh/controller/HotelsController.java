package com.TeamDeadlock.ExploreBangladesh.controller;

import com.TeamDeadlock.ExploreBangladesh.dto.HotelSearchRequest;
import com.TeamDeadlock.ExploreBangladesh.dto.HotelSearchResponse;
import com.TeamDeadlock.ExploreBangladesh.service.HotelsApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hotels")
@CrossOrigin(origins = "*")
public class HotelsController {

    private final HotelsApiService hotelsApiService;

    public HotelsController(HotelsApiService hotelsApiService) {
        this.hotelsApiService = hotelsApiService;
    }

    @PostMapping("/search")
    public ResponseEntity<HotelSearchResponse> searchHotels(@RequestBody HotelSearchRequest request) {
        // If frontend sends empty values, set safe defaults
        if (request.getDestination() == null || request.getDestination().trim().isEmpty()) {
            request.setDestination("Dhaka");
        }

        HotelSearchResponse response = hotelsApiService.searchHotels(request);
        return ResponseEntity.ok(response);
    }

    // Quick test endpoint (open in browser)
    @GetMapping("/test")
    public ResponseEntity<HotelSearchResponse> testHotels() {
        HotelSearchRequest req = new HotelSearchRequest();
        req.setDestination("Dhaka");
        return ResponseEntity.ok(hotelsApiService.searchHotels(req));
    }
}
