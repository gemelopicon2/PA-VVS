package es.udc.paproject.backend.model.entities;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SessionDao extends CrudRepository<Session, Long> {
    List<Session> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}