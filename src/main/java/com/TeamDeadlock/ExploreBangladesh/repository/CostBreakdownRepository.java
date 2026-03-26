package com.TeamDeadlock.ExploreBangladesh.repository;

import com.TeamDeadlock.ExploreBangladesh.entity.CostBreakdownEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CostBreakdownRepository extends JpaRepository<CostBreakdownEntity, Long> {

    List<CostBreakdownEntity> findByTravelPlanId(String planId);
}
