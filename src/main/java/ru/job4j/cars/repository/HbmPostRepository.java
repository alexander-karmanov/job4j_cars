package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import ru.job4j.cars.model.Post;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
@AllArgsConstructor
public class HbmPostRepository implements PostRepository {

    private final CrudRepository crudRepository;

    public List<Post> findPostLastDay() {
        LocalDate today = LocalDate.now().minusDays(1);
        return crudRepository.query("FROM post WHERE created = :current_date",
                Post.class,
                Map.of("current_date", today));
    }

    public List<Post> findPostWithPhoto(String photoPath) {
        return crudRepository.query("FROM post WHERE photo_path is NOT NULL",
                Post.class,
                Map.of("photo_path", photoPath));
    }

    public List<Post> findPostByCarBrand(String brand) {
        return crudRepository.query("FROM post WHERE brand = :brand",
                Post.class,
                Map.of("brand", brand));
    }
}
