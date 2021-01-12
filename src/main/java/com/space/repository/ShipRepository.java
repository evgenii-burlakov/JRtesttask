package com.space.repository;


import com.space.model.Ship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ShipRepository extends JpaRepository<Ship, Long> {

    List<Ship> findByNameIsContainingAndPlanetIsContainingAndProdDateIsBetweenAndCrewSizeIsBetweenAndSpeedIsBetweenAndRatingIsBetween(String name, String planet, Date after, Date before, int minCrewSize, int maxCrewSize, double minSpeed, double maxSpeed, double minRating, double maxRating);
}
