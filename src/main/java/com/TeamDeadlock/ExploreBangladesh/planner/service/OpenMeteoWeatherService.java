package com.TeamDeadlock.ExploreBangladesh.planner.service;

import com.TeamDeadlock.ExploreBangladesh.planner.dto.WeatherDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenMeteoWeatherService {
    
    private final RestTemplate restTemplate;
    private static final String OPEN_METEO_API = "https://api.open-meteo.com/v1/forecast";

    /**
     * Fetch weather forecast for a location
     * 
     * @param latitude Location latitude
     * @param longitude Location longitude
     * @param days Number of days to forecast (max 16)
     * @return List of WeatherDTO objects
     */
    public List<WeatherDTO> getWeatherForecast(double latitude, double longitude, int days) {
        List<WeatherDTO> weatherList = new ArrayList<>();
        
        try {
            // Build API URL
            int forecastDays = Math.min(days, 16);  // Max 16 days allowed
            String url = String.format(
                "%s?latitude=%.4f&longitude=%.4f&daily=weather_code,temperature_2m_max,temperature_2m_min,precipitation_sum,wind_speed_10m_max&timezone=auto&forecast_days=%d",
                OPEN_METEO_API, latitude, longitude, forecastDays
            );
            
            log.info("🌦️  Fetching weather from Open-Meteo: lat={}, lon={}", latitude, longitude);
            
            // Make API call
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response == null || !response.containsKey("daily")) {
                log.warn("⚠️  Invalid weather API response");
                return getDefaultWeatherList(days);
            }
            
            // Parse the response
            Map<String, Object> daily = (Map<String, Object>) response.get("daily");
            List<String> dates = (List<String>) daily.get("time");
            List<Integer> weatherCodes = (List<Integer>) daily.get("weather_code");
            List<Double> tempMax = (List<Double>) daily.get("temperature_2m_max");
            List<Double> tempMin = (List<Double>) daily.get("temperature_2m_min");
            List<Double> precipitation = (List<Double>) daily.get("precipitation_sum");
            List<Double> windSpeed = (List<Double>) daily.get("wind_speed_10m_max");
            
            // Build weather list
            for (int i = 0; i < dates.size(); i++) {
                int code = weatherCodes.get(i);
                WeatherDTO weather = new WeatherDTO();
                weather.setDate(dates.get(i));
                weather.setWeatherCode(code);
                weather.setDescription(getWeatherDescription(code));
                weather.setTemperatureMax(tempMax.get(i));
                weather.setTemperatureMin(tempMin.get(i));
                weather.setPrecipitation(precipitation != null ? precipitation.get(i) : 0.0);
                weather.setWindSpeed(windSpeed != null ? windSpeed.get(i) : 0.0);
                
                weatherList.add(weather);
                
                log.debug("📅 Day {}: {} ({}°C - {}°C, Wind: {} km/h)", 
                    i + 1, weather.getDescription(), 
                    (int) weather.getTemperatureMax(), 
                    (int) weather.getTemperatureMin(),
                    (int) weather.getWindSpeed());
            }
            
        } catch (Exception e) {
            log.error("❌ Error fetching weather data: {}", e.getMessage());
            return getDefaultWeatherList(days);
        }
        
        return weatherList;
    }

    /**
     * Convert WMO weather codes to human-readable descriptions (without emoji)
     */
    private String getWeatherDescription(int code) {
        return switch (code) {
            case 0 -> "Clear sky";
            case 1, 2 -> "Mostly clear";
            case 3 -> "Overcast";
            case 45, 48 -> "Foggy";
            case 51, 53, 55 -> "Drizzle";
            case 61, 63, 65 -> "Rain";
            case 71, 73, 75 -> "Snow";
            case 77 -> "Snow grains";
            case 80, 81, 82 -> "Rain showers";
            case 85, 86 -> "Snow showers";
            case 95, 96, 99 -> "Thunderstorm";
            default -> "Unknown";
        };
    }

    /**
     * Return default weather list if API call fails
     */
    private List<WeatherDTO> getDefaultWeatherList(int days) {
        List<WeatherDTO> list = new ArrayList<>();
        for (int i = 0; i < days; i++) {
            WeatherDTO weather = new WeatherDTO();
            weather.setDescription("⚠️ Weather data unavailable");
            weather.setTemperatureMax(28);
            weather.setTemperatureMin(22);
            list.add(weather);
        }
        return list;
    }
}
