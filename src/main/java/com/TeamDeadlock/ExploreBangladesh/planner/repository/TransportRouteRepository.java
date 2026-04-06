package com.TeamDeadlock.ExploreBangladesh.planner.repository;

import com.TeamDeadlock.ExploreBangladesh.planner.entity.TransportRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransportRouteRepository extends JpaRepository<TransportRoute, Long> {
    
    @Query("SELECT t FROM TransportRoute t WHERE " +
           "(t.sourceDestinationId = :source AND t.targetDestinationId = :target) OR " +
           "(t.sourceDestinationId = :target AND t.targetDestinationId = :source)")
    List<TransportRoute> findRoutesBetweenDestinations(
        @Param("source") Long source,
        @Param("target") Long target
    );
    
    @Query("SELECT t FROM TransportRoute t WHERE " +
           "t.sourceDestinationId = :source AND t.targetDestinationId = :target " +
           "AND t.transportType = :type")
    Optional<TransportRoute> findRouteBySourceTargetAndType(
        @Param("source") Long source,
        @Param("target") Long target,
        @Param("type") String type
    );
    
    @Query("SELECT t FROM TransportRoute t WHERE " +
           "t.sourceDestinationId = :source AND t.targetDestinationId = :target " +
           "ORDER BY t.costEconomyBdt ASC")
    List<TransportRoute> findCheapestRoutes(
        @Param("source") Long source,
        @Param("target") Long target
    );
    
    @Query("SELECT t FROM TransportRoute t WHERE " +
           "t.sourceDestinationId = :source AND t.targetDestinationId = :target " +
           "ORDER BY t.travelTimeHours ASC")
    List<TransportRoute> findFastestRoutes(
        @Param("source") Long source,
        @Param("target") Long target
    );
}
