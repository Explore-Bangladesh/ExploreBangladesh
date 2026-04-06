package com.TeamDeadlock.ExploreBangladesh.planner.repository;

import com.TeamDeadlock.ExploreBangladesh.planner.entity.DestinationInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface DestinationInfoRepository extends JpaRepository<DestinationInfo, Long> {
    Optional<DestinationInfo> findByCityNameIgnoreCase(String cityName);
    List<DestinationInfo> findAllByOrderByCityName();
}
