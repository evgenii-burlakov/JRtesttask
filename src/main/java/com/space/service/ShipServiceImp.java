package com.space.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.*;

@Service
public class ShipServiceImp implements ShipService {

    private final ShipRepository shipRepository;

    @Autowired
    public ShipServiceImp(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    @Override
    public Ship create(ObjectNode json) throws InvalidParameterException{
        try {
            String name = json.get("name").asText();
            String planet = json.get("planet").asText();
            ShipType shipType = ShipType.valueOf(json.get("shipType").asText());
            long prodDate = json.get("prodDate").asLong();
            boolean isUsed;
            if (json.get("isUsed") == null || json.get("isUsed").asText().equals("")) isUsed = false;
            else isUsed = json.get("isUsed").asBoolean();
            double speed = BigDecimal.valueOf(json.get("speed").asDouble()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            int crewSize = json.get("crewSize").asInt();

            if (name.equals("") || name.length() > 50 ||
                    planet.equals("") || planet.length() > 50 ||
                    prodDate < 0 ||
                    speed < 0.01 || speed > 0.99 ||
                    crewSize < 1 || crewSize > 9999) throw new InvalidParameterException();

            Date date = new Date(prodDate);
            int year = date.getYear() + 1900;
            if (year < 2800 || year > 3019) throw new InvalidParameterException();

            double rating = ratingCalculations(isUsed, speed, year);

            Ship ship = new Ship(name, planet, shipType, date, isUsed, speed, crewSize, rating);
            return shipRepository.save(ship);

        } catch (NullPointerException e) {
            throw new InvalidParameterException();
        }
    }

    @Override
    public List<Ship> readAll(@RequestParam Optional<String> name,
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

        long afterLong = after.map(aLong -> aLong - 3153602058L).orElse(1L);

        List<Ship> ships = shipRepository.findByNameIsContainingAndPlanetIsContainingAndProdDateIsBetweenAndCrewSizeIsBetweenAndSpeedIsBetweenAndRatingIsBetween(name.orElse(""), planet.orElse(""), new Date(afterLong), new Date(before.orElse(33104038201989L)), minCrewSize.orElse(0), maxCrewSize.orElse(9999), minSpeed.orElse(0.01), maxSpeed.orElse(0.99), minRating.orElse(0.0), maxRating.orElse(80.0));
        if (shipType.isPresent()) {
            for (int i = ships.size() - 1; i > -1; i--) {
                if (!ships.get(i).getShipType().equals(shipType.get())) {
                    ships.remove(i);
                }
            }
        }
        if (isUsed.isPresent()) {
            for (int i = ships.size() - 1; i > -1; i--) {
                if (!ships.get(i).isUsed() == isUsed.get()) {
                    ships.remove(i);
                }
            }
        }

        if (order.isPresent()) {
            if (order.get().equals("ID")) {
                ships.sort(new Comparator<Ship>() {
                    @Override
                    public int compare(Ship o1, Ship o2) {
                        if (o1.getId() - o2.getId() > 0) return 1;
                        if (o1.getId() - o2.getId() < 0) return -1;
                        else return 0;
                    }
                });
            } else if (order.get().equals("SPEED")) {
                ships.sort(new Comparator<Ship>() {
                    @Override
                    public int compare(Ship o1, Ship o2) {
                        if (o1.getSpeed() - o2.getSpeed() > 0) return 1;
                        if (o1.getSpeed() - o2.getSpeed() < 0) return -1;
                        else return 0;
                    }
                });
            } else if (order.get().equals("DATE")) {
                ships.sort(new Comparator<Ship>() {
                    @Override
                    public int compare(Ship o1, Ship o2) {
                        if (o1.getProdDate().getTime() - o2.getProdDate().getTime() > 0) return 1;
                        if (o1.getProdDate().getTime() - o2.getProdDate().getTime() < 0) return -1;
                        else return 0;
                    }
                });
            } else if (order.get().equals("RATING")) {
                ships.sort(new Comparator<Ship>() {
                    @Override
                    public int compare(Ship o1, Ship o2) {
                        if (o1.getRating() - o2.getRating() > 0) return 1;
                        if (o1.getRating() - o2.getRating() < 0) return -1;
                        else return 0;
                    }
                });
            }
        }
        return ships;
    }

    @Override
    public Ship read(int id) {
        try {
            return shipRepository.findById((long) id).orElseThrow(IndexOutOfBoundsException::new);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public int getShipsCount(@RequestParam Optional<String> name,
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
        return readAll(name, planet, after, before, minCrewSize, maxCrewSize, minSpeed, maxSpeed, minRating, maxRating, shipType, isUsed, pageNumber, pageSize, order).size();
    }

    @Override
    public boolean update(ObjectNode json, int id) {
        if (json.isEmpty()) return true;
        Ship ship = shipRepository.findById((long) id).orElseThrow(NoSuchElementException::new);
        try {
            String name = json.get("name").asText();
            if (!name.equals("") && name.length() < 51) ship.setName(name);
            else return false;
        } catch (NullPointerException e) {
        }

        try {
            String planet = json.get("planet").asText();
            if (!planet.equals("") && planet.length() < 51) ship.setPlanet(planet);
            else return false;
        } catch (NullPointerException e) {
        }

        try {
            ShipType shipType = ShipType.valueOf(json.get("shipType").asText());
            ship.setShipType(shipType);
        } catch (NullPointerException e) {
        }

        try {
            long prodDate = json.get("prodDate").asLong();
            if (prodDate > 0) {
                Date date = new Date(prodDate);
                int year = date.getYear() + 1900;
                if (year > 2799 && year < 3020) ship.setProdDate(date);
                else return false;
            } else return false;
        } catch (NullPointerException e) {
        }

        try {
            double speed = BigDecimal.valueOf(json.get("speed").asDouble()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            if (speed >= 0.01 && speed <= 0.99) ship.setSpeed(speed);
            else return false;
        } catch (NullPointerException e) {
        }

        try {
            int crewSize = json.get("crewSize").asInt();
            if (crewSize >= 1 && crewSize <= 9999) ship.setCrewSize(crewSize);
            else return false;
        } catch (NullPointerException e) {
        }

        try {
            if (!json.get("isUsed").asText().equals("")) {
                boolean isUsed = json.get("isUsed").asBoolean();
                ship.setUsed(isUsed);
            }
        } catch (NullPointerException e) {
        }

        ship.setRating(ratingCalculations(ship.isUsed(), ship.getSpeed(), ship.getProdDate().getYear() + 1900));
        shipRepository.saveAndFlush(ship);
        return true;
    }

    @Override
    public boolean delete(int id) {
        try {
            Optional<Ship> byId = shipRepository.findById((long) id);
            byId.ifPresent(shipRepository::delete);
            return byId.isPresent();
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    @Override
    public List<Ship> sortedByPage(List<Ship> ships, Optional<Integer> pageNumber, Optional<Integer> pageSize) {
        int size = ships.size();
        int page = pageNumber.orElse(0);
        int number = pageSize.orElse(3);
        List<Ship> finalShips = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (i >= page*number && i < page*number + number) {
                finalShips.add(ships.get(i));
            }
        }
        return finalShips;
    }

    private double ratingCalculations(boolean isUsed, double speed, int year) {
        double k;
        if (!isUsed) k = 1;
        else {
            k = 0.5;
        }
        double R = (double) 80 * speed * k / (3019 - year + 1);
        return BigDecimal.valueOf(R).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
