package es.udc.paproject.backend.model.entities;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SessionDao extends JpaRepository<Session, Long> {

    List<Session> findByMovie(Movie movie);

    List<Session> findByMovieId(Long movieId);

    List<Session> findByRoom(Room room);

    List<Session> findByRoomId(Long roomId);

    List<Session> findByDateAfter(LocalDateTime date);

    List<Session> findByDateBefore(LocalDateTime date);

    List<Session> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Session> findByMovieAndDateBetween(Movie movie, LocalDateTime startDate, LocalDateTime endDate);
    List<Session> findByRoomAndDateBetween(Room room, LocalDateTime startDate, LocalDateTime endDate);

    List<Session> findByPriceLessThanEqual(BigDecimal maxPrice);
    List<Session> findByPriceGreaterThanEqual(BigDecimal minPrice);

    List<Session> findByMovieAndRoom(Movie movie, Room room);

    Optional<Session> findByMovieAndRoomAndDate(Movie movie, Room room, LocalDateTime date);

    List<Session> findAllByOrderByDateAsc();
    List<Session> findAllByOrderByDateDesc();


}