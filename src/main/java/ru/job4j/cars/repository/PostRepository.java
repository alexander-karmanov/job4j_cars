package ru.job4j.cars.repository;

import ru.job4j.cars.model.Post;

import java.util.List;

public interface PostRepository {

    List<Post> findPostLastDay();

    List<Post> findPostWithPhoto(String photoPath);

    List<Post> findPostByCarBrand(String brand);

}
