package com.TeamDeadlock.ExploreBangladesh.planner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherDTO {
    private String date;
    private String description;        // "Sunny", "Rainy", "Cloudy", etc.
    private double temperatureMax;
    private double temperatureMin;
    private int weatherCode;           // Raw code from Open-Meteo
    private double precipitation;      // Optional: rainfall amount
    private double windSpeed;          // Optional: max wind speed
}
