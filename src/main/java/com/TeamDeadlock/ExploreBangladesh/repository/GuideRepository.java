package com.TeamDeadlock.ExploreBangladesh.repository;

import com.TeamDeadlock.ExploreBangladesh.entity.GuideEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuideRepository extends JpaRepository<GuideEntity, String> {

    @Query("SELECT DISTINCT g FROM GuideEntity g LEFT JOIN FETCH g.languages WHERE LOWER(g.city) = LOWER(:city)")
    List<GuideEntity> findByCityIgnoreCase(@Param("city") String city);

    @Query("SELECT DISTINCT g FROM GuideEntity g LEFT JOIN FETCH g.languages")
    List<GuideEntity> findAllWithLanguages();

    @Query("SELECT DISTINCT g.city FROM GuideEntity g ORDER BY g.city")
    List<String> findDistinctCities();
}
