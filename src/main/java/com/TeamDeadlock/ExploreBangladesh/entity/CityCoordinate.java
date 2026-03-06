package com.TeamDeadlock.ExploreBangladesh.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "city_coordinates")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CityCoordinate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Lowercase search key, e.g. "dhaka", "cox's bazar" */
    @Column(name = "city_key", unique = true, nullable = false)
    private String cityKey;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private Double latitude;

    public CityCoordinate(String cityKey, Double longitude, Double latitude) {
        this.cityKey = cityKey;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
