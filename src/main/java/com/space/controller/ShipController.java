package com.space.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidParameterException;
import java.util.*;

@Controller
public class ShipController {
    private final ShipService shipService;

    @Autowired
    public ShipController(ShipService shipService) {
        this.shipService = shipService;
    }

    @PostMapping(value = "/rest/ships")
    public ResponseEntity<Ship> create(@RequestBody ObjectNode json) {
        try {
            final Ship create = shipService.create(json);
            return ResponseEntity.ok(create);
        }
        catch (InvalidParameterException e) {}
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/rest/ships")
    public ResponseEntity<List<Ship>> readAll(@RequestParam Optional<String> name,
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
                                              @RequestParam Optional<String> order) {
        final List<Ship> ships = shipService.readAll(name, planet, after, before, minCrewSize, maxCrewSize, minSpeed, maxSpeed, minRating, maxRating, shipType, isUsed, pageNumber, pageSize, order);
        List<Ship> finalShips = shipService.sortedByPage(ships, pageNumber, pageSize);
        return new ResponseEntity<>(finalShips, HttpStatus.OK);
    }

    @GetMapping(value = "/rest/ships/{id}")
    public ResponseEntity<Ship> read(@PathVariable(name = "id") int id) {
        if (id == 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        final Ship ship = shipService.read(id);

        return ship != null
                ? new ResponseEntity<>(ship, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/rest/ships/count")
    public ResponseEntity<Integer> getShipsCount(@RequestParam Optional<String> name,
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
                                                 @RequestParam Optional<String> order) {
        int count = shipService.getShipsCount(name, planet, after, before, minCrewSize, maxCrewSize, minSpeed, maxSpeed, minRating, maxRating, shipType, isUsed, pageNumber, pageSize, order);

        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @PostMapping(value = "/rest/ships/{id}")
    public ResponseEntity<Ship> update(@PathVariable(name = "id") int id, @RequestBody ObjectNode json) {
        if (id < 1) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (shipService.read(id) == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        final boolean updated = shipService.update(json, id);

        return updated
                ? new ResponseEntity<>(shipService.read(id), HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping(value = "/rest/ships/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") int id) {
        if (id == 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        final boolean deleted = shipService.delete(id);

        return deleted
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
