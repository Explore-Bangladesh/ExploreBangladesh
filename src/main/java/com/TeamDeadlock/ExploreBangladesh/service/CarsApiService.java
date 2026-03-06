package com.TeamDeadlock.ExploreBangladesh.service;

import com.TeamDeadlock.ExploreBangladesh.dto.CarSearchResponse;
import com.TeamDeadlock.ExploreBangladesh.dto.CarSearchResponse.Car;
import com.TeamDeadlock.ExploreBangladesh.entity.CarEntity;
import com.TeamDeadlock.ExploreBangladesh.repository.CarRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarsApiService {

    private final CarRepository carRepository;

    public CarsApiService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    /** Map a DB entity to the response DTO. */
    private Car toDto(CarEntity e) {
        Car c = new Car();
        c.setId(e.getCarId());
        c.setName(e.getName());
        c.setType(e.getType());
        c.setSeats(e.getSeats());
        c.setPrice(e.getPrice());
        c.setImageUrl(e.getImageUrl());
        return c;
    }

    public CarSearchResponse searchCars(String location) {
        if (location == null || location.trim().isEmpty()) {
            location = "Dhaka";
        }

        List<CarEntity> entities = carRepository.findByCityIgnoreCase(location.trim());
        List<Car> cars = entities.stream().map(this::toDto).collect(Collectors.toList());

        CarSearchResponse response = new CarSearchResponse();
        response.setSearchedLocation(location);
        response.setCars(cars);
        response.setTotalResults(cars.size());
        return response;
    }

    public List<String> getSupportedCities() {
        return carRepository.findDistinctCities();
    }
}
