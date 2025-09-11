package ru.job4j.cars.repository;

import ru.job4j.cars.model.Car;

import java.util.List;
import java.util.Optional;

public interface CarRepository {
    List<Car> getAll();

    Optional<Car> findCarById(Long id);

    Car saveCar(Car car);
}
