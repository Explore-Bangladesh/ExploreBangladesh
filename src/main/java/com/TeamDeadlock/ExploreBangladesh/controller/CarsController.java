package com.TeamDeadlock.ExploreBangladesh.controller;

import com.TeamDeadlock.ExploreBangladesh.dto.CarSearchResponse;
import com.TeamDeadlock.ExploreBangladesh.service.CarsApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
@CrossOrigin(origins = "*")
public class CarsController {

    private final CarsApiService carsApiService;

    public CarsController(CarsApiService carsApiService) {
        this.carsApiService = carsApiService;
    }

    @GetMapping
    public ResponseEntity<CarSearchResponse> searchCars(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String pickupDate,
            @RequestParam(required = false) String dropoffDate) {

        if (location == null || location.trim().isEmpty()) {
            location = "Dhaka";
        }

        CarSearchResponse response = carsApiService.searchCars(location);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cities")
    public ResponseEntity<List<String>> getSupportedCities() {
        return ResponseEntity.ok(carsApiService.getSupportedCities());
    }

    @GetMapping("/test")
    public ResponseEntity<CarSearchResponse> testCars() {
        return ResponseEntity.ok(carsApiService.searchCars("Dhaka"));
    }
}
