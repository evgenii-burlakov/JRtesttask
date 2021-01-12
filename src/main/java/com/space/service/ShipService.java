package com.space.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.space.model.Ship;
import com.space.model.ShipType;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Optional;

public interface ShipService {
    Ship create (ObjectNode json) throws InvalidParameterException;
    Ship read(int id);
    int getShipsCount(@RequestParam Optional<String> name,
                      @RequestParam Optional<String> planet,
                      @RequestParam Optional<Long> after,
                      @RequestParam Optional<Long> before,
                      @RequestParam Optional<Integer> minCrewSize,
                      @RequestParam Optional<Integer> maxCrewSize,
                      @RequestParam Optional<Double> minSpeed,
                      @RequestParam Optional<Double> maxSpeed,
                      @RequestParam Optional<Double> minRating,
                      @RequestParam Optional<Double> maxRating,
                      @RequestParam Optional<ShipType> shipType,
                      @RequestParam Optional<Boolean> isUsed,
                      @RequestParam Optional<Integer> pageNumber,
                      @RequestParam Optional<Integer> pageSize,
                      @RequestParam Optional<String> order);
    boolean update(ObjectNode json, int id);
    boolean delete (int id);
    List<Ship> sortedByPage(List<Ship> ships, Optional<Integer> pageNumber, Optional<Integer> pageSize);
    List<Ship> readAll(Optional<String> name,
                       Optional<String> planet,
                       Optional<Long> after, Optional<Long> before,
                       Optional<Integer> minCrewSize,
                       Optional<Integer> maxCrewSize,
                       Optional<Double> minSpeed,
                       Optional<Double> maxSpeed,
                       Optional<Double> minRating,
                       Optional<Double> maxRating,
                       Optional<ShipType> shipType,
                       Optional<Boolean> isUsed,
                       Optional<Integer> pageNumber, Optional<Integer> pageSize,
                       Optional<String> order);
}
