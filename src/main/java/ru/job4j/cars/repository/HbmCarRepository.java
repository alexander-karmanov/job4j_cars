package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import ru.job4j.cars.model.Car;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class HbmCarRepository implements CarRepository {

    private final CrudRepository crudRepository;

    @Override
    public List<Car> getAll() {
        return crudRepository.query(
                "FROM Car",
                Car.class
        );
    }

    @Override
    public Optional<Car> findCarById(Long id) {
        return crudRepository.optional(
                "FROM Car WHERE id = :id",
                Car.class,
                Map.of("id", id)
        );
    }

    @Override
    public Car saveCar(Car car) {
        try {
            crudRepository.run(session -> session.persist(car));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при сохранении", e);
        }
        return car;
    }
}
