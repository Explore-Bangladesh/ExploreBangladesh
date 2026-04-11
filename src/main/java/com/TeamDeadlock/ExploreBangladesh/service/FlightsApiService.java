package com.TeamDeadlock.ExploreBangladesh.service;

import com.TeamDeadlock.ExploreBangladesh.dto.FlightSearchRequest;
import com.TeamDeadlock.ExploreBangladesh.dto.FlightSearchResponse;
import com.TeamDeadlock.ExploreBangladesh.dto.FlightSearchResponse.FlightOffer;
import com.TeamDeadlock.ExploreBangladesh.dto.FlightSearchResponse.Itinerary;
import com.TeamDeadlock.ExploreBangladesh.dto.FlightSearchResponse.Segment;
import com.TeamDeadlock.ExploreBangladesh.entity.Airline;
import com.TeamDeadlock.ExploreBangladesh.entity.Airport;
import com.TeamDeadlock.ExploreBangladesh.repository.AirlineRepository;
import com.TeamDeadlock.ExploreBangladesh.repository.AirportRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FlightsApiService {

    @Value("${flightapi.api.key}")
    private String flightApiKey;

    @Value("${flightapi.base-url}")
    private String baseUrl;

    @Value("${flightapi.currency:BDT}")
    private String currency;

    @Value("${flightapi.region:BD}")
    private String region;

    private final RestTemplate restTemplate = new RestTemplate();
    private final AirportRepository airportRepository;
    private final AirlineRepository airlineRepository;

    public FlightsApiService(AirportRepository airportRepository,
                             AirlineRepository airlineRepository) {
        this.airportRepository = airportRepository;
        this.airlineRepository = airlineRepository;
    }

    private Map<String, String> getAirportNames() {
        return airportRepository.findAll().stream()
                .collect(Collectors.toMap(Airport::getIataCode, Airport::getName));
    }

    private Map<String, String> getAirlineNames() {
        return airlineRepository.findAll().stream()
                .collect(Collectors.toMap(Airline::getCode, Airline::getName));
    }

    public FlightSearchResponse searchFlights(FlightSearchRequest request) {
        try {
            String cabinClass = mapCabinClass(request.getTravelClass());
            String url;

            if (request.getReturnDate() != null && !request.getReturnDate().isBlank()) {
                url = String.format(
                        "%s/roundtrip/%s/%s/%s/%s/%s/%d/%d/%d/%s/%s",
                        baseUrl,
                        flightApiKey,
                        request.getOrigin(),
                        request.getDestination(),
                        request.getDepartureDate(),
                        request.getReturnDate(),
                        request.getAdults() != null ? request.getAdults() : 1,
                        0,
                        0,
                        cabinClass,
                        currency
                );
            } else {
                url = String.format(
                        "%s/onewaytrip/%s/%s/%s/%s/%d/%d/%d/%s/%s",
                        baseUrl,
                        flightApiKey,
                        request.getOrigin(),
                        request.getDestination(),
                        request.getDepartureDate(),
                        request.getAdults() != null ? request.getAdults() : 1,
                        0,
                        0,
                        cabinClass,
                        currency
                );
            }

            System.out.println("FlightAPI URL: " + url);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            return parseFlightApiResponse(response, request);

        } catch (Exception e) {
            System.err.println("Flight search error: " + e.getMessage());
            e.printStackTrace();

            FlightSearchResponse errorResponse = new FlightSearchResponse();
            errorResponse.setFlights(new ArrayList<>());
            errorResponse.setTotalResults(0);
            errorResponse.setCurrency(currency);
            return errorResponse;
        }
    }

    @SuppressWarnings("unchecked")
    private FlightSearchResponse parseFlightApiResponse(Map<String, Object> response, FlightSearchRequest request) {
        FlightSearchResponse result = new FlightSearchResponse();
        List<FlightOffer> flights = new ArrayList<>();

        Map<String, String> airportNames = getAirportNames();
        Map<String, String> airlineNames = getAirlineNames();

        List<Map<String, Object>> itinerariesData = (List<Map<String, Object>>) response.get("itineraries");
        List<Map<String, Object>> legsData = (List<Map<String, Object>>) response.get("legs");
        List<Map<String, Object>> segmentsData = (List<Map<String, Object>>) response.get("segments");
        List<Map<String, Object>> placesData = (List<Map<String, Object>>) response.get("places");
        List<Map<String, Object>> carriersData = (List<Map<String, Object>>) response.get("carriers");

        Map<String, Map<String, Object>> legsById = new HashMap<>();
        if (legsData != null) {
            for (Map<String, Object> leg : legsData) {
                legsById.put(String.valueOf(leg.get("id")), leg);
            }
        }

        Map<String, Map<String, Object>> segmentsById = new HashMap<>();
        if (segmentsData != null) {
            for (Map<String, Object> segment : segmentsData) {
                segmentsById.put(String.valueOf(segment.get("id")), segment);
            }
        }

        Map<String, Map<String, Object>> placesById = new HashMap<>();
        if (placesData != null) {
            for (Map<String, Object> place : placesData) {
                placesById.put(String.valueOf(place.get("id")), place);
            }
        }

        Map<String, Map<String, Object>> carriersById = new HashMap<>();
        if (carriersData != null) {
            for (Map<String, Object> carrier : carriersData) {
                carriersById.put(String.valueOf(carrier.get("id")), carrier);
            }
        }

        if (itinerariesData != null) {
            for (Map<String, Object> itineraryData : itinerariesData) {
                FlightOffer offer = new FlightOffer();
                offer.setId(String.valueOf(itineraryData.get("id")));

                List<Map<String, Object>> pricingOptions =
                        (List<Map<String, Object>>) itineraryData.get("pricing_options");

                if (pricingOptions != null && !pricingOptions.isEmpty()) {
                    Map<String, Object> firstPricing = pricingOptions.get(0);
                    Map<String, Object> priceObj = (Map<String, Object>) firstPricing.get("price");
                    if (priceObj != null && priceObj.get("amount") != null) {
                        offer.setPrice(String.valueOf(priceObj.get("amount")));
                    } else {
                        offer.setPrice("0");
                    }
                } else {
                    offer.setPrice("0");
                }

                offer.setCurrency(currency);

                List<String> legIds = (List<String>) itineraryData.get("leg_ids");
                List<Itinerary> mappedItineraries = new ArrayList<>();
                int totalStops = 0;
                String mainCarrierCode = "";
                String mainCarrierName = "";

                if (legIds != null) {
                    for (String legId : legIds) {
                        Map<String, Object> leg = legsById.get(String.valueOf(legId));
                        if (leg == null) continue;

                        Itinerary mappedItinerary = new Itinerary();
                        mappedItinerary.setDuration(formatDurationMinutes((Number) leg.get("duration")));

                        List<String> segmentIds = (List<String>) leg.get("segment_ids");
                        List<Segment> mappedSegments = new ArrayList<>();

                        if (segmentIds != null) {
                            totalStops += Math.max(segmentIds.size() - 1, 0);

                            for (String segmentId : segmentIds) {
                                Map<String, Object> segmentData = segmentsById.get(String.valueOf(segmentId));
                                if (segmentData == null) continue;

                                Segment segment = new Segment();

                                String originPlaceId = String.valueOf(segmentData.get("origin_place_id"));
                                String destinationPlaceId = String.valueOf(segmentData.get("destination_place_id"));

                                Map<String, Object> originPlace = placesById.get(originPlaceId);
                                Map<String, Object> destinationPlace = placesById.get(destinationPlaceId);

                                String originCode = getPlaceCode(originPlace);
                                String destinationCode = getPlaceCode(destinationPlace);

                                segment.setDepartureAirport(originCode);
                                segment.setArrivalAirport(destinationCode);

                                segment.setDepartureCity(
                                        airportNames.getOrDefault(originCode, getPlaceName(originPlace, originCode))
                                );
                                segment.setArrivalCity(
                                        airportNames.getOrDefault(destinationCode, getPlaceName(destinationPlace, destinationCode))
                                );

                                segment.setDepartureTime(formatDateTime(String.valueOf(segmentData.get("departure"))));
                                segment.setArrivalTime(formatDateTime(String.valueOf(segmentData.get("arrival"))));

                                String flightNumber = String.valueOf(segmentData.get("marketing_flight_number"));
                                Object carrierId = segmentData.get("marketing_carrier_id");

                                String carrierCode = resolveCarrierCode(carrierId, carriersById);
                                String carrierName = resolveCarrierName(carrierId, carriersById, airlineNames, carrierCode);

                                segment.setCarrierCode(carrierCode);
                                segment.setCarrierName(carrierName);
                                segment.setFlightNumber(carrierCode + flightNumber);
                                segment.setDuration(formatDurationMinutes((Number) segmentData.get("duration")));
                                segment.setAircraft("");
                                segment.setCabinClass(mapCabinClass(request.getTravelClass()));

                                if (mainCarrierCode.isEmpty()) {
                                    mainCarrierCode = carrierCode;
                                    mainCarrierName = carrierName;
                                }

                                mappedSegments.add(segment);
                            }
                        }

                        mappedItinerary.setSegments(mappedSegments);
                        mappedItineraries.add(mappedItinerary);
                    }
                }

                offer.setItineraries(mappedItineraries);
                offer.setNumberOfStops(totalStops);
                offer.setAirline(mainCarrierCode);
                offer.setAirlineName(mainCarrierName.isBlank() ? mainCarrierCode : mainCarrierName);
                offer.setInstantTicketingRequired(false);

                flights.add(offer);
            }
        }

        if (Boolean.TRUE.equals(request.getNonStop())) {
            flights = flights.stream()
                    .filter(f -> f.getNumberOfStops() == 0)
                    .collect(Collectors.toList());
        }

        result.setFlights(flights);
        result.setTotalResults(flights.size());
        result.setCurrency(currency);
        return result;
    }

    private String mapCabinClass(String travelClass) {
        if (travelClass == null || travelClass.isBlank()) return "Economy";

        return switch (travelClass.toUpperCase()) {
            case "BUSINESS" -> "Business";
            case "FIRST" -> "First";
            case "PREMIUM_ECONOMY" -> "Premium_Economy";
            default -> "Economy";
        };
    }

    private String getPlaceCode(Map<String, Object> place) {
        if (place == null) return "";
        Object code = place.get("iata");
        if (code == null) code = place.get("iata_code");
        if (code == null) code = place.get("display_code");
        if (code == null) code = place.get("sky_code");
        return code != null ? String.valueOf(code) : "";
    }

    private String getPlaceName(Map<String, Object> place, String fallback) {
        if (place == null) return fallback;
        Object name = place.get("name");
        return name != null ? String.valueOf(name) : fallback;
    }

    private String resolveCarrierCode(Object carrierId,
                                      Map<String, Map<String, Object>> carriersById) {
        if (carrierId == null) return "";
        Map<String, Object> carrier = carriersById.get(String.valueOf(carrierId));
        if (carrier == null) return String.valueOf(carrierId);

        Object code = carrier.get("iata");
        if (code == null) code = carrier.get("iata_code");
        if (code == null) code = carrier.get("display_code");
        if (code == null) code = carrier.get("code");
        if (code == null) code = carrier.get("name");

        return code != null ? String.valueOf(code) : String.valueOf(carrierId);
    }

    private String resolveCarrierName(Object carrierId,
                                      Map<String, Map<String, Object>> carriersById,
                                      Map<String, String> airlineNames,
                                      String fallbackCode) {
        if (carrierId == null) return fallbackCode;
        Map<String, Object> carrier = carriersById.get(String.valueOf(carrierId));
        if (carrier == null) return airlineNames.getOrDefault(fallbackCode, fallbackCode);

        Object name = carrier.get("name");
        if (name != null) return String.valueOf(name);

        return airlineNames.getOrDefault(fallbackCode, fallbackCode);
    }

    private String formatDurationMinutes(Number minutes) {
        if (minutes == null) return "";
        int total = minutes.intValue();
        int h = total / 60;
        int m = total % 60;

        if (h > 0 && m > 0) return h + "h " + m + "m";
        if (h > 0) return h + "h";
        return m + "m";
    }

    private String formatDateTime(String isoDateTime) {
        if (isoDateTime == null || isoDateTime.isBlank()) return "";
        if (isoDateTime.contains("T")) {
            String[] parts = isoDateTime.split("T");
            if (parts.length > 1 && parts[1].length() >= 5) {
                return parts[1].substring(0, 5);
            }
        }
        return isoDateTime;
    }

    public Map<String, String> getSupportedAirports() {
        return getAirportNames();
    }
}