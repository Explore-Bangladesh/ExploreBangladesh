package com.TeamDeadlock.ExploreBangladesh.repository;

import com.TeamDeadlock.ExploreBangladesh.entity.CityCoordinate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityCoordinateRepository extends JpaRepository<CityCoordinate, Long> {

    Optional<CityCoordinate> findByCityKey(String cityKey);

    boolean existsByCityKey(String cityKey);
}
