package com.TeamDeadlock.ExploreBangladesh.repository;

import com.TeamDeadlock.ExploreBangladesh.entity.CarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<CarEntity, String> {

    List<CarEntity> findByCityIgnoreCase(String city);

    @Query("SELECT DISTINCT c.city FROM CarEntity c ORDER BY c.city")
    List<String> findDistinctCities();
}
