package com.TeamDeadlock.ExploreBangladesh.repository;

import com.TeamDeadlock.ExploreBangladesh.entity.TravelPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TravelPlanEntityRepository extends JpaRepository<TravelPlanEntity, String> {

    List<TravelPlanEntity> findByBudgetTierIgnoreCase(String budgetTier);

    List<TravelPlanEntity> findByDivisionIgnoreCase(String division);
}
