package com.TeamDeadlock.ExploreBangladesh.service;

import com.TeamDeadlock.ExploreBangladesh.dto.GuideSearchResponse;
import com.TeamDeadlock.ExploreBangladesh.dto.GuideSearchResponse.Guide;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GuidesApiService {

    // Hardcoded guide database for Bangladesh
    private static final List<Guide> ALL_GUIDES = new ArrayList<>();

    private static Guide makeGuide(String id, String name, String city, int experienceYears,
                                   List<String> languages, double rating, String imageUrl) {
        Guide g = new Guide();
        g.setId(id);
        g.setName(name);
        g.setCity(city);
        g.setExperienceYears(experienceYears);
        g.setLanguages(languages);
        g.setRating(rating);
        g.setImageUrl(imageUrl);
        return g;
    }

    static {
        // Dhaka guides
        ALL_GUIDES.add(makeGuide("g-1", "Rahim Uddin", "Dhaka", 8,
                Arrays.asList("Bangla", "English"), 4.8,
                "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=120"));
        ALL_GUIDES.add(makeGuide("g-2", "Kamal Hossain", "Dhaka", 12,
                Arrays.asList("Bangla", "English", "Hindi"), 4.9,
                "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=120"));
        ALL_GUIDES.add(makeGuide("g-3", "Fatema Begum", "Dhaka", 5,
                Arrays.asList("Bangla", "English"), 4.5,
                "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=120"));
        ALL_GUIDES.add(makeGuide("g-4", "Arif Ahmed", "Dhaka", 10,
                Arrays.asList("Bangla", "English", "Hindi"), 4.7,
                "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=120"));

        // Chittagong guides
        ALL_GUIDES.add(makeGuide("g-5", "Nusrat Jahan", "Chittagong", 6,
                Arrays.asList("Bangla", "English"), 4.6,
                "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=120"));
        ALL_GUIDES.add(makeGuide("g-6", "Mizanur Rahman", "Chittagong", 15,
                Arrays.asList("Bangla", "English"), 4.9,
                "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=120"));

        // Cox's Bazar guides
        ALL_GUIDES.add(makeGuide("g-7", "Shahid Alam", "Cox's Bazar", 10,
                Arrays.asList("Bangla", "English"), 4.8,
                "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=120"));
        ALL_GUIDES.add(makeGuide("g-8", "Tasnim Akter", "Cox's Bazar", 4,
                Arrays.asList("Bangla", "English"), 4.3,
                "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=120"));
        ALL_GUIDES.add(makeGuide("g-9", "Jamal Uddin", "Cox's Bazar", 7,
                Arrays.asList("Bangla", "English", "Hindi"), 4.5,
                "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=120"));

        // Sylhet guides
        ALL_GUIDES.add(makeGuide("g-10", "Sumon Mia", "Sylhet", 9,
                Arrays.asList("Bangla", "English"), 4.7,
                "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=120"));
        ALL_GUIDES.add(makeGuide("g-11", "Rupa Das", "Sylhet", 6,
                Arrays.asList("Bangla", "English", "Hindi"), 4.4,
                "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=120"));

        // Rajshahi guides
        ALL_GUIDES.add(makeGuide("g-12", "Habibur Rahman", "Rajshahi", 11,
                Arrays.asList("Bangla", "English"), 4.6,
                "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=120"));

        // Khulna guides
        ALL_GUIDES.add(makeGuide("g-13", "Anisur Rahman", "Khulna", 8,
                Arrays.asList("Bangla", "English"), 4.5,
                "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=120"));
        ALL_GUIDES.add(makeGuide("g-14", "Shamima Nasrin", "Khulna", 5,
                Arrays.asList("Bangla"), 4.2,
                "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=120"));

        // Rangpur guides
        ALL_GUIDES.add(makeGuide("g-15", "Belal Hossain", "Rangpur", 7,
                Arrays.asList("Bangla", "English"), 4.3,
                "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=120"));

        // Bandarban guides
        ALL_GUIDES.add(makeGuide("g-16", "Maung Thein", "Bandarban", 13,
                Arrays.asList("Bangla", "English"), 4.9,
                "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=120"));
        ALL_GUIDES.add(makeGuide("g-17", "Ching Marma", "Bandarban", 9,
                Arrays.asList("Bangla"), 4.6,
                "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=120"));

        // Rangamati guides
        ALL_GUIDES.add(makeGuide("g-18", "Dipak Chakma", "Rangamati", 10,
                Arrays.asList("Bangla", "English"), 4.7,
                "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=120"));

        // Barishal guides
        ALL_GUIDES.add(makeGuide("g-19", "Mostafa Kamal", "Barishal", 6,
                Arrays.asList("Bangla", "English"), 4.4,
                "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=120"));

        // Mymensingh guides
        ALL_GUIDES.add(makeGuide("g-20", "Sharmin Sultana", "Mymensingh", 4,
                Arrays.asList("Bangla", "English"), 4.1,
                "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=120"));
    }

    public GuideSearchResponse searchGuides(String city, String language) {
        List<Guide> filtered = ALL_GUIDES.stream()
                .filter(g -> {
                    boolean matches = true;
                    if (city != null && !city.trim().isEmpty()) {
                        matches = g.getCity().equalsIgnoreCase(city.trim());
                    }
                    if (language != null && !language.trim().isEmpty()) {
                        matches = matches && g.getLanguages().stream()
                                .anyMatch(l -> l.equalsIgnoreCase(language.trim()));
                    }
                    return matches;
                })
                .collect(Collectors.toList());

        GuideSearchResponse response = new GuideSearchResponse();
        response.setGuides(filtered);
        response.setTotalResults(filtered.size());
        response.setSearchedCity(city);
        return response;
    }

    public List<String> getSupportedCities() {
        return ALL_GUIDES.stream()
                .map(Guide::getCity)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}
