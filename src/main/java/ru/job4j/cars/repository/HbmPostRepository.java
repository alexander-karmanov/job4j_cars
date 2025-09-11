package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import ru.job4j.cars.model.Post;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
@AllArgsConstructor
public class HbmPostRepository implements PostRepository {

    private final CrudRepository crudRepository;

    public List<Post> findPostLastDay() {
        LocalDateTime startOfYesterday = LocalDate.now().minusDays(1).atStartOfDay();
        LocalDateTime startOfToday = startOfYesterday.plusDays(1);

        return crudRepository.query(
                "FROM Post p WHERE p.created >= :startOfDay AND p.created < :startOfNextDay",
                Post.class,
                Map.of(
                        "startOfDay", startOfYesterday,
                        "startOfNextDay", startOfToday
                )
        );
    }

    public List<Post> findPostWithPhoto(String photoPath) {
        return crudRepository.query("FROM Post WHERE photo_path is NOT NULL",
                Post.class,
                Map.of("photo_path", photoPath));
    }

    public List<Post> findPostByCarBrand(String brand) {
        return crudRepository.query("FROM Post WHERE brand = :brand",
                Post.class,
                Map.of("brand", brand));
    }
}
