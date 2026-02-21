package es.udc.paproject.backend.model.entities;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomDao extends JpaRepository<Room, Long> {
    List<Room> findByName (String name);

    List<Room> findByNameContainingIgnoreCase(String name);

    List<Room> findByCapacityGreaterThanEqual(int minCapacity);

    List<Room> findByCapacityLessThanEqual(int maxCapacity);

    List<Room> findByCapacityBetween(int minCapacity, int maxCapacity);

    List<Room> findAllByOrderByNameAsc();

    List<Room> findAllByOrderByCapacityDesc();
}