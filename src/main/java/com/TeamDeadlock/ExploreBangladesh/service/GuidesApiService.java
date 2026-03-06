package com.TeamDeadlock.ExploreBangladesh.service;

import com.TeamDeadlock.ExploreBangladesh.dto.GuideSearchResponse;
import com.TeamDeadlock.ExploreBangladesh.dto.GuideSearchResponse.Guide;
import com.TeamDeadlock.ExploreBangladesh.entity.GuideEntity;
import com.TeamDeadlock.ExploreBangladesh.repository.GuideRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GuidesApiService {

    private final GuideRepository guideRepository;

    public GuidesApiService(GuideRepository guideRepository) {
        this.guideRepository = guideRepository;
    }

    /** Map a DB entity to the response DTO. */
    private Guide toDto(GuideEntity e) {
        Guide g = new Guide();
        g.setId(e.getGuideId());
        g.setName(e.getName());
        g.setCity(e.getCity());
        g.setExperienceYears(e.getExperienceYears());
        g.setRating(e.getRating());
        g.setImageUrl(e.getImageUrl());
        g.setLanguages(e.getLanguages());
        return g;
    }

    public GuideSearchResponse searchGuides(String city, String language) {
        List<GuideEntity> entities;

        boolean hasCity     = city     != null && !city.trim().isEmpty();
        boolean hasLanguage = language != null && !language.trim().isEmpty();

        if (hasCity) {
            entities = guideRepository.findByCityIgnoreCase(city.trim());
        } else {
            entities = guideRepository.findAllWithLanguages();
        }

        // Apply language filter in memory (avoids complex JPQL on ElementCollection)
        if (hasLanguage) {
            final String lang = language.trim();
            entities = entities.stream()
                    .filter(g -> g.getLanguages().stream()
                            .anyMatch(l -> l.equalsIgnoreCase(lang)))
                    .collect(Collectors.toList());
        }

        List<Guide> guides = entities.stream().map(this::toDto).collect(Collectors.toList());

        GuideSearchResponse response = new GuideSearchResponse();
        response.setGuides(guides);
        response.setTotalResults(guides.size());
        response.setSearchedCity(city);
        return response;
    }

    public List<String> getSupportedCities() {
        return guideRepository.findDistinctCities();
    }
}
