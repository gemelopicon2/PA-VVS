package es.udc.paproject.backend.model.entities;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieDao extends JpaRepository<Movie, Long> {
    List<Movie> findByTitle(String title);

    List<Movie> findByDurationLessThanEqual(int maxDuration);

    List<Movie> findAllByOrderByTitleAsc();

    List<Movie> findByDurationBetween(int minDuration, int maxDuration);

}