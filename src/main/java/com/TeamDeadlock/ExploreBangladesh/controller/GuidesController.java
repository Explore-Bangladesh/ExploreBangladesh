package com.TeamDeadlock.ExploreBangladesh.controller;

import com.TeamDeadlock.ExploreBangladesh.dto.GuideSearchResponse;
import com.TeamDeadlock.ExploreBangladesh.service.GuidesApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guides")
@CrossOrigin(origins = "*")
public class GuidesController {

    private final GuidesApiService guidesApiService;

    public GuidesController(GuidesApiService guidesApiService) {
        this.guidesApiService = guidesApiService;
    }

    @GetMapping
    public ResponseEntity<GuideSearchResponse> searchGuides(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String language) {

        GuideSearchResponse response = guidesApiService.searchGuides(city, language);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cities")
    public ResponseEntity<List<String>> getSupportedCities() {
        return ResponseEntity.ok(guidesApiService.getSupportedCities());
    }

    @GetMapping("/test")
    public ResponseEntity<GuideSearchResponse> testGuides() {
        return ResponseEntity.ok(guidesApiService.searchGuides("Dhaka", "English"));
    }
}
