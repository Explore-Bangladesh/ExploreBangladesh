package com.TeamDeadlock.ExploreBangladesh.repository;

import com.TeamDeadlock.ExploreBangladesh.entity.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AirportRepository extends JpaRepository<Airport, String> {
}
