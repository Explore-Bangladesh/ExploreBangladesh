package com.TeamDeadlock.ExploreBangladesh.service;

import com.TeamDeadlock.ExploreBangladesh.dto.FlightSearchRequest;
import com.TeamDeadlock.ExploreBangladesh.dto.FlightSearchResponse;
import com.TeamDeadlock.ExploreBangladesh.dto.FlightSearchResponse.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class FlightsApiService {

    @Value("${amadeus.api.key}")
    private String amadeusApiKey;

    @Value("${amadeus.api.secret}")
    private String amadeusApiSecret;

    @Value("${amadeus.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    
    private String accessToken;
    private long tokenExpiry = 0;

    // Bangladesh airports with IATA codes
    private static final Map<String, String> AIRPORT_NAMES = new HashMap<>();
    
    static {
        // Domestic airports
        AIRPORT_NAMES.put("DAC", "Dhaka (Hazrat Shahjalal)");
        AIRPORT_NAMES.put("CGP", "Chittagong (Shah Amanat)");
        AIRPORT_NAMES.put("CXB", "Cox's Bazar");
        AIRPORT_NAMES.put("ZYL", "Sylhet (Osmani)");
        AIRPORT_NAMES.put("RJH", "Rajshahi (Shah Makhdum)");
        AIRPORT_NAMES.put("JSR", "Jessore");
        AIRPORT_NAMES.put("SPD", "Saidpur");
        AIRPORT_NAMES.put("BZL", "Barishal");
        
        // International destinations (commonly searched from Bangladesh)
        AIRPORT_NAMES.put("DXB", "Dubai");
        AIRPORT_NAMES.put("SIN", "Singapore");
        AIRPORT_NAMES.put("BKK", "Bangkok");
        AIRPORT_NAMES.put("KUL", "Kuala Lumpur");
        AIRPORT_NAMES.put("DEL", "Delhi");
        AIRPORT_NAMES.put("CCU", "Kolkata");
        AIRPORT_NAMES.put("DOH", "Doha");
        AIRPORT_NAMES.put("JED", "Jeddah");
        AIRPORT_NAMES.put("LHR", "London Heathrow");
        AIRPORT_NAMES.put("JFK", "New York JFK");
    }

    // Airline names
    private static final Map<String, String> AIRLINE_NAMES = new HashMap<>();
    
    static {
        AIRLINE_NAMES.put("BG", "Biman Bangladesh Airlines");
        AIRLINE_NAMES.put("BS", "US-Bangla Airlines");
        AIRLINE_NAMES.put("VQ", "Novoair");
        AIRLINE_NAMES.put("EK", "Emirates");
        AIRLINE_NAMES.put("QR", "Qatar Airways");
        AIRLINE_NAMES.put("SQ", "Singapore Airlines");
        AIRLINE_NAMES.put("TG", "Thai Airways");
        AIRLINE_NAMES.put("MH", "Malaysia Airlines");
        AIRLINE_NAMES.put("AI", "Air India");
        AIRLINE_NAMES.put("6E", "IndiGo");
        AIRLINE_NAMES.put("SV", "Saudia");
        AIRLINE_NAMES.put("BA", "British Airways");
        AIRLINE_NAMES.put("TK", "Turkish Airlines");
    }

    /**
     * Get OAuth2 access token from Amadeus
     */
    private String getAccessToken() {
        if (accessToken != null && System.currentTimeMillis() < tokenExpiry) {
            return accessToken;
        }

        String tokenUrl = baseUrl + "/v1/security/oauth2/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "grant_type=client_credentials&client_id=" + amadeusApiKey 
                    + "&client_secret=" + amadeusApiSecret;

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(tokenUrl, request, Map.class);
            
            accessToken = (String) response.get("access_token");
            int expiresIn = (Integer) response.get("expires_in");
            tokenExpiry = System.currentTimeMillis() + (expiresIn * 1000) - 60000; // Refresh 1 min early
            
            System.out.println("Got Amadeus access token, expires in " + expiresIn + " seconds");
            return accessToken;
            
        } catch (Exception e) {
            System.err.println("Failed to get Amadeus token: " + e.getMessage());
            throw new RuntimeException("Failed to authenticate with Amadeus API", e);
        }
    }

    /**
     * Search for flights using Amadeus Flight Offers API
     */
    public FlightSearchResponse searchFlights(FlightSearchRequest request) {
        String token = getAccessToken();

        // Build API URL
        StringBuilder urlBuilder = new StringBuilder(baseUrl);
        urlBuilder.append("/v2/shopping/flight-offers?");
        urlBuilder.append("originLocationCode=").append(request.getOrigin());
        urlBuilder.append("&destinationLocationCode=").append(request.getDestination());
        urlBuilder.append("&departureDate=").append(request.getDepartureDate());
        urlBuilder.append("&adults=").append(request.getAdults() != null ? request.getAdults() : 1);
        
        if (request.getReturnDate() != null && !request.getReturnDate().isEmpty()) {
            urlBuilder.append("&returnDate=").append(request.getReturnDate());
        }
        
        if (request.getTravelClass() != null && !request.getTravelClass().isEmpty()) {
            urlBuilder.append("&travelClass=").append(request.getTravelClass());
        }
        
        if (request.getNonStop() != null && request.getNonStop()) {
            urlBuilder.append("&nonStop=true");
        }
        
        urlBuilder.append("&max=50"); // Limit results
        urlBuilder.append("&currencyCode=BDT"); // Bangladesh Taka

        String url = urlBuilder.toString();
        System.out.println("Amadeus API URL: " + url);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> responseEntity = restTemplate.exchange(
                url, HttpMethod.GET, entity, Map.class
            );

            Map<String, Object> response = responseEntity.getBody();
            return parseFlightResponse(response);

        } catch (Exception e) {
            System.err.println("Flight search error: " + e.getMessage());
            e.printStackTrace();
            
            FlightSearchResponse errorResponse = new FlightSearchResponse();
            errorResponse.setFlights(new ArrayList<>());
            errorResponse.setTotalResults(0);
            return errorResponse;
        }
    }

    /**
     * Parse Amadeus API response into our DTO
     */
    @SuppressWarnings("unchecked")
    private FlightSearchResponse parseFlightResponse(Map<String, Object> response) {
        FlightSearchResponse result = new FlightSearchResponse();
        List<FlightOffer> flights = new ArrayList<>();

        List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");
        Map<String, Object> dictionaries = (Map<String, Object>) response.get("dictionaries");
        Map<String, String> carriers = dictionaries != null ? 
            (Map<String, String>) dictionaries.get("carriers") : new HashMap<>();

        if (data != null) {
            for (Map<String, Object> offer : data) {
                FlightOffer flightOffer = new FlightOffer();
                
                flightOffer.setId((String) offer.get("id"));
                
                // Parse price
                Map<String, Object> price = (Map<String, Object>) offer.get("price");
                flightOffer.setPrice((String) price.get("grandTotal"));
                flightOffer.setCurrency((String) price.get("currency"));
                
                // Parse itineraries
                List<Map<String, Object>> itinerariesData = 
                    (List<Map<String, Object>>) offer.get("itineraries");
                List<Itinerary> itineraries = new ArrayList<>();
                
                int totalStops = 0;
                String mainCarrier = "";
                
                for (Map<String, Object> itin : itinerariesData) {
                    Itinerary itinerary = new Itinerary();
                    itinerary.setDuration(formatDuration((String) itin.get("duration")));
                    
                    List<Map<String, Object>> segmentsData = 
                        (List<Map<String, Object>>) itin.get("segments");
                    List<Segment> segments = new ArrayList<>();
                    
                    totalStops += segmentsData.size() - 1;
                    
                    for (Map<String, Object> seg : segmentsData) {
                        Segment segment = new Segment();
                        
                        Map<String, Object> departure = (Map<String, Object>) seg.get("departure");
                        Map<String, Object> arrival = (Map<String, Object>) seg.get("arrival");
                        
                        String depCode = (String) departure.get("iataCode");
                        String arrCode = (String) arrival.get("iataCode");
                        
                        segment.setDepartureAirport(depCode);
                        segment.setDepartureCity(AIRPORT_NAMES.getOrDefault(depCode, depCode));
                        segment.setDepartureTime(formatDateTime((String) departure.get("at")));
                        
                        segment.setArrivalAirport(arrCode);
                        segment.setArrivalCity(AIRPORT_NAMES.getOrDefault(arrCode, arrCode));
                        segment.setArrivalTime(formatDateTime((String) arrival.get("at")));
                        
                        String carrierCode = (String) seg.get("carrierCode");
                        segment.setCarrierCode(carrierCode);
                        segment.setCarrierName(carriers.getOrDefault(carrierCode, 
                            AIRLINE_NAMES.getOrDefault(carrierCode, carrierCode)));
                        
                        if (mainCarrier.isEmpty()) {
                            mainCarrier = carrierCode;
                        }
                        
                        segment.setFlightNumber(carrierCode + seg.get("number"));
                        segment.setDuration(formatDuration((String) seg.get("duration")));
                        
                        Map<String, Object> aircraft = (Map<String, Object>) seg.get("aircraft");
                        if (aircraft != null) {
                            segment.setAircraft((String) aircraft.get("code"));
                        }
                        
                        segments.add(segment);
                    }
                    
                    itinerary.setSegments(segments);
                    itineraries.add(itinerary);
                }
                
                flightOffer.setItineraries(itineraries);
                flightOffer.setNumberOfStops(totalStops);
                flightOffer.setAirline(mainCarrier);
                flightOffer.setAirlineName(carriers.getOrDefault(mainCarrier, 
                    AIRLINE_NAMES.getOrDefault(mainCarrier, mainCarrier)));
                
                flights.add(flightOffer);
            }
        }

        result.setFlights(flights);
        result.setTotalResults(flights.size());
        result.setCurrency("BDT");

        return result;
    }

    /**
     * Format ISO duration (PT2H30M) to readable format (2h 30m)
     */
    private String formatDuration(String isoDuration) {
        if (isoDuration == null || !isoDuration.startsWith("PT")) {
            return isoDuration;
        }
        
        String duration = isoDuration.substring(2);
        StringBuilder result = new StringBuilder();
        
        int hIndex = duration.indexOf('H');
        if (hIndex > 0) {
            result.append(duration.substring(0, hIndex)).append("h ");
            duration = duration.substring(hIndex + 1);
        }
        
        int mIndex = duration.indexOf('M');
        if (mIndex > 0) {
            result.append(duration.substring(0, mIndex)).append("m");
        }
        
        return result.toString().trim();
    }

    /**
     * Format datetime to readable format
     */
    private String formatDateTime(String isoDateTime) {
        if (isoDateTime == null) return "";
        // Input: 2024-01-28T10:30:00 -> Output: 10:30
        if (isoDateTime.contains("T")) {
            String time = isoDateTime.split("T")[1];
            return time.substring(0, 5);
        }
        return isoDateTime;
    }

    /**
     * Get list of supported airports
     */
    public Map<String, String> getSupportedAirports() {
        return new HashMap<>(AIRPORT_NAMES);
    }
}
