package es.udc.paproject.backend.model.entities;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;

@Repository
public interface MovieDao extends JpaRepository<Movie, Long> {
    List<Movie> findByTitle(String title);

    List<Movie> findByDurationLessThanEqual(int maxDuration);

    List<Movie> findAllByOrderByTitleAsc();

    List<Movie> findByDurationBetween(int minDuration, int maxDuration);

    Page<Movie> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    @Query("SELECT DISTINCT m FROM Movie m JOIN m.sessions s WHERE s.date > :date")
    Page<Movie> findDistinctBySessionsDateAfter(LocalDateTime date, Pageable pageable);
}