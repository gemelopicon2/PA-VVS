package es.udc.paproject.backend.model.entities;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomDao extends CrudRepository<Room, Long> {
}