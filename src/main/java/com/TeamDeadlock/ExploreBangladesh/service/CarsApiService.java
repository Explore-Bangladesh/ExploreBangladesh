package com.TeamDeadlock.ExploreBangladesh.service;

import com.TeamDeadlock.ExploreBangladesh.dto.CarSearchResponse;
import com.TeamDeadlock.ExploreBangladesh.dto.CarSearchResponse.Car;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CarsApiService {

    // City-based car inventory for Bangladesh
    private static final Map<String, List<Car>> CITY_CARS = new HashMap<>();

    private static Car makeCar(String id, String name, String type, int seats, double price, String imageUrl) {
        Car c = new Car();
        c.setId(id);
        c.setName(name);
        c.setType(type);
        c.setSeats(seats);
        c.setPrice(price);
        c.setImageUrl(imageUrl);
        return c;
    }

    static {
        // Dhaka
        CITY_CARS.put("dhaka", Arrays.asList(
                makeCar("dhk-1", "Toyota Corolla", "Sedan", 4, 3500, "https://images.unsplash.com/photo-1623869675781-80aa31012a5a?w=600"),
                makeCar("dhk-2", "Toyota Noah", "Micro", 7, 4500, "https://images.unsplash.com/photo-1570125909232-eb263c188f7e?w=600"),
                makeCar("dhk-3", "Toyota Allion", "Sedan", 4, 3000, "https://images.unsplash.com/photo-1549399542-7e3f8b79c341?w=600"),
                makeCar("dhk-4", "Mitsubishi Pajero", "SUV", 7, 7000, "https://images.unsplash.com/photo-1519641471654-76ce0107ad1b?w=600"),
                makeCar("dhk-5", "Toyota Hiace", "Van", 12, 6000, "https://images.unsplash.com/photo-1609520505218-7421df70e621?w=600"),
                makeCar("dhk-6", "Honda City", "Sedan", 4, 3200, "https://images.unsplash.com/photo-1583121274602-3e2820c69888?w=600"),
                makeCar("dhk-7", "Toyota Land Cruiser", "SUV", 7, 12000, "https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?w=600"),
                makeCar("dhk-8", "Nissan Sunny", "Sedan", 4, 2800, "https://images.unsplash.com/photo-1502877338535-766e1452684a?w=600")
        ));

        // Chittagong
        CITY_CARS.put("chittagong", Arrays.asList(
                makeCar("ctg-1", "Toyota Axio", "Sedan", 4, 3000, "https://images.unsplash.com/photo-1549399542-7e3f8b79c341?w=600"),
                makeCar("ctg-2", "Toyota Hiace", "Van", 12, 5500, "https://images.unsplash.com/photo-1609520505218-7421df70e621?w=600"),
                makeCar("ctg-3", "Mitsubishi Pajero Sport", "SUV", 7, 7500, "https://images.unsplash.com/photo-1519641471654-76ce0107ad1b?w=600"),
                makeCar("ctg-4", "Toyota Probox", "Micro", 5, 2500, "https://images.unsplash.com/photo-1570125909232-eb263c188f7e?w=600"),
                makeCar("ctg-5", "Honda Civic", "Sedan", 4, 4000, "https://images.unsplash.com/photo-1583121274602-3e2820c69888?w=600")
        ));

        // Cox's Bazar
        CITY_CARS.put("cox's bazar", Arrays.asList(
                makeCar("cxb-1", "Toyota Land Cruiser Prado", "SUV", 7, 8000, "https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?w=600"),
                makeCar("cxb-2", "Mitsubishi Pajero", "SUV", 7, 7000, "https://images.unsplash.com/photo-1519641471654-76ce0107ad1b?w=600"),
                makeCar("cxb-3", "Toyota Hiace", "Van", 12, 5000, "https://images.unsplash.com/photo-1609520505218-7421df70e621?w=600"),
                makeCar("cxb-4", "Toyota Noah", "Micro", 7, 4000, "https://images.unsplash.com/photo-1570125909232-eb263c188f7e?w=600")
        ));
        CITY_CARS.put("coxsbazar", CITY_CARS.get("cox's bazar"));

        // Sylhet
        CITY_CARS.put("sylhet", Arrays.asList(
                makeCar("syl-1", "Toyota Corolla", "Sedan", 4, 3200, "https://images.unsplash.com/photo-1623869675781-80aa31012a5a?w=600"),
                makeCar("syl-2", "Mitsubishi Pajero", "SUV", 7, 6500, "https://images.unsplash.com/photo-1519641471654-76ce0107ad1b?w=600"),
                makeCar("syl-3", "Toyota Noah", "Micro", 7, 4200, "https://images.unsplash.com/photo-1570125909232-eb263c188f7e?w=600"),
                makeCar("syl-4", "Toyota Hiace", "Van", 12, 5500, "https://images.unsplash.com/photo-1609520505218-7421df70e621?w=600")
        ));

        // Rajshahi
        CITY_CARS.put("rajshahi", Arrays.asList(
                makeCar("raj-1", "Toyota Allion", "Sedan", 4, 2800, "https://images.unsplash.com/photo-1549399542-7e3f8b79c341?w=600"),
                makeCar("raj-2", "Toyota Noah", "Micro", 7, 3800, "https://images.unsplash.com/photo-1570125909232-eb263c188f7e?w=600"),
                makeCar("raj-3", "Nissan Sunny", "Sedan", 4, 2500, "https://images.unsplash.com/photo-1502877338535-766e1452684a?w=600")
        ));

        // Khulna
        CITY_CARS.put("khulna", Arrays.asList(
                makeCar("khl-1", "Toyota Corolla", "Sedan", 4, 3000, "https://images.unsplash.com/photo-1623869675781-80aa31012a5a?w=600"),
                makeCar("khl-2", "Toyota Hiace", "Van", 12, 5000, "https://images.unsplash.com/photo-1609520505218-7421df70e621?w=600"),
                makeCar("khl-3", "Mitsubishi Pajero", "SUV", 7, 6000, "https://images.unsplash.com/photo-1519641471654-76ce0107ad1b?w=600")
        ));

        // Rangpur
        CITY_CARS.put("rangpur", Arrays.asList(
                makeCar("rng-1", "Toyota Axio", "Sedan", 4, 2800, "https://images.unsplash.com/photo-1549399542-7e3f8b79c341?w=600"),
                makeCar("rng-2", "Toyota Noah", "Micro", 7, 3500, "https://images.unsplash.com/photo-1570125909232-eb263c188f7e?w=600")
        ));

        // Barishal
        CITY_CARS.put("barishal", Arrays.asList(
                makeCar("bar-1", "Toyota Corolla", "Sedan", 4, 2800, "https://images.unsplash.com/photo-1623869675781-80aa31012a5a?w=600"),
                makeCar("bar-2", "Toyota Noah", "Micro", 7, 3500, "https://images.unsplash.com/photo-1570125909232-eb263c188f7e?w=600")
        ));

        // Bandarban
        CITY_CARS.put("bandarban", Arrays.asList(
                makeCar("bdn-1", "Mitsubishi Pajero", "SUV", 7, 7500, "https://images.unsplash.com/photo-1519641471654-76ce0107ad1b?w=600"),
                makeCar("bdn-2", "Toyota Land Cruiser", "SUV", 7, 10000, "https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?w=600"),
                makeCar("bdn-3", "Toyota Hiace", "Van", 12, 6000, "https://images.unsplash.com/photo-1609520505218-7421df70e621?w=600")
        ));

        // Rangamati
        CITY_CARS.put("rangamati", Arrays.asList(
                makeCar("rgm-1", "Mitsubishi Pajero", "SUV", 7, 7000, "https://images.unsplash.com/photo-1519641471654-76ce0107ad1b?w=600"),
                makeCar("rgm-2", "Toyota Land Cruiser Prado", "SUV", 7, 9000, "https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?w=600")
        ));

        // Mymensingh
        CITY_CARS.put("mymensingh", Arrays.asList(
                makeCar("mym-1", "Toyota Allion", "Sedan", 4, 2700, "https://images.unsplash.com/photo-1549399542-7e3f8b79c341?w=600"),
                makeCar("mym-2", "Toyota Noah", "Micro", 7, 3500, "https://images.unsplash.com/photo-1570125909232-eb263c188f7e?w=600")
        ));
    }

    public CarSearchResponse searchCars(String location) {
        if (location == null || location.trim().isEmpty()) {
            location = "Dhaka";
        }

        String key = location.toLowerCase().trim();
        List<Car> cars = CITY_CARS.get(key);

        CarSearchResponse response = new CarSearchResponse();
        response.setSearchedLocation(location);

        if (cars != null) {
            response.setCars(new ArrayList<>(cars));
            response.setTotalResults(cars.size());
        } else {
            response.setCars(Collections.emptyList());
            response.setTotalResults(0);
        }

        return response;
    }

    public List<String> getSupportedCities() {
        return CITY_CARS.keySet().stream().sorted().collect(Collectors.toList());
    }
}
