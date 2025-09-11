package ru.job4j.cars.repository;

import ru.job4j.cars.model.*;

import java.time.LocalDateTime;
import java.util.Collections;

public class TestDataFactory {
    private final CrudRepository crudRepository;

    public TestDataFactory(CrudRepository crudRepository) {
        this.crudRepository = crudRepository;
    }

    public Engine createAndSaveEngine(String name) {
        Engine engine = new Engine();
        engine.setName(name);
        crudRepository.run(session -> session.save(engine));
        return engine;
    }

    public User createAndSaveUser(String login, String password) {
        User user = new User();
        user.setLogin(login);
        user.setPassword(password);
        crudRepository.run(session -> session.save(user));
        return user;
    }

    public Owner createAndSaveOwner(String name, User user) {
        Owner owner = new Owner();
        owner.setName(name);
        owner.setUser(user);
        crudRepository.run(session -> session.save(owner));
        return owner;
    }

    public Car createCar(String name, Engine engine, Owner owner) {
        Car car = new Car();
        car.setName(name);
        car.setEngine(engine);
        car.setOwner(owner);
        car.setHistoryOwners(Collections.emptySet());
        return car;
    }

    public Post createAndSavePost(String description, LocalDateTime created) {
        Post post = new Post();
        post.setDescription(description);
        post.setCreated(created);
        crudRepository.run(session -> session.save(post));
        return post;
    }

}
